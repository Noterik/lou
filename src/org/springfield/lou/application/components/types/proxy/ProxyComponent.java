/* 
* ProxyComponent.java
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

package org.springfield.lou.application.components.types.proxy;

import java.util.HashMap;
import java.util.Observable;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.BasicComponent;
import org.springfield.lou.application.components.types.external.ExternalSubscriberManager;

/**
 * ProxyComponent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types.proxy
 *
 */
public class ProxyComponent extends BasicComponent {
	
	ExternalSubscriberManager subscribtions;
	
	public ProxyComponent(String id){
		super();
		this.id = id;
	}
	
	public void init(){
		this.subscribtions = new ExternalSubscriberManager((Html5Application) this.getApplication(), this.id);
	}
	
	public void put(String from, HashMap<String, String[]> properties){
		if(properties.containsKey("subscribe")) this.subscribtions.addSubsctiption(properties);
		else if(properties.containsKey("msg")){
			for(int i=0;i<properties.get("msg").length;i++) this.put(from, properties.get("msg")[i]);
		}
	}
	
	public void put(String from,String msg) {

	}
	
	public ExternalSubscriberManager getSubscribtionManager(){
		return this.subscribtions;
	}
	
	public void update(Observable o, Object arg) {
		//System.out.println("PROXY:: notifying external observers!");
		this.setChanged();
		this.notifyObservers(arg);	
	}	
}
