/* 
* Ln.java
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

import org.springfield.fs.Fs;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Ln command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Ln {
	
	public static boolean execute(List<String> buffer,String currentpath,String[] params) {
		System.out.println("DOING A LN");
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			buffer.add("> Error smithers down");
			return false;
		}
		
		String postpath = currentpath+params[1];
		if (Fs.isMainNode(postpath)) {
			String newbody = "datatype=attributes&referid="+params[2];
			//String result = LazyHomer.sendRequest("POST",postpath,newbody,"text/xml");
			String result = smithers.post(postpath,newbody,"text/xml");
			buffer.add("> ln(m) "+params[1]+" "+newbody);
			System.out.println("RESULT="+result);
		} else {
			String newbody = "<fsxml><attributes><referid>"+params[2]+"</referid></attributes></fsxml>"; 
			//LazyHomer.sendRequest("PUT",postpath+"/attributes",newbody,"text/xml");
			smithers.put(postpath+"/attributes",newbody,"text/xml");

			buffer.add("> ln(i) "+params[1]+" "+newbody);
		}
		return true;
	}	
}
