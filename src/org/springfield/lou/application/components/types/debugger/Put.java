/* 
* Put.java
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

import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Put command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Put {
	
	public static void execute(List<String> buffer,String currentpath,String[] params) {
		if (params.length==1) {
			buffer.add("> put");
			buffer.add("property name and value needed");
			return;
		} else if(params.length==2) {
			buffer.add("> put "+params[1]);
			buffer.add("property name and value needed");
			return;
		}
		if (currentpath.endsWith("/properties/")) {
			String newbody = params[2];
			if (params.length>2) {
				for (int i=3;i<params.length;i++) {
					newbody += " "+params[i];
				}
			}
			String postpath = currentpath+params[1];
			//LazyHomer.sendRequest("PUT",postpath,newbody,"text/xml");
			
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers==null) {
				buffer.add("> Error smithers down");
				return;
			}
			smithers.put(postpath,newbody,"text/xml");
			
			buffer.add("> put "+params[1]+" "+newbody);
		} else {
			buffer.add("> put");
			buffer.add("put only possible in property node");
		}
	}
}
