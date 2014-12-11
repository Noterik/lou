/* 
* LocationManager.java
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

package org.springfield.lou.location;

import java.util.HashMap;
import java.util.Map;

/**
 * LocationManager
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.location
 *
 */
public class LocationManager {

	private static Map<String, Location> openlocations = new HashMap<String, Location>();
	
    public static void put(Location l) {
    	openlocations.put(l.getId(),l);
    }
    
    public static void remove(String id){
    	openlocations.remove(id);
    }
    
    public Location get(String locationid) {
    	return openlocations.get(locationid);
    }
    
    public Map<String, Location> getLocation(){
    	return openlocations;
    }
    
    public static int size(){
    	return openlocations.size();
    }
}
