/* 
* LouServlet.java
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

package org.springfield.lou.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springfield.lou.application.ActionList;
import org.springfield.lou.application.ApplicationManager;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.application.components.types.proxy.ProxyComponent;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Capabilities;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.tools.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Servlet implementation class ServletResource
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.servlet
 */
@WebServlet("/LouServlet")
public class LouServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(LouServlet.class);
	private static final String password = "password";
	private static final long serialVersionUID = 42L;
	private static Map<String, String> urlmappings = new HashMap<String, String>();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LouServlet() {
        super();
        System.out.println("servlet object created");
        // TODO Auto-generated constructor stub
    }
    
    public static void addUrlTrigger(String url,String actionlistname) {
    		String parts[] = url.split(",");
    		urlmappings.put(parts[0],parts[1]+","+actionlistname);
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		String body = request.getRequestURI();
		System.out.println("INCOMMING!! REQUEST IS="+body);
		if(request.getParameter("method")!=null) {
			if(request.getParameter("method").equals("post")){
				System.out.println("going for post");
				doPost(request, response);
				return;
			}
		}
		
		//String body = request.getRequestURI();
		//System.out.println("INCOMMING REQUEST IS="+body);
		
		// need to move to be faster
		String params = request.getQueryString();
		System.out.println("PARAMS1="+params);
		String[] paths = urlMappingPerApplication(request,body);
		if (paths!=null) {
			body = paths[0];
			if (params!=null) {
				params += "&actionlist="+paths[1];
			} else {
				params = "actionlist="+paths[1];
			}
		}
		//System.out.println("PARAMS2="+params);
		
		int pos = body.indexOf("/html5application/");
		if (pos!=-1) {
			doIndexRequest(body,request,response,params);
		} else {
		response.setContentType("text/xml; charset=UTF-8");
		OutputStream out = response.getOutputStream();
		//PrintWriter out = response.getWriter();
		String structureXML = "<fsxml>";
		Iterator it = ApplicationManager.instance().getApplications().keySet().iterator();
		while(it.hasNext()){
			String app = (String) it.next();
			structureXML+="<application id=\""+ApplicationManager.instance().getApplication(app).getFullId()+"\">";
			structureXML+="<componentManager id=\""+ApplicationManager.instance().getApplication(app).getComponentManager()+"\">";
			Iterator it2 = ApplicationManager.instance().getApplication(app).getComponentManager().getComponents().keySet().iterator();
			while(it2.hasNext()){
				String comp = (String) it2.next();
				structureXML += "<component id=\""+ApplicationManager.instance().getApplication(app).getComponentManager().getComponent(comp).getId() +"\">";
				Iterator it3 = ApplicationManager.instance().getApplication(app).getComponentManager().getComponent(comp).getScreenManager().getScreens().keySet().iterator();
				while(it3.hasNext()){
					String scr = (String)it3.next();
					structureXML += "<screen id=\""+ApplicationManager.instance().getApplication(app).getComponentManager().getComponent(comp).getScreenManager().get(scr).getId()+"\">"+ApplicationManager.instance().getApplication(app).getComponentManager().getComponent(comp).getScreenManager().get(scr)+"</screen>";
				}
				structureXML += "</component>";
			}
			structureXML += "</componentManager>";
			structureXML+="<screenManager id=\""+ApplicationManager.instance().getApplication(app).getScreenManager()+"\">";
			Iterator it4 = ApplicationManager.instance().getApplication(app).getScreenManager().getScreens().keySet().iterator();
			while(it4.hasNext()){
				String screen = (String) it4.next();
				structureXML += "<screen id=\""+ApplicationManager.instance().getApplication(app).getScreenManager().get(screen).getId() +"\">";
				Iterator it5 = ApplicationManager.instance().getApplication(app).getScreenManager().get(screen).getComponentManager().getComponents().keySet().iterator();
				while(it5.hasNext()){
					String cmp = (String) it5.next();
					structureXML += "<component id=\""+ApplicationManager.instance().getApplication(app).getScreenManager().get(screen).getComponentManager().getComponent(cmp).getId()+"\">"+ApplicationManager.instance().getApplication(app).getScreenManager().get(screen).getComponentManager().getComponent(cmp)+"</component>";
				}
				structureXML += "</screen>";
			}
			structureXML += "</screenManager>";
			
			structureXML += "</application>";
		}
		structureXML += "</fsxml>";
		//System.out.print(structureXML);
		out.write(structureXML.getBytes());
		
		out.close();
		}
		return;
	}	
	
	private void doIndexRequest(String uri,HttpServletRequest request, HttpServletResponse response,String params) {
		try {	
			response.addHeader("Access-Control-Allow-Origin", "*");  
			response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type");
			
			response.setContentType("text/html; charset=UTF-8");
			OutputStream out = response.getOutputStream();
			//PrintWriter out = response.getWriter();
			
			//String params = request.getQueryString();
			String user = null;
			String app = "test";
			
			int pos = uri.indexOf("/user/");
			if (pos!=-1) {
				user = uri.substring(pos+6);
				pos = user.indexOf("/");
				user = user.substring(0,pos);
			}
	
			pos = uri.indexOf("/html5application/");
			if (pos!=-1) {
				app = uri.substring(pos+18);
				if (app.equals("")) app="test";
			}
			String fullappname = uri.substring(4);
			//String body = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
			//body+="<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
			// CWI / AngularJS compatible
			//String body="<!doctype html>";
			//body+="<html ng-app=\"tkkDemoApp\">";
			String body = "<!DOCTYPE html PUBLIC \"-//HbbTV//1.1.1//EN\" \"http://www.hbbtv.org/dtd/HbbTV-1.1.1.dtd\">";
			body += "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
			body+="<head>\n";
			body+="<meta http-equiv=\"Content-Type\" content=\"application/vnd.hbbtv.xml+xhtml; utf-8\" />";
			body+="<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />";
			body+="<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" />";
			body+="<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n";
			body+="<meta name=\"viewport\" content=\"width=device-width, user-scalable = no,initial-scale=1.0; maximum-scale=1.0;\">";
			body+="<script language=\"javascript\" type=\"text/javascript\">var LouSettings = {\"lou_ip\": \"" + LazyHomer.getExternalIpNumber() + "\", \"lou_port\": \"" + LazyHomer.getBartPort() + "\", \"user\": \"" + user + "\", \"app\": \"" + app + "\", \"fullapp\": \"" + fullappname + "\", \"appparams\": \"" + params + "\"}</script>\n";
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/jquery-1.8.0.js\"></script>\n";
			String libs = getLibPaths(app);
			if (libs!=null) {
				String[] l = libs.split(",");
				for (int i = 0;i<l.length;i++) {
					body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/apps/"+l[i]+"\"></script>\n";
				}
			}
			// check if the domain has a special eddie script (for devel use)
			String domain = fullappname.substring(8);
			domain = domain.substring(0,domain.indexOf("/"));
			String basepath = "/springfield/tomcat/webapps/ROOT/eddie/";
			if (LazyHomer.isWindows()) basepath = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\";
//			System.out.println(basepath+"domain"+File.separator+domain+File.separator+"js"+File.separator+"eddie.js");
			
			//Added by David to test
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/eddie.js?cache\"></script>\n";
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/main.js\"></script>\n";
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/stacktrace.js\"></script>\n";
			body+="<title></title>\n";
			body+="</head>\n";
			
			// CWI / AngularJS compatible
			//body+="<body ng-view>\n";
			body+="<body>\n";

			body+="<div id=\"screen\" />\n";
			body+="</body>\n";
			body+="</html>\n";
			out.write(body.getBytes());
			out.close();
		} catch(Exception e) {
			System.out.println("Lou can't create index page");
			e.printStackTrace();
		}
	}
	
	public String getLibPaths(String id) {
		String result = null;
		String libsdir = "";
		if (LazyHomer.isWindows()) {
			libsdir = "C:\\springfield\\tomcat\\webapps\\ROOT\\eddie\\apps\\"+id+"\\libs";
		} else {
			libsdir = "/springfield/tomcat/webapps/ROOT/eddie/apps/"+id+"/libs";
		}
		//System.out.println("SCANNING="+libsdir);
		File dir = new File(libsdir);
		
		if (!dir.exists()) return null; // return if no dir.
		
		String[] files = dir.list();
		for (int i=0;i<files.length;i++) {
			String filename = files[i];
			if(filename.contains(".svn")) continue;
			if (result==null) {
				result = id+"/libs/"+filename;
			} else {
				result +=","+id+"/libs/"+filename;
			}
		}
		return result;
	}

	protected void handleExternalRequest() {
		
	}
	
	/**
	 * Post request handles mainly external requests
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		
		String body = request.getRequestURI();
		System.out.println("INCOMMING REQUEST IS="+body);

		HashMap<String, String[]> urlProperties = (HashMap<String, String[]>) request.getParameterMap();
		HashMap<String, String[]> properties = new HashMap<String, String[]>();
		//read post data
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
			reader.close();
		} catch (Exception e) { /*report an error*/ }
		
		String xml = jb.toString();
		//parse URI
		String reqUri = request.getRequestURI();
		String domain = reqUri.substring(reqUri.indexOf("/domain/")+8, reqUri.indexOf("/", reqUri.indexOf("/domain/")+8));
		String user = (reqUri.indexOf("/user/")!=-1) ? reqUri.substring(reqUri.indexOf("/user/")+6, reqUri.indexOf("/", reqUri.indexOf("/user/")+6)) : ""  ;
		String application = (reqUri.indexOf("/", reqUri.indexOf("/html5application/")+18)!=-1) ? reqUri.substring(reqUri.indexOf("/html5application/")+18, reqUri.indexOf("/", reqUri.indexOf("/html5application/")+18)) : reqUri.substring(reqUri.indexOf("/html5application/")+18);
		String component = (reqUri.indexOf("/component/")!=-1) ? ((reqUri.indexOf("/", reqUri.indexOf("/component/")+11) !=-1) ? reqUri.substring(reqUri.indexOf("/component/")+11, reqUri.indexOf("/", reqUri.indexOf("/component/")+11)) : reqUri.substring(reqUri.indexOf("/component/")+11)) : ""  ;
		String applicationUri = "/domain/" + domain + ((user.equals("")) ? "" : "/user/" + user) + "/html5application/" + application;
		//System.out.println(xml);
		
		try {
			//create xml from post data
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
			
			//extract message tags
			NodeList messages = doc.getElementsByTagName("message");
			//parse every message separately 
			for(int m=0;m<messages.getLength();m++){
				//clear properties of previous message
				properties.clear();
				//get current message properties
				NodeList nl = ((Element)messages.item(m)).getElementsByTagName("properties").item(0).getChildNodes();
				//put all properties in hashmap
				for(int i=0;i<nl.getLength();i++){
					String[] temp = {nl.item(i).getTextContent()};
					if(properties.containsKey(nl.item(i).getNodeName())){
						List<String> ls = new ArrayList<String>();
						ls.addAll(Arrays.asList(properties.get(nl.item(i).getNodeName())));
						ls.add(nl.item(i).getTextContent());
						properties.put(nl.item(i).getNodeName(), ls.toArray(new String[ls.size()]));
						
					}else 
						properties.put(nl.item(i).getNodeName() , temp);
				}			
				//merge URL properties with xml properties
				properties.putAll(urlProperties);
				properties.remove("method");      
				
				//check if URI has all the info we need
				if(reqUri.indexOf("/domain/")==-1 || reqUri.indexOf("/html5application/")==-1){
					System.out.println("request does not meet our rest structure requirements");
					response.setContentType("text/xml; charset=UTF-8");
					PrintWriter out = response.getWriter();
					out.print("Request Uri error.");out.flush();out.close();return;
				}
				
				ProxyComponent proxy = null;
				try{
					//check if the proxy already exists
					proxy = (ProxyComponent)ApplicationManager.instance().getApplication(applicationUri).getComponentManager().getComponent(component);
				}catch(java.lang.NullPointerException e){}
				if(proxy==null){
					//if proxy does not exist try creating a new one
					try {
						String classname = "org.springfield.lou.application.components.types.proxy."+component.substring(0,1).toUpperCase()+component.substring(1,component.indexOf("proxy"))+"Proxy";
						System.out.println("classname::: " + classname);
						Object o = Class.forName(classname).getConstructor(String.class).newInstance(component.toLowerCase());
						proxy = (ProxyComponent)o;	
						//add proxy component to the application
						proxy.setApplication(ApplicationManager.instance().getApplication(applicationUri));
						ApplicationManager.instance().getApplication(applicationUri).addComponent(proxy);
						//init is necessary because object does not know
						//its application when constructed
						proxy.init();
					} catch(Exception e) {
						e.printStackTrace();
						System.out.println("Component with name \""+component+"\" does not exist");
						PrintWriter out = response.getWriter();
						out.print("Component with name \""+component+"\" does not exist");
						out.flush();out.close();return;
					}
				}
				proxy.put("external", properties);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("XML parsing error. Getting properties only from URL.");
		}
		
		PrintWriter out = response.getWriter();
		out.print("OK");
		out.flush();
		out.close();
		
		return;
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		//read the data from the put request
		InputStream inst = request.getInputStream();
		String data;
		
		// reads the data from inputstring to a normal string.
		java.util.Scanner s = new java.util.Scanner(inst).useDelimiter("\\A");
		data = (s.hasNext()) ? s.next() : null;
		
		//System.out.println("DATA="+data);

		Map<String,String[]> params = request.getParameterMap();
		// lets find the correct application
		Html5ApplicationInterface app = null;
		String url = request.getRequestURI();
		int pos = url.indexOf("/domain/");
		if (pos!=-1) {
			String tappname = url.substring(pos);
			app = ApplicationManager.instance().getApplication(tappname);
			
			if (app==null && ApplicationManager.instance().getAvailableApplicationByInstance(tappname)!=null) {
				app = ApplicationManager.instance().getLoadedApplication(tappname);
				String fullid = url.substring(url.indexOf("/domain/"));
				app.setFullId(fullid);
				ApplicationManager.instance().addApplication(app);
			} else if (app==null) {
				try {
					String classname = "org.springfield.lou.application.types.";
					pos = tappname.indexOf("/html5application/");
					if (pos!=-1) {
						String apppart = tappname.substring(pos+18);
						pos = apppart.indexOf("/");
					    if (pos!=-1) {
					    	apppart = apppart.substring(0,pos);
					    }
					    classname += apppart.substring(0,1).toUpperCase();
					    classname += apppart.substring(1) + "Application";
					}
					//System.out.println("WANT CLASS="+classname);
					Object o = Class.forName(classname).getConstructor(String.class).newInstance(tappname);
					app = (Html5ApplicationInterface)o;
					String fullid = url.substring(url.indexOf("/domain/"));
					app.setFullId(fullid);
					//System.out.println("ADDING APP="+app);
					ApplicationManager.instance().addApplication(app);
				} catch(Exception e) {
					
				}
			}
			
			
		}
		
		if (data.indexOf("put(")==0) {
			app.putData(data);
			return;
		}
		
		if (data.indexOf("stop(")==0) {
			String screenid = data.substring(5,data.length()-1);
			app.removeScreen(screenid,null);
			return;
		}
		
		//build an org.w3c.dom.Document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(data));
			Document doc = builder.parse(is);

			//get the the user information from the xml
			Element root = (Element) doc.getElementsByTagName("fsxml").item(0);
			Element screenxml = (Element) root.getElementsByTagName("screen").item(0);
			
			String screenId = screenxml.getElementsByTagName("screenId").item(0).getTextContent();
		
			// does this screen already have a id ?
			//System.out.println("SCREENID="+screenId);
			if(!screenId.equals("-1") && app.getScreen(screenId)!=null) {
				// ok so we should find it and its attached app
				Screen screen = app.getScreen(screenId);
				screen.setSeen();
				screen.setParameters(params);
				//System.out.println("OLD SCREEN = "+screen.getId());
				response.setContentType("text/xml; charset=UTF-8");
				OutputStream out = response.getOutputStream();
				//PrintWriter out = response.getWriter();
				String msg = screen.getMsg();
				if (msg==null) { // bad bad bad
					try {
						synchronized (screen) {
							screen.wait();
						}
					} catch (InterruptedException e) {
						//	System.out.println("got interrupt.. getting data");
					}
					msg = screen.getMsg();
					if (msg==null) {
						// simulated a drop connection
						System.out.println("DROP CONNECTION");
						out.close();
						return;
					}
				}

				//System.out.println("data2="+msg);
				out.write(msg.getBytes());
				out.flush();
				out.close();
				
			} else {
				//System.out.println("screenId="+screenId);
				if (!screenId.equals("-1")) {
					System.out.println("Sending stop");
					response.setContentType("text/xml; charset=UTF-8");
					OutputStream out = response.getOutputStream();
					//PrintWriter out = response.getWriter();
					out.write(XMLHelper.createScreenIdFSXML("-1",false).getBytes());
					out.flush();
					out.close();
				} else {
					//System.out.println("PARAMS="+params);
					Capabilities caps = getCapabilities(root);
					
					 // extend this with Location info 
					caps.addCapability("ipnumber", request.getRemoteAddr());
					
					Screen screen = app.getNewScreen(caps,params);
					//System.out.println("PARAMSET="+params);
					screen.setParameters(params);
					
					// see if we need to override the location
					String ploc = screen.getParameter("location");
					if (ploc!=null) screen.getLocation().setId(ploc);
					response.setContentType("text/xml; charset=UTF-8");
					OutputStream out = response.getOutputStream();
					out.write(XMLHelper.createScreenIdFSXML(screen.getId(),true).getBytes());
					out.flush();
					out.close();
				}
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return;
	}
	
	private  Capabilities getCapabilities(Element xml) {
		//System.out.println("GETTING CAP2");
		NodeList capabilities = ((Element) xml.getElementsByTagName("capabilities").item(0)).getElementsByTagName("properties").item(0).getChildNodes();
		Capabilities caps = new Capabilities();
		 for(int i=0; i<capabilities.getLength();i++){
			 caps.addCapability(capabilities.item(i).getNodeName(), capabilities.item(i).getTextContent());
		 }
		return caps;
	}
	
	private String[] urlMappingPerApplication(HttpServletRequest request,String inurl) {
		Iterator it = urlmappings.keySet().iterator();
		while(it.hasNext()){
			String mapurl = (String) it.next();
			//System.out.println("MAP CHECK ON =*"+mapurl+"* *"+inurl+"*");
			if (inurl.equals(mapurl)) {
				String[] paths = urlmappings.get(mapurl).split(",");
				return paths;
			}
		}
		return null;
	}

}
