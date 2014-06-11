/* 
* Cp.java
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
 * Cp command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Cp {
	
	public static void execute(List<String> buffer,String currentpath,String[] params) {
		Boolean recursive = false;
		String src = params[1];
		String dest = params[2];
		String options = "";
		if (params[1].equals("-r")) {
			recursive = true;
			options = "-r";
			src = params[2];
			dest = params[3];
			buffer.add("> cp -r "+src+" "+dest);
		} else {
			buffer.add("> cp "+src+" "+dest);
		}
		if (!src.startsWith("/")) {
			src = currentpath+src;
		}
		if (!dest.startsWith("/")) {
			dest = currentpath+dest;
		}
		String body = "<fsxml mimetype=\"application/fscommand\" id=\"copy\">";
		body+="<properties>";
		body+="<source>"+src+"</source>";
		body+="<destination>"+dest+"</destination>";
		if (recursive) {
			body+="<params>-r</params>";
		}
		body+="</properties>";
		body+="</fsxml>";
		System.out.println("BODY2="+body);
		String result = LazyHomer.sendRequestBart("POST",currentpath,body,"text/xml");
		System.out.println("R="+result);		
	}	
}
