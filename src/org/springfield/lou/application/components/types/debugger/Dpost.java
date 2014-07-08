/* 
* Dpost.java
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
 * Dpost command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Dpost {

	public static void execute(List<String> buffer,String currentpath,String[] params) {
		
		String oldpath = currentpath;
		buffer.add("> dpost "+currentpath);	
		// check if its ok
		String body = "<fsxml mimetype=\"application/fscommand\" id=\"dynamic\">";
		body+="<properties>";
		body+="<handler>"+"/dynamic/presentation/playout/flash"+"</handler>";
		body+="<virtualpath>videoplaylist/tagging</virtualpath>";
		body+="</properties>";
		body+="</fsxml>";
		
		ServiceInterface smithers = ServiceManager.getService("smithers");
		String nodes = smithers.post(currentpath,body,"text/xml");
		
	 	try { 
			Document result = DocumentHelper.parseText(nodes);
			System.out.println("DPOST="+result.asXML());
	 	} catch(Exception e) {
	 		buffer.add("Error");
		}
	}	
}
