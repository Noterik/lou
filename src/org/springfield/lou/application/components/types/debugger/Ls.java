/* 
* Ls.java
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Ls command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Ls {

	public static boolean execute(List<String> buffer,String currentpath,String[] params,String[] ignorelist) {
		buffer.add("> dir");
		String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			buffer.add("> Error smithers down");
			return false;
		}
		String nodes = smithers.get(currentpath,xml,"text/xml");
		List<String> dirs = new ArrayList<String>();
 		try { 
			Document result = DocumentHelper.parseText(nodes);
			
			for(Iterator<Node> iter = result.getRootElement().nodeIterator(); iter.hasNext(); ) {
				Element main = (Element)iter.next();
				if (currentpath.endsWith(main.getName()+"/")) {
					String dir = main.attributeValue("id");
					if (!dirs.contains(dir)) {
						if (!Arrays.asList(ignorelist).contains(dir)) {
							dirs.add(dir);
						}
					}	
				} else if (currentpath.endsWith("/properties/")) {
					for(Iterator<Node> iter2 = main.nodeIterator(); iter2.hasNext(); ) {
						Element child = (Element)iter2.next();
						if (child.getName().equals("properties")) {
							for(Iterator<Node> iter3 = child.nodeIterator(); iter3.hasNext(); ) {
								Element prop = (Element)iter3.next();
								String dir = prop.getName();
								if (!dirs.contains(dir)) {
									if (!Arrays.asList(ignorelist).contains(dir)) {
										dirs.add(dir);
									}
								}
							}
						}
					}
				} else {
					for(Iterator<Node> iter2 = main.nodeIterator(); iter2.hasNext(); ) {
						Element child = (Element)iter2.next();
						String dir = child.getName();
						if (!dirs.contains(dir)) {
							if (!Arrays.asList(ignorelist).contains(dir)) {
								dirs.add(dir);
							}
						}
					}
				}
			}
			// ok loop all the results
			String body = "";
			for (int i=0;i<dirs.size();i++) {
				if (dirs.size()<10) {
					body += dirs.get(i)+" \n";
				} else {
					body += dirs.get(i)+" \t";
				}
			}
			buffer.add(body);	
		} catch(Exception e) {
			e.printStackTrace();
		}
 		return true;
	}
}
