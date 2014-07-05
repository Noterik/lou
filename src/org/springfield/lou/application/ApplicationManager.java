/* 
* ApplicationManager.java
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

package org.springfield.lou.application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.lou.application.components.types.OpenappsComponent;
import org.springfield.lou.application.types.DashboardApplication;
import org.springfield.lou.fs.FSList;
import org.springfield.lou.fs.FsNode;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.maggie.MaggieLoader;

/**
 * Application manager
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application
 *
 */
public class ApplicationManager extends Thread {
	
	private static Map<String, Html5ApplicationInterface> runningapps = new HashMap<String, Html5ApplicationInterface>();
	private static Map<String, Html5AvailableApplication> availableapps = null;
	private static ApplicationManager instance;
	private static OpenappsComponent oac;
	private static boolean running = false;
	private static Map<Integer, Html5ApplicationInterface> externalInterfaces = new HashMap<Integer, Html5ApplicationInterface>();
	private static Map<String, Html5ApplicationInterface> router = new HashMap<String, Html5ApplicationInterface>();
	private static Map<String, String> externalMessages = new HashMap<String, String>();
    private static FSList logcollection = null;
    
	private ApplicationManager() {
        System.out.println("Application Manager started");
		if (!running) {
			running = true;
			if (availableapps==null) loadAvailableApps(); // new test for urlmapping
			start();
            new MaggieLoader();

		}
    }
    
    public static ApplicationManager instance(){
    	if(instance==null) instance = new ApplicationManager();
    	return instance;
    }
    
    public void addApplication(Html5ApplicationInterface app) {
    	// for now its just one app not static/instance based yet
    	//System.out.println("ADDAPPLICATION="+app.getFullId()+" ID="+app.getId());
    	runningapps.put(app.getId(),app);
    	update();
    }
    
    public void removeApplication(String id){
    	if(this.getApplication(id)!=null)this.externalInterfaces.remove(this.getApplication(id).getExternalInterfaceId());
    	this.runningapps.remove(id);
    	update();
    }    
    
    public int getEternalInterfaceNumber(){
    	Random generator = new Random(System.currentTimeMillis());
    	
    	Integer rand=-1;
    	while(rand<0 || externalInterfaces.containsKey(rand)){
    		rand = generator.nextInt(9000) + 1000;
    	}    	
    	return rand;   	
    }
    
    public Map<String, String> getExternalMessages(){
    	return this.externalMessages;
    }
    
    public void addExternalInterface(Integer id, Html5Application app){
    	this.externalInterfaces.put(id, app);
    }
    
    public Map<Integer, Html5ApplicationInterface> getExternalInterfaces(){
    	return this.externalInterfaces;
    }
    
    public void removeExternalInterface(Integer id){
    	this.externalInterfaces.remove(id);
    }
    
    public Html5ApplicationInterface getApplication(String name) {
    		return runningapps.get(name);
    }
    
    public Html5ApplicationInterface getLoadedApplication(String name) {
    	//System.out.println("DYNAMIC LOADING WANTED FOR "+name);
    	Html5ApplicationInterface app=getApplication(name);
    	if (app!=null) return app;
    	
    	// so lets see what version is in production !
    	ApplicationClassLoader cl = new ApplicationClassLoader();
    	try {
    		String productionid = getProductionId(name);
    		if (productionid==null) return null;
    		cl.setJarName(name,productionid);
			Object o = cl.loadClass(name).getConstructor(String.class).newInstance(name);
			Html5ApplicationInterface newapp = (Html5ApplicationInterface)o;
			newapp.setHtmlPath("/springfield/lou/apps/"+newapp.getAppname()+"/"+productionid+"/");
			// execute the first command list
			newapp.executeActionlist("init");
    		System.out.println("NEW APP="+newapp+" version="+productionid);
    		return newapp;
    	} catch(Exception e) {
    		System.out.println("ApplicationManager ");
    		e.printStackTrace();
    	}
    	return runningapps.get(name);
    }
    
