/* 
* LouServer.java
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

package org.springfield.lou;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springfield.lou.application.ApplicationManager;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.homer.LouProperties;

/**
 * Main lou class
 *
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2013 or 2014
 * @package org.springfield.lou
 *
 */
public class LouServer {
	/** The LouServer's log4j Logger */
	private static Logger LOG = Logger.getLogger(LouServer.class);

	/** service type of this service */
	private static final String SERVICE_TYPE = "louservice";

	/** instance */
	private static LouServer instance = new LouServer();
	
	/** configuration properties */
	private Properties configuration;

	private Boolean running =  false;
	
	/**
	 * Sole constructor
	 */
	public LouServer() {
		instance = this;
	}
	
	/**
	 * Return LouConfiguration instance
	 * 
	 * @return LouConfiguration instance.
	 */
	public static LouServer instance() {
		return instance;
	}
	
	/**
	 * Check if Lou is running
	 * 
	 * @return boolean true if running, otherwise false
	 */
	public Boolean isRunning() {
		if (running) {
			return true;
		}
		return false;
	}
		
	/**
	 * Returns the configuration.
	 * 
	 * @return The configuration.
	 */
	public Properties getConfiguration() {
		return configuration;
	}
	
	/**
	 * Initializes the configuration
	 */
	public void init() {
        
		// init properties xml
		initConfigurationXML();		
		ApplicationManager.instance(); // trigger the start of the manager
		ServiceHandler.instance();
		running = true;
	}
	
	/**
	 * Loads configuration file.
	 */
	private void initConfigurationXML() {
		System.out.println("Initializing configuration file.");
		
		// configuration file
		configuration = new Properties();
		
		LouProperties mp = LazyHomer.getMyLouProperties();
		if (mp!=null) {
			configuration.put("default-log-level", mp.getDefaultLogLevel());
		} else {
			System.out.println("Loading from configuration failed.");
		}
	}
		
	/**
	 * Returns a list of domains that this Lou is serving
	 * 
	 * @return
	 */
	public List<String> getOwnDomains() {
			List<String> domains = new ArrayList<String>();
			//TODO: load this from config
			domains.add("webtv");
			return domains;
	}    

    /**
     * Shutdown
     */
	public void destroy() {
		instance = null;
		running = false;
	}
}
