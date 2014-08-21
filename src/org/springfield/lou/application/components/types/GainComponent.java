/* 
* GainComponent.java
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

package org.springfield.lou.application.components.types;

import org.springfield.lou.application.components.BasicComponent;

/**
 * GainComponent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types
 *
 */
public class GainComponent extends BasicComponent {
	
	public GainComponent(){
		super();
		this.id = "gain";
	}
	
	public void put(String from,String msg) {
        int pos = msg.indexOf("(");
        
        if (pos!=-1) {
	        String command = msg.substring(0,pos);
	        String content = msg.substring(pos+1,msg.length()-1);
	        String[] args = content.split(",");
	        String contentType = "application/json";
	        String user = getApplication().getScreenManager().get(from).getUserName();
	        if(user==null) user = "guest";
	        System.out.println(user);
	        System.out.println(getApplication().getId());
	        
	        //TODO: add the restlet URI for the component as the ID
	        String body = "{" +
							"\"accountId\" : \"UA-000000-0\"," + 
				        	"\"client\" : {" +
							" \"type\" : \""+getApplication().getFullId()+"\"," +
							" \"version\" : \"0.1\"" +
							"}," +
							"\"object\" : {" +
							" \"id\" : \"2453132\"," +
							" \"title\" : \"News 1\"," +
							" \"uri\": \"/news1?a=20&b=30\"" +
							"}," +
							"\"user\" : {" +
							" \"id\" : \""+user+"\"" +
							"}," +
							"\"interaction\" : {" +
							" \"type\" : \"event\"" +
							"}," +
							"\"attributes\" : {" +
							" \"event\" : {" +
							" \"category\" : \""+args[0]+"\"," +
							" \"action\" : \""+args[1]+"\"" +
							" }," +
							" \"variables\" : [" +
							" {" +
							" \"slot\": 0," +
							" \"name\": \"category\"," +
							" \"value\": \"news\"" +
							" }," +
							" {" +
							" \"slot\": 0," +
							" \"name\": \"topic\"," +
							" \"value\": \"sport\"" +
							" }" +
							"" +
							" ]" +
							"}" +
							"}";
	        System.out.println("sending GAIN stats");
	        //System.out.println(HttpHelper.sendRequest("POST", "http://wa.vse.cz/listener", body, contentType).toString());
        }
	}
}
