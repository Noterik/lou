/* 
* GainSender.java
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

package org.springfield.lou.application.gain;

import java.util.*;

import org.springfield.lou.application.*;
import org.springfield.mojo.http.HttpHelper;
import org.springfield.mojo.http.Response;

/**
 * GainSender
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.types.secondscreen
 *
 */
public class GainSender {
	
	private Html5Application _app;
	private List<GainEvent> sendevents = new ArrayList<GainEvent>();
	
	public GainSender(Html5Application app) {
		_app = app;
	}
		
	public String send(GainEvent ge) {
		//TODO: add the restlet URI for the component as the ID
	    String body = "{" +
	    		"\"accountId\" : \"LINKEDTV-TEST\"," + 
				"\"type\" : \"event\"," +
				"\"userId\" : \""+ge.getUserId()+"\"," +
				"\"objectId\" : \""+ge.getObject()+"\"," +
				"\"attributes\" : {" +
				" \"category\" : \""+ge.getType()+"\"," +
				" \"action\" : \""+ge.getAction()+"\"," +
				" \"location\" : \"32\", "+
				" \"client\" : {" +
				"  \"type\" : \"lou/remote\"," +
				"  \"version\" : \"0.0.1\"" +
				"  }" +
				" }" +
				"}";
				        	
				        	/*
				        	"\"client\" : {" +
							" \"type\" : \""+ge.getApplicationId()+"\"," +
							" \"version\" : \"Springfield Multiscreen Toolkit build 26 Jul 2013\"" +
							"}," +
							"\"object\" : {" +
							" \"id\" : \""+ge.getObject()+"\"," +
							" \"title\" : \""+ge.getObject()+"\"," +
							" \"uri\": \""+ge.getObject()+"\"" +
							"}," +
							"\"user\" : {" +
							" \"id\" : \""+ge.getUserId()+"\"" +
							"}," +
							"\"interaction\" : {" +
							" \"type\" : \"event\"" +
							"}," +
							"\"attributes\" : {" +
							" \"event\" : {" +
							" \"category\" : \""+ge.getType()+"\"," +
							" \"action\" : \""+ge.getAction()+"\"" +
							" }," +
							" \"variables\" : [" +
							" {" +
							" \"slot\": 0," +
							" \"name\": \"category\"," +
							" \"value\": \"times\"" +
							" }," +
							" {" +
							" \"slot\": 0," +
							" \"name\": \"playtime\"," +
							" \"value\": \"00:00:21\"" +
							" }" +
							"" +
							" ]" +
							"}" +
							"}";*/
	    try {
	    	System.out.println("sending GAIN stats");
	    	Response response = HttpHelper.sendRequest("POST", "http://dev.wa.vse.cz/gain/listener", body, "application/json");
	        	
	    	// add one to the list	        	
	        ge.setResult(Integer.toString(response.getStatusCode()));
	        sendevents.add(ge);
	        
	        return Integer.toString(response.getStatusCode());
	    } catch(Exception e) {
	      	System.out.println("GOT A 500 error");
	       	e.printStackTrace();
	       	return ("GOT A 500 error");
	    }
	}
	
	public List<GainEvent> getSendEvents() {
		return sendevents;
	}
}
