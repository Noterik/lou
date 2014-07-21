/* 
* Screen.java
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

package org.springfield.lou.screen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.application.components.BasicComponent;
import org.springfield.lou.application.components.ComponentInterface;
import org.springfield.lou.application.components.ComponentManager;
import org.springfield.lou.application.components.types.AvailableappsComponent;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.location.Location;
import org.springfield.lou.tools.JavascriptInjector;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Screen
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.screen
 *
 */
public class Screen {
	
	private String id;
	private String shortid;
	private String role = "unknown";
	private Capabilities capabilities;
	private Html5ApplicationInterface app;
	private String data = null;
	private long lastseen = -1;
	private ComponentManager cm;
	private Location location;
	private String username = null;
	private Map<String, String[]> params;
	private Map<String, Object> properties;
	
	/**
	 * Constractor for Screen class
	 * @param id the desired id for this screen
	 * @param caps the capabilities object associated with this screen
	 */
	public Screen(Html5ApplicationInterface a,Capabilities caps,String id){
		this.id = id;
		this.capabilities = caps;
		int pos = id.indexOf("/screen/")+8;
		this.shortid=id.substring(pos);
		this.app = a;
		this.cm = new ComponentManager();
		this.properties = new HashMap<String, Object>();
		//this.capabilities = caps;
		//this.content = "";
		setSeen();
	}
	
	public void setParameters(Map<String,String[]> p) {
		params = p;
	}
	
	public String getParameter(String name) {
		String[] values = params.get(name);
		if (values!=null) {
			return values[0];
		} else {
			return null;
		}
	}
	
	public Html5ApplicationInterface getApplication() {
		return app;
	}
	
	public Map<String, String[]> getParameters() {
		return params;
	}
	
	public void setProperty(String key, Object value){
		properties.put(key, value);
	}
	
	public Object getProperty(String key){
		return properties.get(key);
	}
	
	public void setSeen() {
		lastseen = new Date().getTime();	
	}
	
	public void setRole(String r) {
		this.role = r;	
	}
	
