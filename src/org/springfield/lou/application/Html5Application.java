/* 
* Html5Application.java
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

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springfield.lou.application.components.ComponentInterface;
import org.springfield.lou.application.components.ComponentManager;
import org.springfield.lou.application.components.types.proxy.RemoteProxy;
import org.springfield.fs.*;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.location.Location;
import org.springfield.lou.location.LocationManager;
import org.springfield.lou.screen.Capabilities;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screen.ScreenManager;
import org.springfield.lou.user.User;
import org.springfield.lou.user.UserManager;
import org.springfield.lou.util.NodeObserver;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Html5Application
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application
 *
 */
public class Html5Application implements Html5ApplicationInterface,Runnable {
	
	public final static int LOG_INFO = 1;
	public final static int LOG_WARNING = 2;
	public final static int LOG_ERROR = 3;
	public final static String loglevels[] = {"info","warning","error"};
	protected String id;
	protected String fullid;
	protected String htmlpath;
	protected int screencounter;
	protected Boolean timeoutcheck;
    protected ScreenManager screenmanager;
    protected ComponentManager componentmanager;
    protected UserManager usermanager;
    protected ActionListManager actionlistmanager;
    protected Thread t;
    protected String appname = "";
    protected int externalInterfaceId;
    protected String remoteReciever = "video";
    protected boolean paired = false;
    protected boolean sessionrecovery = false;
	protected ArrayList<String> recoverylist  = new ArrayList<String>();
    protected boolean running = true;
    protected int fakeconnectionlost = 0;
    protected int fakeconnectionlostcount = 5;
    protected String location_scope = "browserid";
    protected Map<String, NodeObserver> observingNodes;
	protected Map<String, String> referids = new HashMap<String, String>();
	protected Map<String, String> referidscss = new HashMap<String, String>();
	protected Map<String, String> actionlists = new HashMap<String, String>();
    protected Map<String, String> callbackmethods = new HashMap<String, String>();
    protected Map<String, Object> callbackobjects = new HashMap<String, Object>();
    
