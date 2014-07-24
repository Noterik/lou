/* 
* Mkdir.java
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

package org.springfield.lou.application.components.types.debugger;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Mkdir command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Mkdir {
	
	public static void execute(List<String> buffer,String currentpath,String[] params) {
		
		// find out what type of node we are ?
		String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
		//String nodes = LazyHomer.sendRequestBart("GET",currentpath,xml,"text/xml");
		
		
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			buffer.add("> Error smithers down");
			return;
		}
		String nodes = smithers.get(currentpath,xml,"text/xml");
		
 		try { 
			Document result = DocumentHelper.parseText(nodes);
			String lastid = currentpath.substring(0,currentpath.length()-1);
			lastid = lastid.substring(lastid.lastIndexOf("/")+1);
			Element node = result.getRootElement().element(lastid);
			if (node!=null) {
				String newbody = "<fsxml><properties></properties></fsxml>";
		    	String postpath = currentpath+params[1]+"/properties";
				//LazyHomer.sendRequest("PUT",postpath,newbody,"text/xml");
				smithers.put(postpath,newbody,"text/xml");
			} else {
				String newbody = "<fsxml>";
		    	newbody+="<"+params[1]+"><properties>";	
		    	newbody+="</properties></"+params[1]+"></fsxml>";		    	
		    	
				//LazyHomer.sendRequest("PUT",currentpath+"properties",newbody,"text/xml");
		    	smithers.put(currentpath+"properties",newbody,"text/xml");
			}
			buffer.add("> mkdir "+params[1]);
 		} catch(Exception e) {
			e.printStackTrace();
		}			
	}
}
