/* 
* Save.java
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

/**
 * Save command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Save {
	public static void execute(List<String> buffer, String currentpath,String[] params) {
		buffer.add("> not ported yet");
		return;
		
		/*
		if (params.length==3) {
			String wantednode = params[1];
			String savecode = params[2];
			if (savecode.equals("iamsure")) {
				buffer.add("> saving node "+wantednode);
				String posturl = "/domain/internal/service/smithers/nodes/"+LazyHomer.getSmithersIpNumber()+"/properties/exporturl";
				System.out.println("POSTURL="+posturl);
				String body = currentpath+wantednode;
				System.out.println("WANTEDNODE="+body);
				LazyHomer.sendRequest("PUT",posturl,body,"text/xml");
			} else {
				buffer.add("> save code not correct");	
			}
		} else {
			buffer.add("> missing params need nodename and savecode");
		}
		*/
	}
}