    public Html5Application(String id, String remoteReciever) {
    	this.timeoutcheck = false;
    	this.observingNodes = new HashMap<String, NodeObserver>();
		this.id = id;
		int pos = id.indexOf("/html5application/");
		if (pos!=-1) {
			appname = id.substring(pos+18);
			appname = appname.indexOf("?") == -1 ? appname : appname.substring(0, appname.indexOf("?"));
			pos = appname.indexOf("/");
		    if (pos!=-1) {
		    	appname = appname.substring(0,pos);
		    }
		}
		this.screencounter = 1;
		this.screenmanager = new ScreenManager();
		this.componentmanager = new ComponentManager();
		this.externalInterfaceId = ApplicationManager.instance().getEternalInterfaceNumber();
		ApplicationManager.instance().addExternalInterface(externalInterfaceId, this);
		//System.out.println("external id: " + externalInterfaceId);
		this.usermanager = new UserManager();
		t = new Thread(this);
        t.start();
        try{
        RemoteProxy proxy = new RemoteProxy("remoteproxy", remoteReciever);
		proxy.setApplication(this);
		this.componentmanager.addComponent(proxy);
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        // load action lists and call the init !
        
    }
    
    public void addToRecoveryList(String name) {
    	recoverylist.add(name);
    }
    
    public ArrayList<String> getRecoveryList() {
    	return recoverylist;
    }
    
    public void setSessionRecovery(boolean s) {
    	sessionrecovery = s;
    }
    
    public boolean getSessionRecovery() {
    	return sessionrecovery;
    }
    
	public Html5Application(String id) {
		this(id, "video");
	}
	
	public String getLocationScope() {
		return location_scope;
	}
	
	public void setLocationScope(String ns) {
		location_scope = ns;
	}
	
	public void setId(String i) {
		this.id = i;
	}
	
	public String getId() {
		return id;
	}
	
	public String getAppname() {
		return appname;
	}
	
	public void setPaired(Boolean status){
		this.paired = status;
	}
	
	public String getDomain() {
		String result = id.substring(id.indexOf("/domain/")+8);
		result = result.substring(0,result.indexOf('/'));
		return result;
	}
	
	public void setFullId(String i) {
		this.fullid = i;
	}
	
	public String getFullId() {
		return fullid;
	}
	
	public String getHtmlPath() {
		return htmlpath;
	}
	
	public void setHtmlPath(String p) {
		htmlpath = p;
		this.actionlistmanager = new ActionListManager(this);
	}
	
	public void setCallback(String name,String m,Class c) {
		
	}
	
	public synchronized Screen getNewScreen(Capabilities caps,Map<String,String[]> p) {
		Long newid = new Date().getTime();
		screencounter++;
		Screen screen = new Screen(this,caps,id+"/1/screen/"+newid);
	//	System.out.println("NEW SCREEN="+this+" "+this.getAppname()+" "+this.getId()+" "+screencounter);

		screen.setParameters(p); // this can also be used to set location ?
	//	System.out.println("CAP IP="+caps.getCapability("ipnumber"));
	//	System.out.println("BROWSER ID="+caps.getCapability("smt_browserid"));
		
		// we need to add the location to this screen based on app settings
		if (location_scope.equals("browserid")) {
			String loc = caps.getCapability("smt_browserid");
			if (loc!=null)  {
				//Location nloc = new Location(loc, screen);
				Location nloc = new Location(loc);
				LocationManager.put(nloc);
				screen.setLocation(nloc);
			}
		} else if (location_scope.equals("ipnumber")) {
			String loc = caps.getCapability("ipnumber");
			if (loc!=null)  {
				//Location nloc = new Location(loc, screen); // problemdaniel
				Location nloc = new Location(loc); // problemdaniel
				LocationManager.put(nloc);
				screen.setLocation(nloc);
			}
		} else if (location_scope.equals("screen")) {
			String loc = screen.getId();
			//System.out.println("SCREEN LOC ID="+loc);
			if (loc!=null)  {
				//Location nloc = new Location(loc, screen);
				Location nloc = new Location(loc);

				LocationManager.put(nloc);
				screen.setLocation(nloc);
			}
		}
		this.screenmanager.put(screen);
		this.onNewScreen(screen);
		ApplicationManager.update();
		return screen;
	}
	
	public Screen getScreen(String id) {
		return this.screenmanager.get(id);
	}
	
	public ScreenManager getScreenManager(){
		return this.screenmanager;
	}
	
	public void executeActionlist(String name) {
		if (actionlistmanager.executeList(null, name)) {
			
		} else {
			// call class directly, to simulate a callServer
		}
	}
	
	public void executeActionlist(Screen s,String name) {
		//System.out.println("executeActionlist()");
		//System.out.println(s);
		//System.out.println(name);
		if (actionlistmanager.executeList(s, name)) {
			// so we did call a list !
		} else {
			try {
				// call class directly, to simulate a callServer
				int pos = name.indexOf("(");
				if (pos!=-1) {
					String content = name.substring(pos+1,name.length()-1);
					name = name.substring(0,pos);
					//System.out.println("NAME="+name);
					String methodname=dirToName(name);
					Object caller = this;
					
					// are these 2 overridden ?
					String overridemethodname = callbackmethods.get(name);
					if (overridemethodname!=null) {
						methodname = overridemethodname;
						caller = callbackobjects.get(name);
						//System.out.println("OVERRIDE2 ON="+name+" methodname="+methodname+" object="+caller);
					}
					
					//System.out.println("MNAME="+methodname);
					Method method = caller.getClass().getMethod(methodname,Screen.class,String.class);
					if (method!=null) {
						//System.out.println("DIRECT METHOD FOUND="+method);
						method.invoke(caller,s,content);
					} else {
						System.out.println("MISSING METHOD IN APP ="+method);
					}
				} else {
					//System.out.println("NAME2="+name);
					String methodname=dirToName(name);
					Object caller = this;
					
					// are these 2 overridden ?
					String overridemethodname = callbackmethods.get(name);
					if (overridemethodname!=null) {
						methodname = overridemethodname;
						caller = callbackobjects.get(name);
						//System.out.println("OVERRIDE ON="+name+" methodname="+methodname+" object="+caller);
					}
					
					//System.out.println("MNAME="+methodname);
					Method method = caller.getClass().getMethod(methodname,Screen.class,f);
					if (method!=null) {
						//System.out.println("DIRECT METHOD FOUND="+method);
						method.invoke(caller,s);
					} else {
						System.out.println("MISSING METHOD IN APP ="+method);
					}
				}
				} catch(Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	public UserManager getUserManager(){
		return this.usermanager;
	}
	
	public String getLibPaths() {
		String result = null;
		String libsdir = "";
		if (LazyHomer.isWindows()) {
			libsdir = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\apps\\"+id+"\\libs";
		} else {
			libsdir = "/springfield/tomcat/webapps/ROOT/eddie/apps/"+id+"/libs";
		}
		File dir = new File(libsdir);
		String[] files = dir.list();
		for (int i=0;i<files.length;i++) {
			String filename = files[i];
			if (result==null) {
				result = id+"/libs/"+filename;
			} else {
				result +=","+id+"/libs/"+filename;
			}
		}
		return result;
	}
	
	public void run() {
		while(running){
			try {
					Thread.sleep(10000);
					this.maintainanceRun();
					this.timeoutCheckup();
			} catch (Exception e) {
				System.out.println("Exception in run() application");
				e.printStackTrace();
			}
		}
		System.out.println("application run done, shutting down");
	}
	
	public void timeoutCheckup() {
		//System.out.println("timeoutCheckup()");
		Set<String> keys = this.screenmanager.getScreens().keySet();
		//System.out.println("timeoutCheckup() "+keys.size()+" "+this.getFullId()+" this="+this);
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen screen = this.screenmanager.get(next);
			if ((new Date().getTime()-screen.getLastSeen()>(60*1000))) { // moved from 12 seconds to 12 hours
				String username = screen.getUserName();
				if (username!=null) {
					this.onLogoutUser(screen,username);
				}
			    it.remove(); // avoids a ConcurrentModificationException
			    this.removeScreen(next,username);
				//if(this.screenmanager.size()==0){
				//	this.screencounter = 1;
				//}
			} else {
				//System.out.println("Screen ok "+screen.getId());
			}
		}
	}
	
	public void shutdown() {
		running = false;
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen screen = this.screenmanager.get(next);
			String username = screen.getUserName();
			this.onLogoutUser(screen,username);
			it.remove(); // avoids a ConcurrentModificationException
			System.out.println("SHUTDOWN");
			this.removeScreen(next,username);	
		}
		ApplicationManager.instance().removeApplication(this.id);
	}
	
	public void maintainanceRun() {
			try {
				if (!running) return;
				Set<String> keys = this.screenmanager.getScreens().keySet();
				Iterator<String> it = keys.iterator();
				while(it.hasNext()){
					String next = it.next();
					Screen s = this.screenmanager.get(next);
					s.setContent("synctime",new Date().toString());
				}
			} catch(Exception e) {
				System.out.println("Exception in maintainance run");
			}
	}
	
	public void setContent(String div,String content) {
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen s = this.screenmanager.get(next);
			s.setContent(div,content);
		}
	}
	
	public void setContentOnScope(Screen scopescreen,String div,String content) {
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen s = this.screenmanager.get(next);
			
			// we need to send this only to our location scope
			Location loc =s.getLocation();
			Location scopeloc = scopescreen.getLocation();
			if (loc!=null && scopeloc!=null) {
				String locid = loc.getId(); 
				String slocid = scopeloc.getId(); 
				if (locid.equals(slocid)) {
					s.setContent(div,content);
				}
			} else {
				System.out.println("LOCATION IS NULL SHOULD NOT HAPPEN !");
				s.setContent(div,content);
			}
		}
	}
	
