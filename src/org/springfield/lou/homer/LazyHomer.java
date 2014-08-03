/* 
* LazyHomer.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of Lou, related to the Noterik Springfield project.
*
* Lou is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Lou is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Lou.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.springfield.lou.homer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.*;
import org.dom4j.*;
import org.springfield.lou.*;
import org.springfield.marge.Marge;
import org.springfield.mojo.http.HttpHelper;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * LazyHomer registers this service and holds the
 * configuration for this instance
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.homer
 *
 */
public class LazyHomer implements MargeObserver {
	
	private static Logger LOG = Logger.getLogger(LazyHomer.class);

	/** Noterik package root */
	public static final String PACKAGE_ROOT = "org.springfield";
	private static enum loglevels { all,info,warn,debug,trace,error,fatal,off; }
	public static String myip = "unknown";
	private static int port = -1;
	private static int bart_port = -1;
	private static String external_ipnumber = "";
	static String group = "224.0.0.0";
	static int ttl = 1;
	static boolean noreply = true;
	static Marge mojomarge;
	static LazyMarge marge;
	static LazyLou lazylou;
	static SmithersProperties selectedsmithers = null;
	private static String rootPath = null;
	private static LouServer serv;
	private static Map<String, SmithersProperties> smithers = new HashMap<String, SmithersProperties>();
	private static Map<String, LouProperties> lous = new HashMap<String, LouProperties>();
	private static LazyHomer ins;
	public static boolean local = true;
	private int retryCounter;
	static String role = "production";
	
	/**
	 * Initializes the configuration
	 */
	public void init(String r) {
		rootPath = r;
		retryCounter = 0;
		ins = this;
		initConfig();
		initLogger();
		
		try{
			InetAddress mip=InetAddress.getLocalHost();
			myip = ""+mip.getHostAddress();
		}catch (Exception e){
			System.out.println("Exception ="+e.getMessage());
		}
		LOG.info("Lou init service name = lou on ipnumber = "+myip+" on port "+port);
		marge = new LazyMarge();
		mojomarge = Marge.instance();
		//lazylou = new LazyLou();
		
		// lets watch for changes in the service nodes in smithers
		marge.addObserver("/domain/internal/service/lou/nodes/"+myip, ins);
		marge.addTimedObserver("/smithers/downcheck",6,this);
		new DiscoveryThread();	
	}
	
	public static void addSmithers(String ipnumber,String port,String mport,String role) {
		int oldsize = smithers.size();
		if (!(""+LazyHomer.getPort()).equals(mport)) {
			System.out.println("LOU EXTREEM WARNING CLUSTER COLLISION ("+LazyHomer.getPort()+") "+ipnumber+":"+port+":"+mport);
			return;
		}
		
		if (!role.equals(getRole())) {
			System.out.println("Ignored this smithers ("+ipnumber+") its "+role+" and not "+getRole()+" like us");
			return;
		}
		
		SmithersProperties sp = smithers.get(ipnumber);
		if (sp==null) {
			sp = new SmithersProperties();
			smithers.put(ipnumber, sp);
			sp.setIpNumber(ipnumber);
			sp.setPort(port);
			sp.setAlive(true); // since talking its alive 
			noreply = false; // stop asking (minimum of 60 sec, delayed)
			System.out.println("lou found smithers at = "+ipnumber+" port="+port+" multicast="+mport);
		} else {
			if (!sp.isAlive()) {
				sp.setAlive(true); // since talking its alive again !
				LOG.info("lou recovered smithers at = "+ipnumber);
			}
		}

		// so check if we are known 
		if (oldsize==0 && ins.checkKnown()) {
			// we are verified (has a name other than unknown) and status is on
			LouProperties mp = lous.get(myip);
			setLogLevel(mp.getDefaultLogLevel());
			if (mp!=null && mp.getStatus().equals("on")) {
				if (serv==null) serv = new LouServer();
				if (!serv.isRunning()) {
					
					LOG.info("This lou will be started (on startup)");
				}
			} else {
				if (serv.isRunning()) {
					//running = false;
				} else {
					LOG.info("This lou is not turned on, use smithers todo this for ip "+myip);
				}
			}
		}
	}

