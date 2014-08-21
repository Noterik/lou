/* 
* JavascriptInjector.java
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

package org.springfield.lou.tools;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * JavascriptInjector
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.tools
 */
public class JavascriptInjector {
	
	private static final String TRY = "\n\ntry{";
	
	private static final String CATCH = "\n}catch(err){\n"
			+"var trace = printStackTrace();\n"
		    +"console.error(\"filename.js: \"+err.message + \"\\n\\n\" + trace.join('\\n\\n'));\n"
		    + "}\n";
	
	private static final String HAMMER_BINDINGS = "$('/elements/').hammer({"
		+ "prevent_default: true,"
		+ "drag_vertical: true,"
		+ "drag_min_distance: 40"
		+ "}).bind('/events/', function(event){/function/(event);});";
	
	private static final String COMPONENT_GLOBAL_DEF = "\n"
			+ "var comp = /Component/.prototype ? new /Component/() : /Component/();\n"
			+ "if(!window.comps){window.comps = {};}"
			+ "if(!window.comps['/component/']){window.comps['/component/'] = {};}"
			+ "window.comps['/component/']['/target/'] = comp;"
			+ "if(typeof comp.setId == 'function'){comp.setId('/target/');}"
			+ "if(!window.components){ window.components = {}; }\n"
			+ "window.components['/component/'] = comp;\n"
			+ "$('#/target/').data('component', comp);\n"
			+ "function /component/_putMsg(msg) {\n"
			+ "components./component/.putMsg(msg);\n"
			+ "}";
	
	/**
	 * Injects try/catch clause in function. It searches for 
	 * 'debug_function' tags in the JavaScript file, finds the
	 * next opening curly bracket and inserts the try statements.
	 * Then it finds the matching closing curly bracket of the 
	 * function and inserts the catch statement before it. Does so
	 * for all the appearances of 'debug_function'. Finally replaces
	 * all 'debug_function' with 'function'.
	 * @param source1 the content of the JavaScript file
	 * @param filename the absolute path to the XML file
	 * @return the content of the JavaScript file including the try/catch statement
	 */
	public static String injectTryCatch(String source1, String filename) {
		String source = source1;
		String body;
		String prefunction, function, postfunction;
		int point=0, openingbrace, closingbrace, functionpoint;
		while((functionpoint=source.indexOf("springfield_function", point))!=-1) {
		
			body = "";
			openingbrace = source.indexOf("{", functionpoint);
			closingbrace = findClosingBrace(source, openingbrace+1);
			
			//if something goes wrong, return the original file
			if(closingbrace==-1) return source1;
			
			prefunction = source.substring(0, openingbrace+1);
			function = source.substring(openingbrace+1, closingbrace);
			postfunction = source.substring(closingbrace);
			
			body += prefunction;
			body += JavascriptInjector.TRY;
			body += function;
			body += JavascriptInjector.CATCH.replace("filename.js", filename.substring(filename.indexOf("\\")+1));
			body+= postfunction;
			
			source = body;
			
			point = openingbrace + 7;
				
		}		
		source = source.replace("springfield_function", "function");
		
		return source;
	}	
	
	/**
	 * Adds the Hammer.js bindings in the end of the file. It gets the properties
	 * from the XML file in the same directory as the JavaScript file.
	 * 
	 * @param source
	 * @param xmlfile
	 * @return
	 */
	public static String injectTouchBindings(String source, String xmlfile) {
		String newBody = source;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlfile);
			
			NodeList bindings = doc.getElementsByTagName("binding");
			String appendBody = "";
			for(int i=0;i<bindings.getLength();i++) {
				NodeList properties = ((Element)bindings.item(i))
						.getElementsByTagName("properties")
						.item(0).getChildNodes();
				
				String events = ((Element)properties).getElementsByTagName("events").item(0).getTextContent();
				String function = ((Element)properties).getElementsByTagName("function").item(0).getTextContent();
				String elements = ((Element)properties).getElementsByTagName("elements").item(0).getTextContent();
				
				appendBody += JavascriptInjector.HAMMER_BINDINGS;
				
				appendBody = appendBody.replaceAll("/events/", events);
				appendBody = appendBody.replaceAll("/elements/", elements);
				appendBody = appendBody.replaceAll("/function/", function);
				appendBody += "\n\n";
			}
			
//			newBody = newBody.replace("return self;", appendBody + "\n\nreturn self;");
			newBody += appendBody;
			return newBody;
		} catch (Exception e) {
			e.printStackTrace();
			return source;
		} 
	}
		
	public static String injectComponentGlobalDefinitions(String source, String component, String target) {		
		String newBody = source;
		
		newBody += JavascriptInjector.COMPONENT_GLOBAL_DEF.replaceAll("/component/", component).replaceAll("/target/", target).replaceAll("/Component/", component.substring(0,1).toUpperCase()+component.substring(1));
		
		return newBody;
	}	
	
	private static int findClosingBrace(String source, int index) {
		
		int closingbraces=0, openingbraces=1, linecounter=0;
		
		for(int i=index;i<source.length();i++) {
			if(source.charAt(i) == '{') openingbraces++;
			else if(source.charAt(i) == '}') closingbraces++;
			
			if(closingbraces==openingbraces) {
				return i;
			}
		}		
		return -1;
	}
}
