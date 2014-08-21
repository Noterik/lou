/* 
* RemoteServlet.java
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

package org.springfield.lou.application.remoteregister;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springfield.lou.application.ApplicationManager;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.servlet.LouServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.springfield.lou.application.Html5Application;

/**
 * RemoteServlet
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.remoteregister
 *
 */
public class RemoteServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(LouServlet.class);
	private static final String password = "password";
	private static final long serialVersionUID = 42L;
	private Map<String, String> router;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoteServlet() {
        super();
        System.out.println("RemoteServlet object created");
        router = new HashMap<String, String>();
        // TODO Auto-generated constructor stub
    }

	
	public static void handle(String xml,String msg,String ipnumber) {		
		if(ApplicationManager.instance().getRouter().containsKey(ipnumber)){
//			forwardRequest(xml, ApplicationManager.instance().getRouter().get(ipnumber), ipnumber);
			forwardRequest(msg, ApplicationManager.instance().getRouter().get(ipnumber), ipnumber);

			return;
		}
		System.out.println("unregistered remote.");
		handleNewRemote(xml, ipnumber);
	}
	
	private static void forwardRequest(String message, Html5ApplicationInterface app, String ip){
		app.getComponentManager().getComponent("trafficlight").put("external", message);
	}
	
	private static void handleNewRemote(String xml, String ip){
		String currentInput;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
			NodeList messages = ((Element) doc.getElementsByTagName("fsxml").item(0)).getElementsByTagName("message");
			
			for(int i=0;i<messages.getLength();i++){
				Element message = (Element) messages.item(i);
				String msg = ((Element)message.getElementsByTagName("properties").item(0))
						.getElementsByTagName("msg").item(0).getTextContent();
				String buttonClicked = null;
				System.out.println(msg);
				if(msg.indexOf("buttonClicked(")!=0) return;
				buttonClicked = msg.substring(msg.indexOf("(")+1, msg.lastIndexOf(")"));
				currentInput = ApplicationManager.instance().getExternalMessages().get(ip);
				if(currentInput==null) currentInput = "";
				if(currentInput.length()>3) currentInput = currentInput.substring(1,4);
				currentInput += buttonClicked;
				System.out.println("currentInput: " + currentInput);
				if(ApplicationManager.instance().getExternalInterfaces().containsKey(new Integer(Integer.parseInt(currentInput)))){
					System.out.println("match found!!!");
					ApplicationManager.instance().getRouter().put(ip, ApplicationManager.instance()
							.getExternalInterfaces().get(Integer.parseInt(currentInput)));
					//set paired status true for this application so notifications will not get the notification anymore
					Html5Application temp = ((Html5Application)ApplicationManager.instance()
					.getExternalInterfaces().get(Integer.parseInt(currentInput)));
					
					temp.setPaired(true);
					temp.getComponentManager().getComponent("notification").put("", "closelong()");
				}
				
				ApplicationManager.instance().getExternalMessages().put(ip, currentInput);
				
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(NumberFormatException e){
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
