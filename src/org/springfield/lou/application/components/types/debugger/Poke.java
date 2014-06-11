/* 
* Poke.java
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

import org.springfield.lou.screen.Screen;
import org.springfield.lou.screen.ScreenManager;

/**
 * Poke command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Poke {
	public static void execute(ScreenManager sm,List<String> buffer,String from,String[] params) {
        Screen sfrom = sm.get(from);
        if (sfrom!=null) {
        	String color = "0,0,0";
        	String[] parts = params[1].split(",");
        	int c = Integer.parseInt(parts[1]);
    		buffer.add(">timewarp ... poke "+params[1]);
    		switch (c) {
				case 1 : color = "255,255,255"; break;
				case 2 : color = "136,0,0"; break;
				case 3 : color = "170,255,238"; break;
				case 4 : color = "204,68,204"; break;
				case 5 : color = "0,204,85"; break;
				case 6 : color = "0,0,170"; break;	
				case 7 : color = "238,238,119"; break;	
				case 8 : color = "221,136,85"; break;	
				case 9 : color = "102,68,0"; break;	
				case 10 : color = "255,119,119"; break;	
				case 11 : color = "51,51,51"; break;	
				case 12 : color = "119,119,119"; break;	
				case 13 : color = "170,255,102"; break;	
				case 14 : color = "0,136,255"; break;
				case 15 : color = "178,178,178"; break;	
    		}
        	
        	if (parts[0].equals("53280")) {
        		sfrom.putMsg("debugger","app","color("+color+",0.8)");
        	} else if (parts[0].equals("53281")) {
        		sfrom.putMsg("debugger","app","backgroundcolor("+color+")");
        	} else if (parts[0].equals("646")) {
        		sfrom.putMsg("debugger","app","textcolor("+color+")");
        	}
        }		
	}
}