	public int getExternalInterfaceId(){
		return this.externalInterfaceId;
	}
	
	public void putData(String data) {
		int pos = data.indexOf("put(");
		if (pos!=-1) {
			data = data.substring(pos+4);
			int pos2 = data.indexOf(")");
			if (pos2!=-1) {
				String target = data.substring(0,pos2);
				int pos3 = target.indexOf(",");
				String from = target.substring(0,pos3);
				target = target.substring(pos3+1);
				String content = data.substring(pos2+2);
				Screen ts = this.screenmanager.get(from);
				if (ts!=null) {
					ts.setSeen();
				} else {
				//	System.out.println("EMPTY FROM SCREEN = "+from);
				}
				
				//System.out.println("FROM="+from+" TARGET="+target+" CONTENT="+content);
				if (target.equals("")) {
					// get the correct screen in this case the sender
					ts.put(from,content);
				} else {
					// check if we are a component 
					ComponentInterface comp = this.componentmanager.getComponent(target);	
					if (comp!=null) {
						//System.out.println("FOUND COMPONENENT = "+comp.getId());
						comp.put(from,content);
					} else if (target.equals("../*")) {
						// do we send it all screens attached ?
						setContentAllScreens(from,content);
					} else if (target.indexOf("../")==0){
						// ok so not all screens but a screen !
						String ns = target.substring(3);
						String cs = from.substring(0,from.lastIndexOf("/"));
						//System.out.println("NS="+ns+" CS="+cs);
						Screen ts2 = this.screenmanager.get(cs+"/"+ns);
						ts2.put(from,content);
					}//else System.out.println("nothing to do");
				}
			}
		}
 	}
	
