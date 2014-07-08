/* 
* Rm.java
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
 * Rm command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Rm {
	
	public static void execute(List<String> buffer,String currentpath,String[] params) {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			buffer.add("> Error smithers down");
			return;
		}
		
		if (params.length==1) {
			buffer.add("> rm ");
			buffer.add("missing filename");	
			return;
		} else {
			String[] parts = currentpath.split("/");
			if (parts.length<3) {
				buffer.add("> rm "+params[1]);
				buffer.add("not allowed to delete here (top level)");	
				return;
			}
		}
		
		buffer.add("> rm "+params[1]+" done");		
		//String result = LazyHomer.sendRequestBart("DELETE",currentpath+params[1],null,null);
		String result = smithers.delete(currentpath+params[1],null,null);
		System.out.println("R="+result);
	}
}
