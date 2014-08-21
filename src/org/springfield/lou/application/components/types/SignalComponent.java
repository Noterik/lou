/* 
* SignalComponent.java
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
 * SignalComponent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types
 *
 */
public class SignalComponent extends BasicComponent {
	
	public void put(String from,String msg) {
		
        int pos = msg.indexOf("(");      
        if (pos!=-1) {
	        String command = msg.substring(0,pos);
	        String content = msg.substring(pos+1,msg.length()-1);
	        if (command.equals("notify")) {
	            String[] ar = content.split(",");
	            BasicComponent observable = (BasicComponent)this.getApplication().getComponentManager().getComponent(ar[0]);
	            if(observable!=null){
	            	observable.change(from+":"+content);
	            }
	            else System.out.println("observable component is null");
	        }
	        else if(command.equals("subscribe")){
	        	String[] ar = content.split(",");
	        	System.out.println("component " + ar[0] + "subscribed to " + ar[1]);
	        	BasicComponent observer = (BasicComponent)this.getApplication().getComponentManager().getComponent(ar[0]);
	        	BasicComponent observable = (BasicComponent)this.getApplication().getComponentManager().getComponent(ar[1]);
	        	if(observer!=null && observable !=null)observable.addObserver(observer);
	        }   
        }
	}
}