    public String getProductionId(String appname) {
    	Html5AvailableApplication app = getAvailableApplicationByInstance(appname);
    	return app.getProductionVersion();
    }

    public Html5AvailableApplication getAvailableApplicationByInstance(String url) {
    	int pos = url.indexOf("html5application/");
    	if (pos!=-1) {
    		String name = url.substring(pos+17);
    		return getAvailableApplication(name);
    	}
    	return null;
    }
    
    public Html5AvailableApplication getAvailableApplication(String name) {
    	if (availableapps==null) loadAvailableApps();
    	return availableapps.get(name);
    }
    
    public Map<String, Html5ApplicationInterface> getRouter(){
    	return this.router;
    }    
    
    public Map<String, Html5ApplicationInterface> getApplications(){
    	return runningapps;
    }
    
    public Map<String, Html5AvailableApplication> getAvailableApplications(){
    	
    	if (availableapps==null) loadAvailableApps();
    	return availableapps;
    }    
    
    public static void setOpenappCallback(OpenappsComponent c) {
    	oac = c;
    }
    
    public static void update() {
    	if (oac!=null) oac.update();
    }
    
    public static void startLogger(String url) {
    		logcollection =  new FSList(url);
    }
    
    public static void log(Html5ApplicationInterface app,FsNode n) {
    		if (logcollection==null) return;
    		String url = logcollection.getPath();
    		if (app.getId().equals(url)) {
    			logcollection.addNode(n);
    			if (oac!=null) oac.logChange(logcollection);
    		}
    }
    
    
    public void upload(String appname) {
    	System.out.println("UPLOAD DONE FOR = "+appname);
    	// lets see what we have in our upload dir
    	//TODO: make this configurable or at least windows compatible
    	File uploaddir = new File("/springfield/lou/uploaddir");
    	if (uploaddir.isDirectory()) {
    		File[] files = uploaddir.listFiles();
    		if (files!=null) {
    			for (File uploadfile : files) {
    				System.out.println("UPLOAD FILE="+uploadfile.toString());
    				processUploadedWar(uploadfile,appname);
    		    }
    	 }
    	}
    	loadAvailableApps();
    }
    
    public void uploadnew() {
    	// lets see what we have in our upload dir
    	File uploaddir = new File("/springfield/lou/uploaddir");
    	if (uploaddir.isDirectory()) {
    		File[] files = uploaddir.listFiles();
    		if (files!=null) {
    			for (File uploadfile : files) {
    				System.out.println("UPLOAD FILE="+uploadfile.toString());
    				String filename = uploadfile.getName();
    				int pos = filename.indexOf("smt_");
    				if (pos!=-1) {
    					filename = filename.substring(pos+4);
    					pos = filename.indexOf("app.war");
        				System.out.println("POS2="+pos);
    					if (pos!=-1) {
    						filename = filename.substring(0,pos);
    						System.out.println("APPNAME FILE="+filename);
    						createNewAppEntry(filename);
    						processUploadedWar(uploadfile,filename);
    					}
    				}
    		    }
    		}
    	}
    }
    
    private void createNewAppEntry(String appname) {
		String writepath = "/domain/internal/service/lou/apps/";		
		
		// creare the node		
		String newbody = "<fsxml><properties></properties></fsxml>";
    	String postpath = writepath+appname+"/properties";
    	//System.out.println("RS="+LazyHomer.sendRequest("PUT",postpath,newbody,"text/xml"));
    }    
    