	public String getRole() {
		return role;	
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setLocation(Location loc) {
		location = loc;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getShortId() {
		return this.shortid;
	}
	
	public String getUserName() {
		return username;
	}	
	
	public long getLastSeen() {
		return lastseen;
	}
	
	public void put(String from,String content) {
		app.putOnScreen(this,from, content);
	}

	/**
	 * Assigns capabilities the screen
	 * @param caps the capabilities object to be associated with this screen
	 */
	public void setCapabilities(Capabilities caps){
		this.capabilities = caps;
	}
	
	public Capabilities getCapabilities(){
		return capabilities;
	}
	
	/**
	 * Sets data to be sent to the screen
	 * @param data the data to be sent
	 */
	public void setContent(String t,String c){
		if (data==null) {
			data = "set("+t+")="+c;
		} else {
			data += "($end$)set("+t+")="+c;
		}
		synchronized (this) {
		    this.notify();
		}
	}
	
	/**
	 * Sets data to be sent to the screen
	 * @param data the data to be sent
	 */
	public void addContent(String t,String c){
		if (data==null) {
			data = "add("+t+")="+c;
		} else {
			data += "($end$)add("+t+")="+c;
		}
		synchronized (this) {
		    this.notify();
		}
	}
	
	public void setScript(String t,String c){
		
		if (data==null) {
			data = "setscript("+t+")="+c;
		} else {
			data += "($end$)setscript("+t+")="+c;
		}
		synchronized (this) {
		    this.notify();
		}
	}
	
	/**
	 * Sets data to be sent to the screen
	 * @param data the data to be sent
	 */
	public void removeContent(String t, Html5ApplicationInterface app){
		removeContent(t, false, app);
	}
	
	public void removeContent(String t){
		removeContent(t, false, getApplication());
	}
	
	public void removeContent(String t, boolean leaveElement, Html5ApplicationInterface app){
		if (data==null) {
			data = "remove("+t+"," + leaveElement + ")";
		} else {
			data += "($end$)remove("+t+"," + leaveElement + ")";
		}
		synchronized (this) {
		    this.notify();
		}
		
		app.removeComponentFromScreen(t, this);
	}
		
	/**
	 * gets the data for this screen, emptys the buffer and notifies the servlet
	 * that there are new data to be sent
	 * @return the data to be sent
	 */
	public String getMsg(){
		String dt = this.data;
		this.data = null;
		return dt;
	}
	
	public void putMsg(String t,String f,String c) {
		if (data==null) {
			data = "put("+t+")="+c;
		} else {
			data += "($end$)put("+t+")="+c;
		}
		synchronized (this) {
		    this.notify();	
		}
	}
	
	public void dropConnection() {
		data = null;
		synchronized (this) {
		    this.notify();	
		}
	}
	
	public ComponentManager getComponentManager(){
		return this.cm;
	}
	
	public void loadStyleSheet(String style, Html5ApplicationInterface app) {
		//TODO: make this at least windows compatible or configurable
		//System.out.println("Screen.loadStyleSheet(" + style + ", " + app + ")");
		String stylepath ="/springfield/tomcat/webapps/ROOT/eddie/"+style;
		
		String packagepath = app.getHtmlPath();
		if (packagepath!=null) {
			int pos = style.indexOf("/css/");
			if (pos!=-1) {
				stylepath = packagepath + style.substring(pos+1);
			}
		}
		//System.out.println("LOADING STYLE="+stylepath);
		if (style.equals("apps/dashboard/css/dashboardapp.css")) {
			stylepath="/springfield/tomcat/webapps/ROOT/eddie/apps/dashboard/css/generic.css";
		}
		Boolean failed = false;
//		stylepath ="C:\\\\springfield\\tomcat\\webapps\\ROOT\\eddie\\"+stylepath;
		StringBuffer str = null;
		try {
			str = new StringBuffer();
			BufferedReader br;
			br = new BufferedReader(new FileReader(stylepath));
			String line = br.readLine();
			while (line != null) {
				str.append(line);
				str.append("\n");
				line = br.readLine();
			 }
			br.close();
		} catch (FileNotFoundException e) {
			failed=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (failed) {
			stylepath ="/springfield/tomcat/webapps/ROOT/eddie/generic.css";
			
			packagepath = app.getHtmlPath();
			if (packagepath!=null) {
				int pos = style.indexOf("/css/");
				if (pos!=-1) {
					stylepath = packagepath + "css/generic.css";
				}
			}
		//	System.out.println("LOADING STYLE="+stylepath);
//			stylepath ="C:\\\\springfield\\tomcat\\webapps\\ROOT\\eddie\\"+stylepath;
			 str = null;
			try {
				str = new StringBuffer();
				BufferedReader br;
				br = new BufferedReader(new FileReader(stylepath));
				String line = br.readLine();
				while (line != null) {
					str.append(line);
					str.append("\n");
					line = br.readLine();
				 }
				br.close();
			} catch (FileNotFoundException e) {
				failed=true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
			
		String body = ""+ str.toString();
		String stylename = stylepath.substring(stylepath.lastIndexOf("/")+1, stylepath.indexOf(".css"));
		if(stylename.contains("_")) stylename = stylename.substring(0, stylename.indexOf("_"));
		if (data==null) {
			data = "setstyle(head)=" + stylename +"style,"+body;
		} else {
			data += "($end$)setstyle(head)="+ stylename +"style,"+body;
		}
		synchronized (this) {
		    this.notify();
		}
	}
	
	public void removeStyle(String style){
		
		if (data==null) {
			data = "removestyle("+style+"style)";
		} else {
			data += "($end$)removestyle("+style+"style)";
		}
		synchronized (this) {
		    this.notify();
		}
	}
	
	public void loadScript(String target,String scriptpath, Html5ApplicationInterface app) {
		// lets find out what is the active version for this app
		String basepath = "/springfield/tomcat/webapps/ROOT/eddie/";
		if (LazyHomer.isWindows()) basepath = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\";

		
		String filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+scriptpath;
		File file = new File(filename);
		if (!file.exists()) {
			// ok so not in the domain/app/component (step 1)
						
			filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"components"+File.separator+scriptpath;
			file = new File(filename);
			if (!file.exists()) {
				// ok also not in domain/component

				filename = basepath+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+scriptpath;
				file = new File(filename);
				if (!file.exists()) {
					// ok also not in app/component

					// so its in component
					filename = basepath+"components"+File.separator+scriptpath;
				}
			}
		}
		
		if(new File(filename).exists()){
			String touchBindingsXml = filename.substring(0, filename.lastIndexOf("\\")+1) + "bindings.xml";
//			System.out.println("checking for file: " + filename);
//			System.out.println("checking for bindings: " + touchBindingsXml);
			try {
				BufferedReader br = new BufferedReader(new FileReader(filename));
			
				StringBuffer str = new StringBuffer();
				String line = br.readLine();
				while (line != null) {
					str.append(line);
					str.append("\n");
					line = br.readLine();
				}
				br.close();
				
				String body = str.toString();
				body = JavascriptInjector.injectTryCatch(body, scriptpath);
				//if there is an bindings.xml file in the component directory
				//inject the Javascript with hammer.js events
				if(new File(touchBindingsXml).exists()){
					body = JavascriptInjector.injectTouchBindings(body, touchBindingsXml);
				}
				this.setScript(target, body);
			} catch (Exception e){
				e.printStackTrace();
			}
		}else {
			//System.out.println("File " +filename+ " does not exist");
		}
	}
	
	public void loadComponentScript(String target,String scriptpath, Html5ApplicationInterface app, String comp) {
		// lets find out what is the active version for this app
		String basepath = "/springfield/tomcat/webapps/ROOT/eddie/";
		if (LazyHomer.isWindows()) basepath = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\";

		String packagepath = app.getHtmlPath();
		String filename = null;
		if (packagepath!=null) {
			filename = packagepath + "components"+File.separator+scriptpath;
		} else {	
			filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+scriptpath;
			File file = new File(filename);
			if (!file.exists()) {
				// ok so not in the domain/app/component (step 1)
							
				filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"components"+File.separator+scriptpath;
				file = new File(filename);
				if (!file.exists()) {
					// ok also not in domain/component
	
					filename = basepath+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+scriptpath;
					file = new File(filename);
					if (!file.exists()) {
						// ok also not in app/component
	
						// so its in component
						filename = basepath+"components"+File.separator+scriptpath;
					}
				}
			}
		}
		
		// so lets see if we have a referid on this that overrides it ?
		String referid = app.getReferid(target);
		if (referid!=null) {
			// so create new filename based on it ? (example : /websiteserviceone/defaultoutput);
			if (referid.startsWith("/")) {
				String refappname = referid.substring(1);
				int pos = refappname.indexOf("/");
				String refcname = refappname.substring(pos+1);
				refappname = refappname.substring(0,pos);
				Html5AvailableApplication refapp = ApplicationManager.instance().getAvailableApplication(refappname);
				if (refapp!=null) {
					filename = "/springfield/lou/apps/"+refappname+File.separator+refapp.getProductionVersion()+File.separator+"components"+File.separator+refcname+File.separator+refcname+".js";
				}
			}
		}

		
		
		if(new File(filename).exists()){
			String touchBindingsXml = filename.substring(0, filename.lastIndexOf(LazyHomer.isWindows() ? "\\" : "/")+1) + "bindings.xml";
//			System.out.println("checking for file: " + filename);
//			System.out.println("checking for bindings: " + touchBindingsXml);
			try {
				BufferedReader br = new BufferedReader(new FileReader(filename));
			
				StringBuffer str = new StringBuffer();
				String line = br.readLine();
				while (line != null) {
					str.append(line);
					str.append("\n");
					line = br.readLine();
				}
				br.close();
				
				String body = str.toString();
				
				body = body.replace("$cname",target.substring(0,1).toUpperCase()+target.substring(1));
				
				body = JavascriptInjector.injectTryCatch(body, scriptpath);
				//if there is an bindings.xml file in the component directory
				//inject the Javascript with hammer.js events
				if(new File(touchBindingsXml).exists()){
					body = JavascriptInjector.injectTouchBindings(body, touchBindingsXml);
				}
				body = JavascriptInjector.injectComponentGlobalDefinitions(body, comp, target);
				this.setScript(target, body);
			} catch (Exception e){
				e.printStackTrace();
			}
		}else {
			//System.out.println("File " +filename+ " does not exist");
		}
	}
	
	public void onNewUser(String name) {
		username = name;
		//System.out.println("onNewUser="+name);
		app.onNewUser(this, name);
	}
	
	public void onLoginFail(String name) {
		app.onLoginFail(this, name);
	}
	
	public void onLogoutUser(String name) {
		username = null;
		//System.out.println("USERLOGOUT="+name);
		app.onLogoutUser(this, name);
	}
	
    /**
     * 
     * adds application id, checks with barney and talks to mojo if allowed
     * 
     * @param path
     * @return
     */
    public final FsNode getNode(String path) {
    	String asker = this.getUserName(); // gets the use name
    	if (asker!=null && !asker.equals("")) {
    		System.out.println("screen getNode "+asker);
    		ServiceInterface barney = ServiceManager.getService("barney");
    		if (barney!=null) {
    			String allowed = barney.get("userallowed(read,"+path+",0,"+asker+")",null,null);
    			if (allowed!=null && allowed.equals("true")) {
    				return Fs.getNode(path); // so its allowed ask it
    			}
    		}
    	}
    	return null;
    }
    
	
	public void log(String msg) {
		app.log(this,msg);
	}
	
	public void log(String msg,int level) {
		app.log(this,msg,level);
	}
	
	public void loadContent(String target,String ctype,Boolean overload, Html5ApplicationInterface app) {
		// lets find out what is the active version for this app
		String templatepath = app.getComponentManager().getComponentPath(ctype);
		
		String basepath = "/springfield/tomcat/webapps/ROOT/eddie/";
		if (LazyHomer.isWindows()) basepath = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\";
		
		String packagepath = app.getHtmlPath();
		String filename = null;
		if (packagepath!=null) {
			filename = packagepath + "components"+File.separator+templatepath;
		} else {
			
			filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+templatepath;
			File file = new File(filename);
			if (!file.exists()) {
				// ok so not in the domain/app/component (step 1)
							
				filename = basepath+"domain"+File.separator+app.getDomain()+File.separator+"components"+File.separator+templatepath;
				file = new File(filename);
				if (!file.exists()) {
					// ok also not in domain/component
	
					filename = basepath+"apps"+File.separator+app.getAppname()+File.separator+"components"+File.separator+templatepath;
					file = new File(filename);
					if (!file.exists()) {
						// ok also not in app/component
	
						// so its in component
						filename = basepath+"components"+File.separator+templatepath;
					}
				}
			}
		}
		
		// the above part should be redone we don't support overriding like that anymore (daniel) ?
		
		// so lets see if we have a referid on this that overrides it ?
		String referid = app.getReferid(ctype);
		if (referid!=null) {
			// so create new filename based on it ? (example : /websiteserviceone/defaultoutput);
			if (referid.startsWith("/")) {
				String refappname = referid.substring(1);
				int pos = refappname.indexOf("/");
				String refcname = refappname.substring(pos+1);
				refappname = refappname.substring(0,pos);
				Html5AvailableApplication refapp = ApplicationManager.instance().getAvailableApplication(refappname);
				if (refapp!=null) {
					filename = "/springfield/lou/apps/"+refappname+File.separator+refapp.getProductionVersion()+File.separator+"components"+File.separator+refcname+File.separator+refcname+".html";
				}
			}
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuffer str = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				str.append(line);
				str.append("\n");
				line = br.readLine();
			 }
			br.close();
			String body = str.toString();
			
			// preprocess
			body = body.replace("$cname","components."+ctype);
			
			if(overload) {
				this.setContent(target, body);
			} else {
				this.addContent(target, body);
			}
		} catch (Exception e){
				//System.out.println("Can't read template file for: "+ target);
		}
		
		
		// should it be component or target ? Daniel, changed it
		// ok lets turn  it into a component if needed
		ComponentInterface comp = app.getComponentManager().getComponent(ctype);
		if (comp!=null) {
			// we already have it so i guess its multiscreen component nice :)
			//register this component to the screen and the screen to the component
			comp.getScreenManager().put(this);
			this.cm.addComponent(comp);
			
		} else {
			// start a component based on the name (fixed now)
			try {
				String classname = "org.springfield.lou.application.components.types.";
			    classname += ctype.substring(0,1).toUpperCase();
			    classname += ctype.substring(1) + "Component";
				Object o = Class.forName(classname).newInstance();
				comp = (ComponentInterface)o;

			} catch(Exception e) {
				// lets assume its a basic component then
				comp = new BasicComponent();
			}
			comp.setId(ctype);
			comp.setApplication(app);
			app.addComponentToScreen(comp, this);
		}
	}
}