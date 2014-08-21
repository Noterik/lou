/* 
* XMLHelper.java
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

package org.springfield.lou.tools;

/**
 * XMLHelper
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.tools
 */
public class XMLHelper {	
	/**
	 * 
	 * @param id
	 * @return a String representation of an XML containing screen/properties/id, where id has the value of the id parameter
	 */
	public static String createScreenIdFSXML(String id,Boolean clean) {
		if (!clean) id = "appreset";
		String str="<fsxml>" +
						"<screen>" +
							"<properties>" +
								"<screenid>" + id+ "</screenid>" +
							"</properties>" +
						"</screen>" +
				  "</fsxml>";
		return str;
	}
}
