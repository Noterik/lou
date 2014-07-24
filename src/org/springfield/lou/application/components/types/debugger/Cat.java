/* 
* Cat.java
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

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Cat command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Cat {
	
	public static void execute(List<String> buffer, String currentpath,String[] params) {
		boolean recursive = false;
		String name = params[1];
		if (name.equals("-r")) {
			recursive = true;
			name = params[2];
		}
		
		if (recursive) {
			buffer.add("> cat -r "+params[2]);
			String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
			//String result = LazyHomer.sendRequestBart("GET",currentpath+name,xml,"text/xml");
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers==null) {
				buffer.add("> Error smithers down");
				return;
			}
			String result = smithers.get(currentpath+name,xml,"text/xml");
			
			String[] lines = result.split(">");
			for (int i=0;i<lines.length;i++) {
				buffer.add(lines[i]+">");
			}
		} else {
			buffer.add("> cat "+name);
			String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers!=null) {
				String nodes = smithers.get(currentpath+name,xml,"text/xml");
				List<String> dirs = new ArrayList<String>();
				try { 
					Document result = DocumentHelper.parseText(nodes);
					System.out.println("R="+result.asXML());
					String value = result.getRootElement().getText();
				//System.out.println("V="+value+" r="+result.getRootElement().getName());
					buffer.add(value);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
