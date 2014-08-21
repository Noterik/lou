/* 
* ExternalSubscriberManager.java
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.BasicComponent;

/**
 * ExternalSubscriberManager
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.external
 *
 */
public class ExternalSubscriberManager {

	private HashMap<String, ExternalSubscriber> subscribtions;
	private Html5Application application;
	private String component;
	
	public ExternalSubscriberManager(Html5Application application, String component){
		this.subscribtions = new HashMap<String, ExternalSubscriber>();
		this.application = application;
		this.component = component;
	}
	
	public void addSubsctiption(HashMap<String, String[]> properties){
		System.out.println("adding external subscriber");
		String url = properties.get("subscribe")[0];
		List<String> events = Arrays.asList(properties.get("events")[0].split(","));
		ExternalSubscriber es = null;
		
		if(properties.containsKey("class")){
			Object o;
			try {
				String classname = "org.springfield.lou.application.components.types.external."+properties.get("class")[0];
				o = Class.forName(classname).getConstructor(Html5Application.class, String.class, String.class, List.class).newInstance(application, url, component, events);
				es = (ExternalSubscriber)o;
				((BasicComponent)this.application.getComponentManager().getComponent(component)).addObserver(es);
			} catch (Exception e) {
				e.printStackTrace();
			} 	
		}
		else{
			es = new ExternalSubscriber(application, url, component, events);
			((BasicComponent)this.application.getComponentManager().getComponent(component)).addObserver(es);				
		}
		
		if(this.subscribtions.containsKey(url))
			((BasicComponent)this.application.getComponentManager().getComponent(this.subscribtions.get(url).getSubscribedComponentName())).deleteObserver(this.getExternalSubscriber(url));
		
		this.subscribtions.put(url, es);
	}
	
	public void addSubsctiption(String url, List<String> events){
		System.out.println("adding external subscriber" + url);
		if(this.subscribtions.containsKey(url))
			((BasicComponent)this.application.getComponentManager().getComponent(this.subscribtions.get(url).getSubscribedComponentName())).deleteObserver(this.getExternalSubscriber(url));
		
		this.subscribtions.put(url, new ExternalSubscriber(application, url, component, events));
	}
	
	public void addSubscription(ExternalSubscriber subscriber){
		this.subscribtions.put(subscriber.getUrl(), subscriber);
		((BasicComponent)this.application.getComponentManager().getComponent(component)).addObserver(subscriber);
	}
	
	public ExternalSubscriber getExternalSubscriber(String name){
		return this.subscribtions.get(name);
	}
	
	public void remove(String name){
		this.subscribtions.remove(name);
	}
		
	public HashMap<String, ExternalSubscriber> getMap(){
		return this.subscribtions;
	}
}
