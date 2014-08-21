/* 
* ExternalSubscriber.java
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
import java.util.Observer;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.BasicComponent;
import org.springfield.lou.application.components.ComponentInterface;
import org.springfield.mojo.http.HttpHelper;

/**
 * ExternalSubscriber
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.external
 *
 */
public class ExternalSubscriber implements Observer {
	
	private String url;
	private ComponentInterface component;
	private List<String> actions;
	private Html5Application app;
	
	public ExternalSubscriber(Html5Application application, String url, String component, List<String> actions){
		System.out.println("Creating External Subscriber!");
		this.app = application;
		this.url = url;
		if(application==null)System.out.println("application is null");
		this.component = (BasicComponent)application.getComponentManager().getComponent(component);
		this.actions = actions;
	}

	@Override
	public void update(Observable o, Object arg) {
		String[] ar1 = ((String)arg).split(":");
		String[] ar2 = ((String)ar1[1]).split(",");
        String comp = ar2[0];
        String act = ar2[1];
        
        String xml = "<fsxml>" +
			        		"<message>" +
				        		"<properties>" +
				        			"<component>"+comp+"</component>" +
				        			"<action>"+act+"</action>" +
				        		"</properties>" +
			        		"</message>" +
		        		"</fsxml>";
        
        if(this.actions.contains(act)) HttpHelper.sendRequest("POST", this.url, xml, "text/xml");
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public List<String> getEvents(){
		return this.actions;
	}
	
	public String getSubscribedComponentName(){
		return this.component.getId();
	}
	
	public String getApplicationName(){
		return this.app.getAppname();
	}
	
	public Html5Application getApplication(){
		return this.app;
	}	
}