    private void processUploadedWar(File warfile,String wantedname) {
    	// lets first check some vitals to check what it is
    	String warfilename = warfile.getName();
    	if (warfilename.startsWith("smt_") && warfilename.endsWith("app.war")) {
    		// ok so filename checks out is smt_[name]app.war format
    		String appname = warfilename.substring(4, warfilename.length()-7);
    		if (wantedname.equals(appname)) {
    			// ok found file is the wanted file
    			// format "29-Aug-2013-16:55"
    			System.out.println("NEW VERSION OF "+appname+" FOUND INSTALLING");
    			Date now = new Date();
    			SimpleDateFormat df = new SimpleDateFormat("d-MMM-yyyy-HH:mm");
    			String datestring = df.format(now);
    			String writedir = "/springfield/lou/apps/"+appname+"/"+datestring;
    			
    			// create the node
	    	    Html5AvailableApplication vapp = getAvailableApplication(appname);
	        	String newbody ="<fsxml><properties></properties></fsxml>";	
	        	
				String result = LazyHomer.sendRequest("PUT","/domain/internal/service/lou/apps/"+appname+"/versions/"+datestring+"/properties",newbody,"text/xml");
    			System.out.println("RESULTD="+result);
    			// make all the dirs we need
    			File md = new File(writedir);
    			md.mkdirs();
    			md = new File(writedir+"/war");
    			md.mkdirs();
    			md = new File(writedir+"/jar");
    			md.mkdirs();
    			md = new File(writedir+"/components");
    			md.mkdirs();
    			md = new File(writedir+"/css");
    			md.mkdirs();
    			md = new File(writedir+"/libs");
    			md.mkdirs();
    			
    			try {
    				JarFile war = new JarFile(warfile);  
    				System.out.println("WARFILE="+war+" "+warfile);
    		
    				// ok lets first find the jar file !
    				 JarEntry entry = war.getJarEntry("WEB-INF/lib/smt_"+appname+"app.jar");  
    				 if (entry!=null) {
    					 byte[] bytes = readJarEntryToBytes(war.getInputStream(entry)); 
    					 writeBytesToFile(bytes,writedir+"/jar/smt_"+appname+"app.jar");
    				 }
    				 // unpack all in eddie dir
    				 Enumeration<JarEntry> iter = war.entries();  
    				 while (iter.hasMoreElements()) {  
    				       JarEntry lentry = iter.nextElement();
    				       //System.out.println("LI="+lentry.getName());
    				       String lname = lentry.getName();
    				       if (!lname.endsWith("/")) {
    				    	   int pos = lname.indexOf("/"+appname+"/");
    				    	   if (pos!=-1) {
    				    		   String nname = lname.substring(pos+appname.length()+2);
    				    		   System.out.println("LE="+nname);
    				    		   String dname = nname.substring(0,nname.lastIndexOf('/'));
    				    		   File de = new File(writedir+"/"+dname);
    				    		   de.mkdirs();
    		    				   byte[] bytes = readJarEntryToBytes(war.getInputStream(lentry)); 
    		    				   writeBytesToFile(bytes,writedir+"/"+nname);
    				    	   }
    				       }
    				 }
    				 war.close();
    				 File ren = new File("/springfield/lou/uploaddir/"+warfilename);
    				 File nen = new File(writedir+"/war/smt_"+appname+"app.war");
    				 System.out.println("REN="+warfilename);
    				 System.out.println("REN="+writedir+"/war/smt_"+appname+"app.war");
    				 System.out.println("MOVE FILE="+ren.renameTo(nen));
    				 
		    	     loadAvailableApps();
    				 // should we make in development or production based on autodeploy ?
		    	     vapp = getAvailableApplication(appname);
		    	     if (vapp!=null) {
		    	    	 System.out.println("AUTODEPLOY="+vapp.getAutoDeploy());
		    	    	 String mode = vapp.getAutoDeploy();
		    	    	 if (mode.equals("production")) {
		    	    		 makeProduction(appname, datestring);
		    	    	 } else if (mode.equals("development")) {
		    	    		 makeDevelopment(appname, datestring);
		    	    	 } else if (mode.equals("development/production")) {
		    	    		 makeDevelopment(appname, datestring);
		    	    		 makeProduction(appname, datestring);
		    	    	 }
		    	     }

		    	     Html5ApplicationInterface app = getApplication("/domain/webtv/html5application/dashboard");
    				 if (app!=null) {
    					DashboardApplication dapp = (DashboardApplication)app;
    					dapp.newApplicationFound(appname);
    				 }
    				 
    			} catch(Exception e) {
    				e.printStackTrace();
    			}    	
    		}
    	}
    }
    
