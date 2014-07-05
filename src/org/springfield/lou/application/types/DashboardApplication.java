/* 
* DashboardApplication.java
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

package org.springfield.lou.application.types;

import org.springfield.lou.application.*;
import org.springfield.lou.application.components.types.AvailableappsComponent;
import org.springfield.lou.screen.*;

/**
 * DashboardApplication
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.types
 *
 */
public class DashboardApplication extends Html5Application {	

	public DashboardApplication(String id){
		super(id); 
	}
	
	public void onNewScreen(Screen s) {
		loadStyleSheet(s,"dashboardapp");
		s.setRole("dashboard");
		loadContent(s, "feedbackbar");
		loadContent(s, "login");
		loadContent(s, "notification");
	}
	
	public void onNewUser(Screen s,String name) {
		System.out.println("onNewUser2="+name);
		super.onNewUser(s, name);
		System.out.println("onNewUser3="+name);
		if (name.equals("admin")) {
			System.out.println("onNewUser4="+name);
			loadContent(s, "debug","debugmode");		
			loadContent(s, "logger","logger");
			loadContent(s, "opentools");
        	s.putMsg("feedbackbar","app","html(Welcome "+name+" please use with care)");
    		loadContent(s, "appdetails");
		}
	}
	
	public void newApplicationFound(String name) {
		//System.out.println("NEW APP="+name);
		AvailableappsComponent comp = (AvailableappsComponent)getComponentManager().getComponent("availableapps");
		if (comp!=null) {
			comp.update();
			comp.updateDetails(name);
		}
	}
}