	public static LouProperties getMyLouProperties() {
		return lous.get(myip);
	}
	
	public static int getMyLouPosition() {
		int i = 0;
		for(Iterator<LouProperties> iter = lous.values().iterator(); iter.hasNext(); ) {
			LouProperties m = (LouProperties)iter.next();
			i++;
			if (m.getIpNumber().equals(myip)) return i;
		}
		return -1;
	}
	
	public static boolean inDeveloperMode() {
		LouProperties mp = lous.get(myip);
		return mp.getDeveloperMode();
	}
	
	public static int getNumberOfLous() {
		return lous.size();
	}
	
	private Boolean checkKnown() {
		String xml = "<fsxml><properties><depth>1</depth></properties></fsxml>";
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return false;
		String nodes = smithers.get("/domain/internal/service/lou/nodes",xml,"text/xml");
		
		boolean iamok = false;

		try { 
			boolean foundmynode = false;
			Document result = DocumentHelper.parseText(nodes);
			for(Iterator<Node> iter = result.getRootElement().nodeIterator(); iter.hasNext(); ) {
				Element child = (Element)iter.next();
				if (!child.getName().equals("properties")) {
					String ipnumber = child.attributeValue("id");
					String status = child.selectSingleNode("properties/status").getText();
					String name = child.selectSingleNode("properties/name").getText();


					// lets put all in our lou list
					LouProperties mp = lous.get(ipnumber);
					if (mp==null) {
						mp = new LouProperties();
						lous.put(ipnumber, mp);

					}
					
					mp.setIpNumber(ipnumber);
					mp.setName(name);
					mp.setStatus(status);
					mp.setDefaultLogLevel(child.selectSingleNode("properties/defaultloglevel").getText());
					mp.setPreferedSmithers(child.selectSingleNode("properties/preferedsmithers").getText());
					Node dn =  child.selectSingleNode("properties/developermode");
					if (dn!=null) {
						String dm = dn.getText();
						if (dm!=null && dm.equals("true")) {
							mp.setDeveloperMode(true);
						}
					}
					
					//System.out.println("DEVELOPER MODE = "+mp.getDeveloperMode());
					
					//System.out.println("comparing ip "+ipnumber+" with "+myip);					
					if (ipnumber.equals(myip)) {
						//System.out.println("ip was equal");
						foundmynode = true;
						retryCounter = 0;
						if (name.equals("unknown")) {
							LOG.info("This lou is not verified change its name, use smithers todo this for ip "+myip);
						} else {
							// so we have a name (verified) return true
							iamok = true;
						}
					} else {
						//System.out.println("ip was not equal!");
					}
				}	
			}
			if (!foundmynode) {
				if (retryCounter < 30) {
					//retry 30 times (= 5 min) to handle temp smithers downtime (eg daily restarts)
					retryCounter++;
				} else {
					LOG.info("LazyHomer : Creating my processing node "+LazyHomer.getSmithersUrl()  + "/domain/internal/service/lou/properties");
					String os = "unknown"; // we assume windows ?
					try{
						  os = System.getProperty("os.name");
					} catch (Exception e){
						LOG.error("LazyHomer : "+e.getMessage());
					}
					
					String newbody = "<fsxml>";
		        	newbody+="<nodes id=\""+myip+"\"><properties>";
		        	newbody+="<name>unknown</name>";
		        	newbody+="<status>off</status>";
		        	newbody+="<lastseen>"+new Date().getTime()+"</lastseen>";
		        	newbody+="<preferedsmithers>"+myip+"</preferedsmithers>";
		        	newbody+="<activesmithers>"+selectedsmithers.getIpNumber()+"</activesmithers>";

		        	// i know this looks weird but left it for future extentions
		        	if (isWindows()) {
		        		newbody+="<defaultloglevel>info</defaultloglevel>";
		        	} if (isMac()) {
		        		newbody+="<defaultloglevel>info</defaultloglevel>";
		        	} if (isUnix()) {
		        		newbody+="<defaultloglevel>info</defaultloglevel>";
		        	} else {
		        		newbody+="<defaultloglevel>info</defaultloglevel>";
		        	}
		        	newbody+="</properties></nodes></fsxml>";	
		        	smithers.put("/domain/internal/service/lou/properties",newbody,"text/xml");
				}
			}
		} catch (Exception e) {
			LOG.info("LazyHomer exception doc");
			e.printStackTrace();
		}
		return iamok;
	}

