/* 
* Load.java
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

import java.io.File;
import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.lou.homer.LazyHomer;

/**
 * Load command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Load {
	public static void execute(List<String> buffer, String currentpath,String[] params) {
		buffer.add("> not ported yet ");	
		return;
		/*
		if (params.length==3) {
			String wantedpath = params[1];
			String savecode = params[2];
			if (savecode.equals("iamsure")) {
				buffer.add("> loading node from disk : "+wantedpath+" into this location : "+currentpath);
				String posturl = "/domain/internal/service/smithers/nodes/"+LazyHomer.getSmithersIpNumber()+"/properties/importurl";
				String feedback = checkPaths(currentpath,wantedpath);	
				if (feedback!=null) {
					buffer.add("> "+feedback);	
				} else {
					String body = currentpath+","+wantedpath;
					System.out.println("WANTEDNODE="+body);
					LazyHomer.sendRequest("PUT",posturl,body,"text/xml");
				}
			} else {
				buffer.add("> load code not correct");	
			}
		} else {
			buffer.add("> missing params need nodepath (starting with /domain) and savecode");
		}
		*/
	}
	
	private static String checkPaths(String currentpath,String wantedpath) {
		String result=null;
		if (Fs.isMainNode(currentpath)) {
			System.out.println("MAIN");
		} else {
			System.out.println("SUB");
			String l[] = currentpath.split("/");
			String id = l[l.length-1];
			String type = l[l.length-2];
			// use these to test agains the types from the wanted path
			String wl[] = wantedpath.split("/");
			String wid = wl[wl.length-1];
			String wtype = wl[wl.length-2];
			
			// lets first check if the types and id's match
			if (!id.equals(wid)) {
				return "trying to load different id into the current path";
			} else if (!type.equals(wtype)) {
				return "trying to load different node type into the current path";
			}
			
			// next lets make sure the data is really on disk
			String path = "/springfield/smithers/import/";
			if (LazyHomer.isWindows()) {
				path = "c:\\springfield\\smithers\\import\\";
			}
			File dir = new File(path+wantedpath);
			if (!dir.exists()) {
				return "data you are trying to load not found in the import dir";
			}
		}
		return result;
	}
}