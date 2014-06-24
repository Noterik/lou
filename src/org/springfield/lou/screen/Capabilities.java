/* 
* Capabilities.java
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

package org.springfield.lou.screen;

import java.util.HashMap;
import java.util.Map;

/**
 * Capabilities
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.screen
 *
 */
public class Capabilities {
	
	private Map<String, String> capabilities;
	public final int MODE_GENERIC = 1;
	public final int MODE_IPHONE_PORTRAIT = 2;
	public final int MODE_IPHONE_LANDSCAPE = 3;
	public final int MODE_IPAD_PORTRAIT = 4;
	public final int MODE_IPAD_LANDSCAPE = 5;
	public final int MODE_APHONE_PORTRAIT = 6;
	public final int MODE_APHONE_LANDSCAPE = 7;
	public final int MODE_ATABLED_PORTRAIT = 8;
	public final int MODE_ATABLED_LANDSCAPE = 9;
	
	/**
	 * Constructor for the Capabilities Class
	 */
	public Capabilities(){
		this.capabilities = new HashMap<String, String>();
	}
	
	/**
	 * 
	 * @param key of the capability (usualy the name of the capability)
	 * @return String value of the capability with the key specified
	 */
	public String getCapability(String key){	
		return this.capabilities.get(key);
	}	
	
	/**
	 * adds a capability for the screen this object belongs to
	 * @param key the name of the capability
	 * @param value the value of the capability
	 */
	public void addCapability(String key, String value){
		this.capabilities.put(key, value);
	}
	
	public Map<String, String> getCapabilities(){
		return this.capabilities;
	}
	/**
	 * 
	 * @return the number of capabilities this object has
	 */
	public int getLength(){
		return this.capabilities.size();
	}
	
	public int getDeviceMode() {
		String ua = getCapability("useragent");
		if (ua!=null) {
			if (ua.indexOf("iPhone")!=-1) {
				String o = getCapability("orientation");
				if (o.equals("0")) {
					return MODE_IPHONE_PORTRAIT;
				} else {
					return MODE_IPHONE_LANDSCAPE;	
				}
			} else if (ua.indexOf("iPad")!=-1) {
				String o = getCapability("orientation");
				if (o.equals("0")) {
					return MODE_IPAD_PORTRAIT;
				} else {
					return MODE_IPAD_LANDSCAPE;	
				}	
			} else if (ua.indexOf("Android")!=-1) {
				String o = getCapability("orientation");
				System.out.println("OOO="+o);
				if (o.equals("90") || o.equals("-90")) {
					return MODE_ATABLED_PORTRAIT;
				} else {
					return MODE_ATABLED_LANDSCAPE;	
				}	
			}
		}
		return MODE_GENERIC;
	}
	
	public String getDeviceModeName() {
		int mode = getDeviceMode();
		switch (mode) {
			case MODE_GENERIC: return null;
			case MODE_IPHONE_PORTRAIT : return "iphone_portrait"; 
			case MODE_IPHONE_LANDSCAPE : return "iphone_landscape";
			case MODE_IPAD_PORTRAIT : return "ipad_portrait"; 
			case MODE_IPAD_LANDSCAPE : return "ipad_landscape"; 
			case MODE_APHONE_PORTRAIT : return "aphone_portrait";
			case MODE_APHONE_LANDSCAPE : return "aphone_landscape"; 
			case MODE_ATABLED_PORTRAIT : return "atabled_portrait";
			case MODE_ATABLED_LANDSCAPE : return "atabled_landscape"; 
		}
		return null;
	}
}