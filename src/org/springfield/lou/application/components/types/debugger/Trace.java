/* 
* Trace.java
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

import org.springfield.lou.application.components.types.DebuggerComponent;
import org.springfield.lou.homer.LazyMarge;

/**
 * Trace command for filesystem debugger
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.debugger
 *
 */
public class Trace {
		public static void execute(List<String> buffer,DebuggerComponent debugger,String[] params) {
	
		if (params.length==1) {
			buffer.add("> need more params [ADD],[LIST],[DEL] ");	
			return;
		}
		String type = params[1];
		if (type.equals("add")) {
			//LazyMarge.addObserver("/domain/internal/*", debugger);
			LazyMarge.addObserver("/domain/webtv/*", debugger);
		}
			
		buffer.add("> trace "+type);
	}
		
	public static void remoteSignal(List<String> buffer,String from,String method,String url) {
		buffer.add("trace> "+method+" "+from+" "+url);
	}
		
}