	public void loadStyleSheet(Screen s,String sname) {
    	String refercss = getReferidCSS(sname);
    	if (refercss==null) {
    		s.loadStyleSheet(getApplicationCSS(sname) , this);
    	} else {
        	if (refercss.startsWith("/")) {
    				String refappname = refercss.substring(1);
    				int pos = refappname.indexOf("/");
    				String refcss = refappname.substring(pos+1);
    				refappname = refappname.substring(0,pos);
    				s.loadStyleSheetRefer(getApplicationCSSRefer(refcss,refappname),refappname);
        	}
    		
    	}
		//s.loadStyleSheet(sname, this);
	}
	
	// example loadStyleSheet(s,"trafficcontroller",appname);
	
	public void loadStyleSheet(Screen s,String dstyle,String sname) {
		String fs = getDeviceCSS(dstyle);
		if (LazyHomer.isWindows()) {
			fs = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\apps\\"+appname+"\\css\\"+dstyle+".css";
		} else {
			fs = "/springfield/tomcat/webapps/ROOT/eddie/apps/"+appname+"/css/"+dstyle+".css";
		}
		File f = new File(fs);
		if (f.exists()) {
			s.loadStyleSheet(getDeviceCSS(dstyle) , this);
		} else {
			s.loadStyleSheet(sname , this);
		}
	}

	public void loadContent(Screen s, String div,String comp) {
		s.loadContent(div,comp,true, this);
		// default also load the script attached
		s.loadComponentScript(div,componentmanager.getComponentJS(comp), this, comp);
	
		Iterator<String> it = this.componentmanager.getComponent(comp).getProperties().keySet().iterator();	
		while(it.hasNext()){
			String property = (String) it.next();
			String msg = "setdata(" + property + "," + this.componentmanager.getComponents().get(comp).getProperties().get(property) + ")";
			s.putMsg(comp, "", msg);
			System.out.println("SENDING INIT PROPERTIES!!! MSG::: "+ msg + "target:: " + comp);
		}
	}
	
	public void addContent(Screen s, String div,String comp) {
		s.loadContent(div,comp,false, this);
		// default also load the script attached
		s.loadScript(div,componentmanager.getComponentJS(comp), this);
	}
	
	public void addReferid(String div,String referid) {
		// adds a referid for a external app to a local div name
		referids.put(div, referid);
	}
	
	public String getReferid(String ctype) {
		return referids.get(ctype);
	}
	
