/* 
* LouRestlet.java
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

package org.springfield.lou.restlet;

import org.restlet.Context;
import org.restlet.routing.Router;

/**
 * LouRestlet
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.restlet
 *
 */
public class LouRestlet extends Router {	
	public LouRestlet(Context cx) {
		super(cx);
		
		this.setRoutingMode(Router.MODE_BEST_MATCH);
		
		// logging resource
		this.attach("/logging",LoggingResource.class);
		
		// default resource
		this.attach("/",LouResource.class);
	}
}