    public void makeProduction(String appname,String version) {
    	// first we should change smithers and signal the others
    	
    	// change in memory
    	Html5AvailableApplication avapp = getAvailableApplication(appname);
    	if (avapp!=null) {
    		String oldname = avapp.getProductionVersion();
    		Html5AvailableApplicationVersion oldv = avapp.getVersion(oldname);
    		if (oldv!=null) {
    			oldv.setProductionState(false);
    		}
    		Html5AvailableApplicationVersion newv = avapp.getVersion(version);
    		if (newv!=null) {
    			newv.setProductionState(true);
    		}
    		
    		//TODO: make this configurable or at least windows compatible
    		String source = "/springfield/lou/apps/"+appname+"/"+version;
    		String target = "/springfield/tomcat/webapps/ROOT/eddie/apps/"+appname;
    		
    		//System.out.println("SO="+source);
    		//System.out.println("TA="+target);
    		try {
    			// create dir if needed
    			Runtime.getRuntime().exec("/bin/mkdir "+target);
    			
        		// delete symlinks if available
    			Runtime.getRuntime().exec("/bin/rm "+target+"/css");
    			Runtime.getRuntime().exec("/bin/rm "+target+"/libs");
    			Runtime.getRuntime().exec("/bin/rm "+target+"/img");
    			
    			// create the sym links
    			Runtime.getRuntime().exec("/bin/ln -s "+source+"/css "+target+"/css");
    			Runtime.getRuntime().exec("/bin/ln -s "+source+"/libs "+target+"/libs");
    			Runtime.getRuntime().exec("/bin/ln -s "+source+"/img "+target+"/img");
    		} catch(Exception e) {
    			System.out.println("Can't create symlink");
    			e.printStackTrace();
    		}

        	// we need to unload the old one from memory
    		avapp.deleteCaches();
    	} else {
    		System.out.println("Appliction not found "+appname);
    	}
    	
    	// we need to unload the old one from memory
		for(Iterator<String> iter = runningapps.keySet().iterator(); iter.hasNext(); ) {
			String appn = (String)iter.next();
			if (appn.indexOf("html5application/"+appname)!=-1) {
				Html5ApplicationInterface rapp = runningapps.get(appn);
				System.out.println("SHUTDOWN OLD APP="+rapp.getId());
				rapp.shutdown();
				runningapps.remove(appn);
			}
		}
    	// load the new one, will be done on next usage !
		purgeOldVersions();
    }
    
    public void makeDevelopment(String appname,String version) {
    	// change in memory
    	Html5AvailableApplication avapp = getAvailableApplication(appname);
    	if (avapp!=null) {
    		String oldname = avapp.getDevelopmentVersion();
    		Html5AvailableApplicationVersion oldv = avapp.getVersion(oldname);
    		if (oldv!=null) {
    			oldv.setDevelopmentState(false);
    		}
    		Html5AvailableApplicationVersion newv = avapp.getVersion(version);
    		if (newv!=null) {
    			newv.setDevelopmentState(true);
    		}
    		avapp.deleteCaches();
    	} else {
    		System.out.println("Appliction not found "+appname);
    	}
    	
    	// we need to unload the old one from memory
		avapp.deleteCaches();
		
    	// we need to unload the old one from memory
		for(Iterator<String> iter = runningapps.keySet().iterator(); iter.hasNext(); ) {
			String appn = (String)iter.next();
			if (appn.indexOf("html5application/"+appname)!=-1) {
				runningapps.remove(appn);
			}
		}
    	// load the new one, will be done on next usage !
    }
    
    public void deleteVersion(String appname,String version) {
    	// change in memory
    	Html5AvailableApplication avapp = getAvailableApplication(appname);
    	if (avapp!=null) {
    		avapp.deleteVersion(version);
    	} else {
    		System.out.println("Appliction not found "+appname);
    	}
    }
    