	public static void setLastSeen() {
		Long value = new Date().getTime();
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return;
		smithers.put("/domain/internal/service/lou/nodes/"+myip+"/properties/lastseen", ""+value, "text/xml");
	}
	
	public static void send(String method, String uri) {
		try {
			MulticastSocket s = new MulticastSocket();
			String msg = myip+" "+method+" "+uri;
			byte[] buf = msg.getBytes();
			DatagramPacket pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group), port);
			s.send(pack,(byte)ttl);
			s.close();
		} catch(Exception e) {
			System.out.println("LazyHomer error "+e.getMessage());
		}
	}
	
	public static Boolean up() {
		if (smithers==null) return false;
		return true;
	}
	
	public static String getSmithersIpNumber() {
		if (selectedsmithers!=null) {
			return selectedsmithers.getIpNumber();
		}
		return null;
	}
	
	public static String getSmithersUrl() {
		if (selectedsmithers==null) {
			for(Iterator<SmithersProperties> iter = smithers.values().iterator(); iter.hasNext(); ) {
				SmithersProperties s = (SmithersProperties)iter.next();
				if (s.isAlive()) {
					selectedsmithers = s;
				}
			}
		}
		return "http://"+selectedsmithers.getIpNumber()+":"+selectedsmithers.getPort()+"/smithers2";
	}
	
	public static String getBartUrl() {
		if (selectedsmithers==null) {
			for(Iterator<SmithersProperties> iter = smithers.values().iterator(); iter.hasNext(); ) {
				SmithersProperties s = (SmithersProperties)iter.next();
				if (s.isAlive()) {
					selectedsmithers = s;
				}
			}
		}
		return "http://"+selectedsmithers.getIpNumber()+":"+selectedsmithers.getPort()+"/bart";
	}
	
	public void remoteSignal(String from,String method,String url) {
		if (url.indexOf("/smithers/downcheck")!=-1) {
			for(Iterator<SmithersProperties> iter = smithers.values().iterator(); iter.hasNext(); ) {
				SmithersProperties sm = (SmithersProperties)iter.next();
				if (!sm.isAlive()) {
					LOG.info("One or more smithers down, try to recover it");
					LazyHomer.send("INFO","/domain/internal/service/getname");
				}
			}
		} else {
			// only one trigger is set for now so we know its for nodes :)
			if (ins.checkKnown()) {
				// we are verified (has a name other than unknown)		
				LouProperties mp = lous.get(myip);
				if (serv==null) serv = new LouServer();
				if (mp!=null && mp.getStatus().equals("on")) {
	
					if (!serv.isRunning()) { 
						LOG.info("This lou will be started");
						serv.init();
					}
					setLogLevel(mp.getDefaultLogLevel());
				} else {
					if (serv.isRunning()) {
						LOG.info("This lou will be turned off");
						serv.destroy();
					} else {
						LOG.info("This lou is not turned on, use smithers todo this for ip "+myip);
					}
				}
			}
		}
	}
	
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}
 
	public static boolean isMac() {
 		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("mac") >= 0);
 	}
 
	public static boolean isUnix() {
 		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
 	}

	
	/**
	 * get root path
	*/
	public static String getRootPath() {
		return rootPath;
	}
	
	private static void setLogLevel(String level) {
		Level logLevel = Level.INFO;
		Level oldlevel = LOG.getLogger(PACKAGE_ROOT).getLevel();
		switch (loglevels.valueOf(level)) {
			case all : logLevel = Level.ALL;break;
			case info : logLevel = Level.INFO;break;
			case warn : logLevel = Level.WARN;break;
			case debug : logLevel = Level.DEBUG;break;
			case trace : logLevel = Level.TRACE;break;
			case error: logLevel = Level.ERROR;break;
			case fatal: logLevel = Level.FATAL;break;
			case off: logLevel = Level.OFF;break;
		}
		if (logLevel.toInt()!=oldlevel.toInt()) {
			LOG.getLogger(PACKAGE_ROOT).setLevel(logLevel);
			LOG.info("logging level: " + logLevel);
		}
	}
	
	/**
	 * Initializes logger
	*/
    private void initLogger() {    	 
    	System.out.println("Initializing logging.");
    	
    	// get logging path
    	String logPath = LazyHomer.getRootPath().substring(0,LazyHomer.getRootPath().indexOf("webapps"));
		logPath += "logs/lou/lou.log";	

		try {
			// default layout
			Layout layout = new PatternLayout("%-5p: %d{yyyy-MM-dd HH:mm:ss} %c %x - %m%n");
			
			// rolling file appender
			DailyRollingFileAppender appender1 = new DailyRollingFileAppender(layout,logPath,"'.'yyyy-MM-dd");
			BasicConfigurator.configure(appender1);
			
			// console appender 
			ConsoleAppender appender2 = new ConsoleAppender(layout);
			BasicConfigurator.configure(appender2);
		}
		catch(IOException e) {
			System.out.println("LouServer got an exception while initializing the logger.");
			e.printStackTrace();
		}
		
		Level logLevel = Level.OFF;
		LOG.getRootLogger().setLevel(Level.OFF);
		LOG.getLogger(PACKAGE_ROOT).setLevel(logLevel);
		LOG.info("logging level: " + logLevel);
		
		LOG.info("Initializing logging done.");
    }
    
	private void initConfig() {
		System.out.println("Lou: initializing configuration.");
		
		// properties
		Properties props = new Properties();
		
		// new loader to load from disk instead of war file
		String configfilename = "/springfield/homer/config.xml";
		if (isWindows()) {
			configfilename = "c:\\springfield\\homer\\config.xml";
		}
		
		// load from file
		try {
			System.out.println("INFO: Loading config file from load : "+configfilename);
			File file = new File(configfilename);

			if (file.exists()) {
				props.loadFromXML(new BufferedInputStream(new FileInputStream(file)));
			} else { 
				System.out.println("FATAL: Could not load config "+configfilename);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// only get the marge communication port unless we are a smithers
		port = Integer.parseInt(props.getProperty("marge-port"));
		
		bart_port = Integer.parseInt(props.getProperty("default-bart-port"));
		external_ipnumber = props.getProperty("external-ipnumber");	
		
		role = props.getProperty("role");
		if (role==null) role = "production";
		System.out.println("SERVER ROLE="+role);
	}

    /**
     * Shutdown
     */
	public static void destroy() {
		// destroy timer
		if (marge!=null) marge.destroy();
	}
	
	private class DiscoveryThread extends Thread {
	    DiscoveryThread() {
	      super("dthread");
	      start();
	    }

	    public void run() {
	     int counter = 0;
	      while (LazyHomer.noreply || counter<10) {
	    	if (counter>4 && LazyHomer.noreply) LOG.info("Still looking for smithers on multicast port "+port+" ("+LazyHomer.noreply+")");
	    	LazyHomer.send("INFO","/domain/internal/service/getname");
	        try {
	          sleep(500+(counter*100));
	          counter++;
	        } catch (InterruptedException e) {
	          throw new RuntimeException(e);
	        }
	      }
	      LOG.info("Stopped looking for new smithers");
	    }
	}

	public static int getPort() {
		return port;
	}

	public static int getBartPort() {
		return bart_port;
	}
	
	public static String getExternalIpNumber() {
		return external_ipnumber;
	}
	
	public static String getRole() {
		return role;
	}
	
	public static  boolean isSelectedSmithers() {
		if (selectedsmithers!=null) {
			return true;
		} else {
			return false;
		}
	}	
}
