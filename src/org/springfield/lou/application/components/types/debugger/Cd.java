/* 
* Cd.java
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
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Cd command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Cd {
	
	public static String execute(List<String> buffer,String currentpath,String[] params) {
		String oldpath = currentpath;
		if (params.length>1) {
			String wantedpath = params[1];
			if (wantedpath.equals("../") || wantedpath.equals("..")) {
				currentpath = currentpath.substring(0,currentpath.length()-1);
				int pos = currentpath.lastIndexOf("/");
				if (pos!=-1) currentpath = currentpath.substring(0,pos);
			} else if (wantedpath.indexOf("/")==0) {
				currentpath = wantedpath;
			} else {
				currentpath += wantedpath;
			}
			if (!currentpath.endsWith("/")) currentpath += "/";
			
			buffer.add("> cd "+currentpath);	
			// check if its ok
			String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
			//String nodes = LazyHomer.sendRequestBart("GET",currentpath,xml,"text/xml");
			
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers==null) {
				buffer.add("> Error smithers down");
				return currentpath;
			}
			String nodes = smithers.get(currentpath,xml,"text/xml");
			
	 		try { 
				Document result = DocumentHelper.parseText(nodes);
				if (result.asXML().indexOf("<message>No data available</message>")!=-1 || result.asXML().indexOf("<totalResultsReturned>0</totalResultsReturned></properties>")!=-1) {
					buffer.add("No such entry");	
					return oldpath;
				}
	 		} catch(Exception e) {
				buffer.add("No such entry");
				return oldpath;
			}
		} else {
			buffer.add("> cd");
		}
		return currentpath;
	}
}
