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
import java.util.Enumeration;
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
import org.springfield.lou.ServiceHandler;
import org.springfield.lou.application.ActionList;
import org.springfield.lou.application.ApplicationManager;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.application.components.types.proxy.ProxyComponent;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Capabilities;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.tools.XMLHelper;
import org.springfield.mojo.http.ProxyHandler;
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
		response.addHeader("Access-Control-Allow-Headers", "Content-Type,Range,If-None-Match,Accept-Ranges");
		response.addHeader("Access-Control-Expose-Headers", "Content-Range");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type,Range,If-None-Match,Accept-Ranges");
		response.addHeader("Access-Control-Expose-Headers", "Content-Range");
		System.out.println("METHOD="+request.getMethod()+" "+request.getRequestURI());
		String mt = request.getContentType();
		if (mt!=null && mt.indexOf("text/put")!=-1) { // need to check who made this and why (daniel)
			doPut(request,response);
			return;
		}
		
		//System.out.println("REQ="+request.getRequestURI()+" PARAMS="+request.getQueryString()+" MT="+request.getContentType());
		String body = request.getRequestURI();
		if(request.getParameter("method")!=null) {
			if(request.getParameter("method").equals("post")){
				//System.out.println("going for post");
				doPost(request, response);
				return;
			}
		}
		
		// if proxy request send it to Servicehandler 
		if (body.startsWith("/lou/proxy/")) {
			ProxyHandler.get("lou",request,response);
			return;
		}
		
		// need to move to be faster
		String params = request.getQueryString();
		String hostname = request.getHeader("host");
		String[] paths = urlMappingPerApplication(hostname,body);
		
		
		if (paths!=null) {
			//check if url trigger also contains params
			String triggerParams = null;
			if (paths[0].indexOf("?") != -1) {
				triggerParams = paths[0].substring(paths[0].indexOf("?")+1);
				paths[0] = paths[0].substring(0,paths[0].indexOf("?"));
			}
			
			body = paths[0];
			
			if (params!=null) {
				params += "&actionlist="+paths[1];
				if (triggerParams != null) {
					params += "&"+triggerParams;
				}
			} else {
				params = "actionlist="+paths[1];
				if (triggerParams != null) {
					params += "&"+triggerParams;
				}
			}
		}
		
		//System.out.println("PARAMS2="+params);
		
		int pos = body.indexOf("/html5application/");
		if (pos!=-1) {
			pos = body.indexOf("/lou/domain/");
			if (pos!=0) {
				System.out.println("Fixedget="+body);
				body = body.substring(pos);
				System.out.println("Fixedout="+body);
			}
			doIndexRequest(body,request,response,params);
		} else {
			// should we report something back ?
		}
		return;
	}	
	
	private void doIndexRequest(String uri,HttpServletRequest request, HttpServletResponse response,String params) {
		try {	
			response.addHeader("Access-Control-Allow-Origin", "*");  
			response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type,Range,If-None-Match,Accept-Ranges");
			response.addHeader("Access-Control-Expose-Headers", "Content-Range");
			
			response.setContentType("text/html; charset=UTF-8");
			OutputStream out = response.getOutputStream();
			//PrintWriter out = response.getWriter();
			//System.out.println("INDEX REQ="+request.getRequestURI());
			//String params = request.getQueryString();
			String user = null;
			String nameapp = "test";
			
			int pos = uri.indexOf("/user/");
			if (pos!=-1) {
				user = uri.substring(pos+6);
				pos = user.indexOf("/");
				user = user.substring(0,pos);
			}
	
			pos = uri.indexOf("/html5application/");
			if (pos!=-1) {
				nameapp = uri.substring(pos+18);
				nameapp = nameapp.indexOf("?") == -1 ? nameapp : nameapp.substring(0, nameapp.indexOf("?"));
				if (nameapp.equals("")) nameapp="test"; // weird has to be moved to manager only needed for loading of libs
			}
			String fullappname = uri.substring(4);
			Html5ApplicationInterface app = ApplicationManager.instance().getApplication(fullappname);
			if (app==null) { // no such app is available
				System.out.println("MISSING APP REQUESTED="+fullappname);
				String body ="No app under that name found";
				out.write(body.getBytes());
				out.flush();
				out.close();
				return;
			}
			
			
			//String body = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
			//body+="<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
			// CWI / AngularJS compatible
			//String body="<!doctype html>";
			//body+="<html ng-app=\"tkkDemoApp\">";
			String body = "<!DOCTYPE html PUBLIC \"-//HbbTV//1.1.1//EN\" \"http://www.hbbtv.org/dtd/HbbTV-1.1.1.dtd\">";
			body += "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
			body+="<head>\n";
			//String favicon = "http://www.euscreen.eu/images/favicon.png";
			String favicon = app.getFavicon();
			if (favicon!=null) {
				body+="<link rel=\"icon\" type=\"image/png\" href=\""+favicon+"\"/>";
			}
			body+="<meta http-equiv=\"Content-Type\" content=\"application/vnd.hbbtv.xml+xhtml; utf-8\" />";
			body+="<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />";
			body+="<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" />";
			body+="<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n";
			body+="<meta name=\"viewport\" content=\"width=device-width, user-scalable = no,initial-scale=1.0; maximum-scale=1.0;\">";
			body+=app.getMetaHeaders(request);
			body+="<script type=\"text/javascript\">if (!window.console) window.console = {};if (!window.console.log) window.console.log = function () { };</script>";
			body+="<script language=\"javascript\" type=\"text/javascript\">var LouSettings = {\"lou_ip\": \"" + LazyHomer.getExternalIpNumber() + "\", \"lou_port\": \"" + LazyHomer.getBartPort() + "\", \"user\": \"" + user + "\", \"app\": \"" + nameapp + "\", \"fullapp\": \"" + fullappname + "\", \"appparams\": \"" + params + "\"}</script>\n";
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/jquery-1.8.0.js\"></script>\n";
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"http://code.jquery.com/ui/1.11.2/jquery-ui.js\"></script>\n";
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/jquery.ui.touch-punch.min.js\"></script>\n";
			
			body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/jquery.xdomainrequest.min.js\"></script>\n";
			String libs = getLibPaths(nameapp);
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
			
			//System.out.println("USER-AGENT="+request.getHeader("user-agent"));
			String agent = request.getHeader("user-agent");
			if (agent != null && agent.indexOf("HbbTV/1.1.1")==-1) {
				body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/eddie.js?cache\"></script>\n";
				body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/main.js\"></script>\n";
				body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/stacktrace.js\"></script>\n";
			} else {
				body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/eddie_hbbtv.js?cache\"></script>\n";
				body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/main_hbbtv.js\"></script>\n";
				body+="<script language=\"javascript\" type=\"text/javascript\" src=\"/eddie/js/stacktrace_hbbtv.js\"></script>\n";	
			}
			body+="<title></title>\n";
			body+="</head>\n";
			
			// CWI / AngularJS compatible
			//body+="<body ng-view>\n";
			body+="<body>\n";

			body+="<div id=\"screen\" />\n";
			body+="</body>\n";
			body+="</html>\n";
			out.write(body.getBytes());
			out.flush();
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
		doPut(request,response);
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type,Range,If-None-Match,Accept-Ranges");
		response.addHeader("Access-Control-Expose-Headers", "Content-Range");
		//read the data from the put request
		
		//System.out.println("PUT REQ="+request.getRequestURI());
		
		InputStream inst = request.getInputStream();
		String data;
		
		// reads the data from inputstring to a normal string.
		java.util.Scanner s = new java.util.Scanner(inst).useDelimiter("\\A");
		data = (s.hasNext()) ? s.next() : null;
		
		if (data==null) {
			return;
		}
		
		//System.out.println("DATA="+data);

		Map<String,String[]> params = request.getParameterMap();
		// lets find the correct nlication
		Html5ApplicationInterface app = null;
		String url = request.getRequestURI();
		int pos = url.indexOf("/domain/");
		if (pos!=-1) {
			String tappname = url.substring(pos);
			app = ApplicationManager.instance().getApplication(tappname);
		}
		
		if (data.indexOf("put(")==0) {
			app.putData(data);
			return;
		}
		
		String gaintest = request.getParameter("type");
		
		if (gaintest!=null && gaintest.equals("gain")) {
			System.out.println("GAINDATA="+data);
			app.externalGainEvent(request, data);
			return;
		}
		
		
		
		if (data.indexOf("stop(")==0) {
			System.out.println("RECIEVED STOP FROP CLIENT");
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
							//screen.wait();
							screen.wait(60*1000);
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
				System.out.println("lost flow why ? screenId="+screenId+" "+app.getScreen(screenId));
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
	
	private String[] urlMappingPerApplication(String host,String inurl) {
		//System.out.println("HOST="+host+" URL="+inurl);
		Iterator it = urlmappings.keySet().iterator();
		while(it.hasNext()){
			String mapurl = (String) it.next();
			String lmapurl = mapurl;
			int pos = mapurl.indexOf("@");
			if (pos!=-1) {
				String checkhost = mapurl.substring(0,pos);
				mapurl = mapurl.substring(pos+1);
				if (checkhost.equals(host) && inurl.equals(mapurl)) {
					String[] paths = urlmappings.get(lmapurl).split(",");
					return paths;
				}
			} else {
				if (inurl.equals(mapurl)) {
					String[] paths = urlmappings.get(mapurl).split(",");
					return paths;
				}
			}
		}
		return null;
	}

}