	public void addReferidCSS(String local,String refercss) {
		referidscss.put(local, refercss);
	}
	
	
	public String getReferidCSS(String css) {
		return referidscss.get(css);
	}

	
	public void loadContent(Screen s, String comp) {
		s.loadContent(comp, comp,true, this);
		s.loadComponentScript(comp, componentmanager.getComponentJS(comp), this, comp);
		//s.loadScript(comp, componentmanager.getComponentGestures(comp),this);
		
		Iterator<String> it = this.componentmanager.getComponent(comp).getProperties().keySet().iterator();	
		while(it.hasNext()){
			String property = (String) it.next();
			String msg = "setdata(" + property + "," + this.componentmanager.getComponents().get(comp).getProperties().get(property) + ")";
			s.putMsg(comp, "", msg);
			//System.out.println("SENDING INIT PROPERTIES!!! MSG::: "+ msg + "target:: " + comp);
		}
	}

	
	public void setContentAllScreens(String from,String content) {
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen s = this.screenmanager.get(next);
			s.put(from,content);
		}
	}
	
	public void put(String from,String content) {
		System.out.println("Application put should be overridden");
	}
	
	public void putOnScreen(Screen s,String from,String content) {
		
		// old commands can we integrate them in actions ???
		String component = content.substring(content.indexOf("(")+1, content.indexOf(")"));
		if(content.indexOf("load(")==0)	{
			String[] parts = component.split(",");
			if (parts.length==1) { 
				loadContent(s, component);
			} else {
				loadContent(s, parts[0],parts[1]);
			}
		} else if(content.indexOf("add(")==0) {
			String[] parts = component.split(",");
			addContent(s, parts[0],parts[1]);
		} else if(content.indexOf("remove(")==0) {
			removeContent(s, component);
		} else if(content.indexOf("log(")==0) {
			String[] parts = component.split(",");
			eddieLog(s,component);
		}
		// call the actionlists attached !
        executeActionlist(s,content);
	}
	
	public void removeContent(Screen s, String comp){
		s.removeContent(comp, this);	
	
	}
	
	public void removeContentAllScreens(String comp){
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen s = this.screenmanager.get(next);
			s.removeContent(comp, this);	
		}
	}
	
	public void removeContentAllScreensWithRole(String role,String comp){
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen s = this.screenmanager.get(next);
			if (s.getRole().equals(role)) {
				s.removeContent(comp, this);	
			}
		}
	}
	
	public void loadContentAllScreensWithRole(String role,String comp) {
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen s = this.screenmanager.get(next);
			if (s.getRole().equals(role)) {
				loadContent(s,comp);	
			}
		}
	}
	
	public void setContentAllScreensWithRole(String role,String div,String content) {
		Set<String> keys = this.screenmanager.getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = it.next();
			Screen s = this.screenmanager.get(next);
			if (s.getRole().equals(role)) {
				s.setContent(div,content);
			}
		}
	}
	
	public void addComponentToScreen(ComponentInterface comp, Screen sc){
		this.componentmanager.addComponent(comp);
		comp.getScreenManager().put(sc);
		sc.getComponentManager().addComponent(comp);	
	}
	
	public void addComponent(ComponentInterface comp) {
		this.componentmanager.addComponent(comp);	
	}
	
	public void removeComponentFromScreen(String comp, Screen sc){
		try{
			sc.getComponentManager().removeComponent(comp);
		}catch(Exception e){
			//e.printStackTrace();
		}
		try{
			this.componentmanager.getComponent(comp).getScreenManager().remove(sc.getId());
		}catch(Exception e){
			//e.printStackTrace();
		}
		
		try{
			Iterator<String> it = this.screenmanager.getScreens().keySet().iterator();	
			while (it.hasNext()){
				Screen s = this.screenmanager.get(it.next());
				//if a screen still has this component don't remove it
				//from the application component manager
				if(s.getComponentManager().getComponents().containsKey(comp)) return;
			}
			
			this.componentmanager.removeComponent(this.componentmanager.getComponent(comp).getId());
		}catch(Exception e){
			//e.printStackTrace();
		}
		
	}
	
	public void removeScreen(String id,String username){
		Screen screen = this.screenmanager.get(id);
//		String username =null;
		if (screen!=null) {
			username = screen.getUserName();
			Location nloc = screen.getLocation();
			if (nloc!=null) {
				LocationManager.remove(nloc.getId());
				System.out.println("location remove = "+nloc.getId()+" "+LocationManager.size());
			}
		}
		ScreenManager.globalremove(id);
		System.out.println("REMOVE SCREEN CALLED SIZE="+this.screenmanager.size());
		Iterator<String> it = this.componentmanager.getComponents().keySet().iterator();
		while(it.hasNext()){
			this.componentmanager.getComponent((String)it.next()).getScreenManager().remove(id);
		}
		onScreenTimeout(screen);
		//if(this.screenmanager.size()==0){
		//	this.screencounter = 1;
		//}

		if (username!=null) {
			User u = usermanager.getUser(username);
				if(u!=null) {
					u.removeScreen(id);
					if (u.getScreens().size()==0) {
						// user not on any screen remove it from the app
						usermanager.removeUser(u);	
					}
			}
		}
		ApplicationManager.update();
	}
	
	public ComponentManager getComponentManager(){
		return this.componentmanager;
	}
	
	public void onScreenTimeout(Screen s) {
		//System.out.println("Screen timeout should be overridden by application");
	}
	
	public void onNewScreen(Screen s) {
		//loadContent(s, "signal"); old code ?
		String extraactionlist = s.getParameter("actionlist");
		//System.out.println("EXTRALIST="+extraactionlist);
		if (extraactionlist!=null) {
			executeActionlist(s,extraactionlist);
		} else {
			executeActionlist(s,"newscreen");
		}
	}
	
	public void onLogoutUser(Screen s,String name) {
		User u = usermanager.getUser(name);
		if (u!=null) { // should check if still on other screen !!!
			usermanager.removeUser(u);
		}
		ApplicationManager.update();	
	}
	
	public void onNewUser(Screen s,String id) {
		User u = usermanager.getUser(id);
		if (u==null) {
			u = new User(id);
			u.addScreen(s);
			usermanager.addUser(u);
		} else {
			u.addScreen(s);
		}
		ApplicationManager.update();
		System.out.println("NewUser APP="+appname);
		if (!appname.equals("dashboard")) executeActionlist(s,"newuser");
	}
	
	public void onLoginFail(Screen s,String id) {
		executeActionlist(s,"login/loginfail");
	}
	
    public String getApplicationCSS(String name) {
    	// weird for now.
    	//System.out.println("CSS NAME="+name);
    	String path = "apps/"+appname+"/css/"+name+".css";
    	return path;
     }
    
    public String getApplicationCSSRefer(String refcss,String refappname) {
    	// weird for now.
    	//System.out.println("CSS NAME="+refcss);
		String path = "apps/"+refappname+"/css/"+refcss+".css";
		//System.out.println("REFERID CSS !!!!="+path);
		return path;
    }

    
    public String getDeviceCSS(String dstyle) {
    	// weird for now.
    	String path = "apps/"+appname+"/css/"+dstyle+".css";
    	return path;
    }
    
    public int getScreenCount() {
    	return screenmanager.getScreens().size();
    }
    
    public int getUserCount() {
    	return usermanager.size();
    }
    
    public int getScreenIdCounter() {
    	return screencounter-1;
    }
    
    public void subscribe(String node, FSXMLStrainer strainer){
    	System.out.println("Html5Application.subscribe(" + node + ", " + strainer + ")");
    	this.observingNodes.put(node, new NodeObserver(node, strainer));
    }
    
    public void unsubscribe(String node){
    	this.observingNodes.remove(node);
    }
    
    
    private void eddieLog(Screen s,String content) {
    	String[] parts = content.split(",");
    	String l = parts[1];
    	int level = LOG_INFO; // default to info
    	if (l.equals("warning")) { level = LOG_WARNING; }
    	else if (l.equals("error")) { level = LOG_ERROR; }
    	FsNode n = new FsNode("log");	
  		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
  		n.setId(f.format(new Date()));
  		n.setProperty("level", loglevels[level-1]);
  		n.setProperty("source", "js");
  		n.setProperty("msg", parts[0]);
  		if (s!=null) {
  			n.setProperty("screen", s.getShortId());
  			if (s.getUserName()!=null) {
  				n.setProperty("user", s.getUserName());
  			} else {
  				n.setProperty("user", "unknown");
  			}
  		}
  		ApplicationManager.log(this, n);
    }
    
    public void log(String msg) {
    		log(null,msg,LOG_INFO);
    }
    
    public void log(String msg,int level) {
		log(null,msg,level);
    }
    
    public void log(Screen s,String msg) {
		log(s,msg,LOG_INFO);
    }
    
    public boolean inDevelopemntMode() {
    	return LazyHomer.inDeveloperMode();
    }
    
    /**
     * 
     * adds application id, checks with barney and talks to mojo if allowed
     * 
     * @param path
     * @return
     */
    public final FsNode getNode(String path) {
    	String asker = this.getClass().getName(); // gets the name from the classloader
    	int pos = asker.indexOf("org.springfield.lou.application.types.");
    	if (pos==0) { // make sure we are in the right package
    		asker = asker.substring(pos+38);
    		System.out.println("getNode "+asker);
    		ServiceInterface barney = ServiceManager.getService("barney");
    		if (barney!=null) {
    			String allowed = barney.get("applicationallowed(read,"+path+",0,"+asker+")",null,null);
    			if (allowed!=null && allowed.equals("true")) {
    				return Fs.getNode(path); // so its allowed ask it
    			}
    		}
    	}
    	return null;
    }
    
	public boolean checkNodeActions(FsNode node,String actions) {
    		return checkNodeActions(node,0,actions);
	}
    
	public boolean checkNodeActions(FsNode node,int depth,String actions) {
    	String asker = this.getClass().getName(); // gets the name from the classloader
    	int pos = asker.indexOf("org.springfield.lou.application.types.");
    	if (pos==0) { // make sure we are in the right package
    		asker = asker.substring(pos+38);
    		return node.checkActions(asker,"application",depth,actions); 
    	}
    	return false;
	}
	
	public String getFavicon() {
		return null;
	}
	
	public String getMetaHeaders(HttpServletRequest request) {
		return ""; // default is empty;
	}
    
    public void log(Screen s,String msg,int level) {
    		FsNode n = new FsNode("log");	
    		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
    		n.setId(f.format(new Date()));
    		n.setProperty("level", loglevels[level-1]);
    		n.setProperty("source", "java");
    		n.setProperty("msg", msg);
    		if (s!=null) {
    			n.setProperty("screen", s.getShortId());
    			if (s.getUserName()!=null) {
    					n.setProperty("user", s.getUserName());
    			} else {
					n.setProperty("user","unknown");	
    			}
    		} else {
    			n.setProperty("screen", "application");
    			n.setProperty("user","unknown");    			
    		}
    		ApplicationManager.log(this, n);
    }
    
    public void setCallback(String name,String m,Object o) {
    	System.out.println("SET CALLBACK "+name+" to "+m+" o="+o);
    	callbackmethods.put(name, m); 
    	callbackobjects.put(name, o); 
    }
    
    private String dirToName(String dir) {
    	// converts things like divione/mouseup to 'actionDivoneMouseup'
    	String name = "action";
    	String[] parts = dir.split("/");
    	for (int i=0;i<parts.length;i++) {
    		String tmp = parts[i].substring(0,1);
    		String tmp2 = parts[i].substring(1);
    		name += tmp.toUpperCase()+tmp2.toLowerCase();
    	}
    	return name;
    }
    
    public Boolean externalGainEvent(HttpServletRequest request,String data) {
    	System.out.println("Error: Incoming gain event on : "+id+" not handled by app");
    	System.out.println("DATA="+data);
    	return false;
    }

}