    public void setAutoDeploy(String appname,String mode) {
    	// change in memory
    	Html5AvailableApplication avapp = getAvailableApplication(appname);
    	if (avapp!=null) {
    		avapp.setAutoDeploy(mode);
    	} else {
    		System.out.println("Appliction not found "+appname);
    	}
    }

    public void loadAvailableApps() {
    	//System.out.println("ApplicationManager.loadAvailableApps()");
    	availableapps = new HashMap<String, Html5AvailableApplication>();
    	String xml = "<fsxml><properties><depth>2</depth></properties></fsxml>";
		long starttime = new Date().getTime(); // we track the request time for debugging only
    	String nodes = LazyHomer.sendRequest("GET","/domain/internal/service/lou/apps",xml,"text/xml");
    	//System.out.println("APP NODECOUNT="+nodes.length());
		long endtime = new Date().getTime(); // we track the request time for debugging only
		//System.out.println("SMITHERSTIME="+(endtime-starttime));
		try { 
			Document result = DocumentHelper.parseText(nodes);
			for(Iterator<Node> iter = result.getRootElement().nodeIterator(); iter.hasNext(); ) {
				Element child = (Element)iter.next();
				if (!child.getName().equals("properties")) {
					String id = child.attributeValue("id");
					//System.out.println("AVAIL="+id);
			    	Html5AvailableApplication vapp = new Html5AvailableApplication();
					//System.out.println("N0");
			    	vapp.setId(id);
					//System.out.println("N1");
	
			    	// get all the versions and nodes
			    	String production = null;
			    	String development = null;
					for(Iterator<Node> iter2 = child.nodeIterator(); iter2.hasNext(); ) {
						//System.out.println("N2");
						Node node = iter2.next();
						if(node instanceof Element){
							Element child2 = (Element)node;	
							String nname = child2.getName();
							if (nname.equals("properties")) {
								//System.out.println("N3");
								for(Iterator<Node> iter3 = child2.nodeIterator(); iter3.hasNext(); ) {
									Node node2 = iter3.next();
									if(node2 instanceof Element){
										//System.out.println("N4");
										Element child3 = (Element)node2;	
										String pname = child3.getName();
										if (pname.equals("autodeploy")) {
											vapp.loadAutoDeploy(child3.getText());
										}
									}
								}
							} else if (nname.equals("versions")) {
								//System.out.println("N5");
								String version = child2.attributeValue("id");
						    	Html5AvailableApplicationVersion v = new Html5AvailableApplicationVersion(vapp);
						    	v.setId(version);
						    	vapp.addVersion(v);
								//System.out.println("N5.1");
						    	
							} else if (nname.equals("nodes")) {
								//System.out.println("N6");
								String ipnumber = child2.attributeValue("id");
								String ipversion = child2.attributeValue("referid");
							} else if (nname.equals("production")) {	
								//System.out.println("N7");
								production = child2.attributeValue("referid");
							} else if (nname.equals("development")) {
								//System.out.println("N7");
								development = child2.attributeValue("referid");
							}
							//System.out.println("N5.2");
							// ok lets set prod/dev version
							if (production!=null) {
								Html5AvailableApplicationVersion pv = vapp.getVersionByUrl(production);
								if (pv!=null) {
									pv.setProductionState(true);
									// lets scan for triggers !
									String scanpath="/springfield/lou/apps/"+vapp.getId()+"/"+pv.getId()+"/actionlists/";
									//System.out.println("ACTIONLIST PRESCANNER="+scanpath);
									ActionListManager.readActionListsDirForUrlTriggers(scanpath);
								}
							}
							//System.out.println("N5.2");
							if (development!=null) {
								Html5AvailableApplicationVersion dv = vapp.getVersionByUrl(development);
								if (dv!=null) dv.setDevelopmentState(true);
							}
							//System.out.println("N5.3");
						}else{
							System.out.println("NOT AN ELEMENT!");
							System.out.println(node);
						}
						//System.out.println("N99");
					}				
					//System.out.println("AVAIL PUT="+id);
			    		availableapps.put(id, vapp);
			    	
			    	// parse it again to get the nodes, don't like it but simplest way
					for(Iterator<Node> iter2 = child.nodeIterator(); iter2.hasNext(); ) {
						Element child2 = (Element)iter2.next();	
						String nname = child2.getName();
						if (nname.equals("nodes")) {
							String ipnumber = child2.attributeValue("id");
							String ipversion = child2.attributeValue("referid");
							//System.out.println("IP="+ipnumber+" VER="+ipversion);
							Html5AvailableApplicationVersion vv = vapp.getVersionByUrl(ipversion);
							if (vv!=null) {
								vv.addNode(ipnumber);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			System.out.println("Application manager : ");
			e.printStackTrace();
		}
    	if (availableapps.size()==0) {
    		System.out.println("NO APPS FOUND RETURNING NULL");
    		availableapps=null;
    	}
    	//System.out.println("END OF LOADAVAIL");
     }
    
    private byte[] readJarEntryToBytes(InputStream is) {  
    	try {
    		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();  
    		int nextValue = is.read();  
    		while (-1 != nextValue) {  
        		byteStream.write(nextValue);  
        		nextValue = is.read();  
    		}  
    		byte[] bytes = byteStream.toByteArray(); 
    		return bytes;
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
	public void run() {
		while (running) {
			try {
				uploadnew();
				//System.out.println("check new upload");
				Thread.sleep(500);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
    
    private void writeBytesToFile(byte[] bytes,String filename) {
    	try {
    		FileOutputStream stream = new FileOutputStream(filename);
    		try {
    			stream.write(bytes);
    		} finally {
    			stream.close();
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }   
    
    private void purgeOldVersions() {
		int shour = 3600;
		int sday = 24*3600;
		int smonth = 30*24*3600;
		int syear = 365*24*3600;
    		ArrayList<String> copies = null;
    		Long now = new Date().getTime()/1000;
		Set<String> keys = ApplicationManager.instance().getAvailableApplications().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = (String) it.next();
			System.out.println("PURGE APPNAME="+next);
			copies = new ArrayList<String>();
			Html5AvailableApplication vapp = ApplicationManager.instance().getAvailableApplication(next);
			Iterator<Html5AvailableApplicationVersion> it2 = vapp.getOrderedVersions();
			while(it2.hasNext()){
				Html5AvailableApplicationVersion version = it2.next();
				String dates = version.getId();
				// 13-May-2014-16:44
				try {
					DateFormat df = new SimpleDateFormat("dd-MMM-yyyy-HH:mm");
					Date date =  df.parse(dates);
					long then = date.getTime()/1000;
					long delta = now - then;
					String token = null;
					if (delta<shour) {
						// last hour keep all versions !
						token = "S"+delta;
					} else if (delta<(sday)) {
						// in the last 24 hours, keep one per hour max
						int mod = (int)(delta/shour);
						token = "H"+mod;
					} else if (delta<(smonth)) {
						// in the last 30 days, keep one per day max
						int mod = (int)(delta/sday);
						token = "D"+mod;
					} else if (delta<(syear)) {
						// in the year, keep one per month max
						int mod = (int)(delta/smonth);
						token = "M"+mod;
					} else {
						// keep one per year
						int mod = (int)(delta/syear);
						token = "Y"+mod;
					}
					// check if we already have one
					if (!copies.contains(token)) {
						copies.add(token);
						System.out.println(token+" NAME="+next+" DATE="+dates+" DELTA="+delta+" KEEP");
					} else {
						System.out.println(token+" NAME="+next+" DATE="+dates+" DELTA="+delta+" DELETE");
						if (!version.isDevelopmentVersion() && !version.isProductionVersion()) {
							vapp.deleteVersion(dates);
						}
					}
				} catch(Exception e) {
					System.out.println("PARSE PROBLEM ON DATE IN PURGE DATE = "+dates);
				}
				
			}
		}
    }
}
