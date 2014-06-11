/* 
* LouApplication.java
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

package org.springfield.lou.restlet;

import javax.servlet.ServletContext;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.springfield.lou.homer.LazyHomer;

import com.noelios.restlet.ext.servlet.ServletContextAdapter;

/**
 * LouApplication
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.restlet
 *
 */
public class LouApplication extends Application {	
	private static LazyHomer lh = null; 
	
    public LouApplication() {
    	super();
    	System.out.println("Lou starts");
    }
    
    public LouApplication(Context parentContext) {
    	super(parentContext);
    }
    
    public void start(){
		try{
			super.start();
		}catch(Exception e){
			System.out.println("Error starting application");
			e.printStackTrace();
		}
	} 
    
    /**
	 * Called on shutdown
	 */
	public void stop() throws Exception {
		try {
			super.stop();
		} catch (Exception e) {
			System.out.println("lou: error stopping application");
			e.printStackTrace();
		}		
		// destroy global config
		//LouServer.instance().destroy();
		lh.destroy();
	}

    @Override
    public Restlet createRoot() {
    	// set rootpath and return restlet
    	ServletContextAdapter adapter = (ServletContextAdapter) getContext();
		ServletContext servletContext = adapter.getServletContext();
		
		LazyHomer lh = new LazyHomer();
		lh.init(servletContext.getRealPath("/"));		
		
		// disable logging
		Component component = (Component)servletContext.getAttribute("com.noelios.restlet.ext.servlet.ServerServlet.component");
		component.getLogService().setEnabled(false);
		
		return new LouRestlet(super.getContext());
    }
}
