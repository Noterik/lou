/* 
* RemoteProxy.java
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

/**
 * RemoteProxy
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package com.noterik.springfield.lou.application.components.types.proxy
 *
 */
public class RemoteProxy extends ProxyComponent{

	private String target;
	
	/**
	 * Constructor for GenericProxy class. Always use init() after construction.
	 * @param proxyname the id of the proxycomponent. You can retrieve 
	 * this component with this id with the method componentManager.getComponent(proxyname)
	 * @param target the name of the component to forward the messages to, as well as observe 
	 * this component.
	 */
	public RemoteProxy(String proxyname, String target)  {
		super(proxyname);	
		this.target = target;
		//System.out.println("PROXYTARGET="+target);
	}
	
	public void init(){
		super.init();
		this.getApplication().getComponentManager().getComponent("signal").put("", "subscribe("+this.id+","+this.target+")");
		this.getApplication().getComponentManager().getComponent("signal").put("", "subscribe("+this.target+","+this.id+")");
	}
	
	public void put(String from,String msg) {
		this.getApplication().getComponentManager().getComponent(this.target).put("external", msg);
		this.change(this.id + ":" + from+","+msg);
	}
}