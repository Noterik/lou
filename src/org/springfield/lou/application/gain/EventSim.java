/* 
* EventSim.java
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

import java.util.Iterator;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

/**
 * EventSim
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.types.secondscreen
 *
 */
public class EventSim {
	
	private String _type = "user";
	private String _name = "select";
	private Html5Application _app;
	private GainSender _gainsender;
	
	public EventSim(Html5Application app,GainSender gainsender) {
		_app = app;
		_gainsender = gainsender;
	}
	
	public void putOnScreen(Screen s,String command,String content) {
		// we should check command
		command = command.substring(9);
		if (command.equals("typechange")) {
			changeType(s,content);
		} else if (command.equals("namechange")) {
			changeName(s,content);
		} else if (command.equals("sendevent")) {
			sendEvent(s);
		}
	}
	
	public void changeType(Screen s,String type) {
		// so we need to load new data to the divs
		_type = type;
		s.setContent("eventsimtype",setTypeBody(type));
		s.setContent("eventsimoptions",setOptionsBody(type));
	}
	
	public void changeName(Screen s,String name) {
		_name = name;
	}
	
	public void sendEvent(Screen s) {
		// send a event
		GainEvent ge = new GainEvent();
		ge.setApplicationId("/domain/linkedtv/user/daniel/html5application/secondscreen");
		ge.setObject("/domain/linkedtv/video/rbb/10");
		ge.setUserId("/domain/linkedtv/user/rita");
		ge.setType(_type);
		ge.setAction(_name);

		_gainsender.send(ge);
		
		// lets update the list
		String body = "<table>";
		body += "<tr><th>app</th><th>user</th><th>object</th><th>type</th><th>action</th><th>result</th></tr>";
		for(Iterator<GainEvent> iter = _gainsender.getSendEvents().iterator(); iter.hasNext(); ) {
			GainEvent ev = (GainEvent)iter.next();
			body+="<tr><td>"+ev.getApplicationId()+"</td>";
			body+="<td>"+ev.getUserId()+"</td>";
			body+="<td>"+ev.getObject()+"</td>";
			body+="<td>"+ev.getType()+"</td>";
			body+="<td>"+ev.getAction()+"</td>";
			int pos = ev.getResult().indexOf("_id");
			if (pos!=-1) {
				body+="<td>"+ev.getResult().substring(pos)+"</td></tr>";
			} else {
					body+="<td>"+ev.getResult()+"</td></tr>";
			}
		}
		body += "</table>";
		s.setContent("eventsimlogs",body);
	}
	
	private String setTypeBody(String type) {
		String body = "Event Type : ";
		body += "<select id=\"eventsim_typevalue\" onchange=\"return components.eventsim.typechange(event)\">";
		body += "<option value=\""+type+"\">"+type+"</option>";
		body += "<option value=\"player\">player</option>";
		body += "<option value=\"user\">user</option>";
		body += "<option value=\"player\">player</option>";
		body += "<option value=\"screen\">screen</option>";
		body += "<option value=\"application\">application</option>";
		body += "</select>";
		return body;
	}
	
	private String setOptionsBody(String type) {
		String body = "Name : ";
		if (type.equals("player")) {
			body += "<select id=\"eventsim_namevalue\" onchange=\"return components.eventsim.namechange(event)\">";
			body += "<option value=\"play\">play</option>";
			body += "<option value=\"pause\">pause</option>";
			body += "<option value=\"stop\">stop</option>";
			body += "</select>";
		} else if (type.equals("user")) {
			body += "<select id=\"eventsim_namevalue\" onchange=\"return components.eventsim.namechange(event)\">";
			body += "<option value=\"select\">select</option>";
			body += "<option value=\"viewtime\">viewtime</option>";
			body += "<option value=\"bookmarked\">bookmarked</option>";
			body += "</select>";
		}
		body += "<input id=\"eventsim_sendevent\" onclick=\"return components.eventsim.sendevent()\" type=\"submit\" name=\"send event\">";
		return body;
	}
}
