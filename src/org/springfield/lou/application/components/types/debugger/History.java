/* 
* History.java
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

import java.util.*;

/**
 * History command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class History {
	
	private static List<String> history = new ArrayList<String>();
	private static int pos = 0;
	
	public static String prev() {
		if (pos>0) {
			return history.get(pos--);
		}
		return null;
	}
	
	public static String next() {
		if (pos<history.size()) {
			return history.get(pos++);
		}
		return null;
	}
	
	public static void history(List<String> buffer,String[] params) {
		int j = 1;
		for(int i=0;i<history.size();i++) {
			buffer.add("> "+(j++)+" "+history.get(i));
		}
	}
	
	public static void add(String[] cmds) {
		String line = "";
		for (int i=0;i<cmds.length;i++) {
			if (line==null) {
				line = cmds[i];
			} else {
				line +=" "+cmds[i];
			}
		}
		history.add(line);
		pos = history.size()-1;
	}
}
