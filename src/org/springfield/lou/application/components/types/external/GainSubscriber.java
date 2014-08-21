/* 
* GainSubscriber.java
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

package org.springfield.lou.application.components.types.external;

import java.util.List;
import java.util.Observable;

import org.springfield.lou.application.Html5Application;

/**
 * GainSubscriber
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.external
 *
 */
public class GainSubscriber extends ExternalSubscriber {
	
	public GainSubscriber(Html5Application application, String url, String component, List<String> actions){
		super(application, url, component, actions);
	}
	
	@Override
	public void update(Observable o, Object arg) {
    	String[] ar1 = ((String)arg).split(":");
		String[] ar2 = ((String)ar1[1]).split(",");
        String comp = ar2[0];
        String act = ar2[1];
        String from = ar1[0];
    	
        String contentType = "application/json";
        String user = null;
        
        try{
        	user = getApplication().getScreenManager().get(from).getUserName();
        }catch(NullPointerException e){}
        if(user==null) user = "guest";
//        System.out.println(user);
//        System.out.println(getApplication().getId());
        
        //TODO: add the restlet URI for the component as the ID
        String body = "{" +
						"\"accountId\" : \"UA-000000-0\"," + 
			        	"\"client\" : {" +
						" \"type\" : \"Html5Application\"," +
						" \"version\" : \"0.1\"" +
						"}," +
						"\"object\" : {" +
						" \"id\" : \""+getApplication().getFullId()+"\"," +
						" \"title\" : \"News 1\"," +
						" \"uri\": \"/news1?a=20&b=30\"" +
						"}," +
						"\"user\" : {" +
						" \"id\" : \"userid\"" +
						"}," +
						"\"interaction\" : {" +
						" \"type\" : \"event\"" +
						"}," +
						"\"attributes\" : {" +
						" \"event\" : {" +
						" \"category\" : \""+comp+"\"," +
						" \"action\" : \""+act+"\"" +
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
//        System.out.println("sending GAIN stats" + body);
        //System.out.println(HttpHelper.sendRequest("POST", "http://wa.vse.cz/listener", body, contentType));
    }
}
