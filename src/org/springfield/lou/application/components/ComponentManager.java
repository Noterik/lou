/* 
* ComponentManager.java
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

package org.springfield.lou.application.components;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springfield.lou.application.ApplicationManager;

/**
 * ComponentManager
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components
 *
 */
public class ComponentManager {
	private Map<String, ComponentInterface> runningcomponents = new HashMap<String, ComponentInterface>();
	
    public ComponentManager() {
    	//System.out.println("Component Manager started");
    }
    
    public void addComponent(ComponentInterface comp) {
    	this.runningcomponents.put(comp.getId(),comp);
    	//System.out.println("ADD COMP="+comp.getId());
    	ApplicationManager.update();
    }
    
    public void removeComponent(String id) {
    	// possible bug daniel
//    	try{
//    		this.runningcomponents.remove(id);
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
    	ApplicationManager.update();
    }
    
    public ComponentInterface getComponent(String name) {
    	return this.runningcomponents.get(name);
    }
    
    public Map<String, ComponentInterface> getComponents(){
    	return this.runningcomponents;
    }
    
    public String getComponentPath(String name) {
    	String path = name+File.separator+name+".html";
    	return path;
    }
    
    public String getComponentJS(String name) {
    	String path = name+File.separator+name+".js";
    	return path;
    }
    
    public String getComponentGestures(String name) {
    	String path = name+File.separator+name+"_gestures.js";
    	return path;
    }
    
    public int size() {
    	return runningcomponents.size();
    }
}
