/* 
* Filecomplete.java
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
import java.util.Iterator;
import java.util.List;

import org.springfield.fs.*;

/**
 * Filecomplete command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Filecomplete {
	public static String execute(List<String> buffer, String currentpath,String[] params) {
		List<String> hits = new ArrayList<String>();
		
		String key = params[params.length-1];
	
		Boolean main = Fs.isMainNode(currentpath);
		if (currentpath.endsWith("properties/")) {
			FsNode props = Fs.getNode(currentpath.substring(0,currentpath.length()-12));
			for(Iterator<String> iter = props.getKeys(); iter.hasNext(); ) {
				String pkey = (String)iter.next();
				if (pkey.indexOf(key)==0) {
					if (!hits.contains(pkey)) {
						hits.add(pkey);
					}
				}
			}
		} else {
			List<FsNode> nodes = Fs.getNodes(currentpath,0);
			for (int i=0;i<nodes.size();i++) {
				FsNode node = nodes.get(i);
				String nkey = node.getName();
				if (main) nkey = node.getId();
				if (nkey.indexOf(key)==0) {
					if (!hits.contains(nkey)) {
						hits.add(nkey);
					}
				}
			}
			if (!main && !hits.contains("properties") && "properties".indexOf(key)==0) {
				hits.add("properties");
			}
		}
		if (hits.size()!=-1) {
			if (hits.size()==1) {
				String newcommand = null;
				for (int i=1;i<params.length-1;i++) {
					if (i==1) {
						newcommand = params[i];
					} else {
						newcommand += " "+params[i];
					}
				}
				return newcommand+" "+hits.get(0);
			} else {
				String line = null;
				buffer.add("> ");
				for (int i=0;i<hits.size();i++) {
					if (line==null) {
						line = hits.get(i);
					} else {
						line +=" "+hits.get(i);	
					}
					if (line.length()>60) {
						buffer.add("> "+line);
						line = null;
					}
				}
				if (line!=null) {
					buffer.add("> "+line);
				}
				
				int len = params.length;
				String overlap = getLongestOverlap(hits);
				if (overlap.length()>0) len = len -1;
				
				// return the current command (weird)
				String newcommand = null;
				for (int i=1;i<len;i++) {
					if (i==1) {
						newcommand = params[i];
					} else {
						newcommand += " "+params[i];
					}
				}
				return newcommand+" "+overlap;
			}
		}
		return null;
	}
	
	private static String getLongestOverlap(List<String> hits) {
		String result = "";
		
		Boolean done = false;
		int i = 0;
		try {
		while (!done) {
			Character a = hits.get(0).charAt(i);
			// compare if all are the same
			for (int j=1;j<hits.size();j++) {
				Character b = hits.get(j).charAt(i);
				if (!a.equals(b)) {
					done = true;
				}
			}
			result += a;
			i++;
		}
		} catch(Exception e) {
			// out of range is also fine as a end;
		}
		return result;
	}
}
