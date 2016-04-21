/* 
* LoggingResource.java
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.springfield.lou.homer.LazyHomer;

/**
 * The logging resource is used to configure the debugging behavior. 
 * The logging can be configured hierarchically. Logging inherited level
 * for a given logger is equal to that of its parent. So to configure
 * a single class use the full name, com.fu.Bar, and to configure a
 * package and every ancestor use the package name, com.fu
 * 
 * Resource uri:
 * 		/logging
 * 
 * Agruments:
 *  	level				The logging level you want to set (ALL,INFO,WARN,
 *  						DEBUG,TRACE,ERROR,FATAL,OFF) 
 *  	name (optional)		The full package, or classname you want to configure.
 *
 * @author Derk Crezee
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.lou.restlet
 *
 */
public class LoggingResource extends ServerResource {

	public LoggingResource() {
		//constructor
	}

	/**
	 * DoInit
	 * 
	 * @param context
	 * @param request
	 * @param response
	 */
	public void doInit(Context context, Request request, Response response) {
		super.init(context, request, response);
		
		// add representational variants allowed
        getVariants().add(new Variant(MediaType.TEXT_XML));
	}

	// allowed actions: GET 
	public boolean allowPut() {return false;}
	public boolean allowPost() {return false;}
	public boolean allowGet() {return true;}
	public boolean allowDelete() {return false;}
	
	/**
	 * GET
	 */
	@Get
	public void handleGet() {
		String responseBody = "";
		
		// get parameters
		Form qForm = getRequest().getResourceRef().getQueryAsForm();
		String name = qForm.getFirstValue("name",LazyHomer.PACKAGE_ROOT); // default root package
		String level = qForm.getFirstValue("level",null);
		
		// check level parameter
		if(level!=null) {
			// determine level
			Level logLevel = null;
			if(level.toLowerCase().equals("all")) {
				logLevel = Level.ALL;
			} else if(level.toLowerCase().equals("info")) {
				logLevel = Level.INFO;
			} else if(level.toLowerCase().equals("warn")) {
				logLevel = Level.WARN;
			} else if(level.toLowerCase().equals("debug")) {
				logLevel = Level.DEBUG;
			} else if(level.toLowerCase().equals("trace")) {
				logLevel = Level.TRACE;
			} else if(level.toLowerCase().equals("error")) {
				logLevel = Level.ERROR;
			} else if(level.toLowerCase().equals("fatal")) {
				logLevel = Level.FATAL;
			} else if(level.toLowerCase().equals("off")) {
				logLevel = Level.OFF;
			}
			
			// check level
			if(logLevel==null) {
				responseBody = "please provide log level: {ALL,INFO,WARN,DEBUG,TRACE,ERROR,FATAL,OFF}";
			} else {
				// set level
				Logger.getLogger(name).setLevel(logLevel);
				
				responseBody = "logging for " + name + " was set to " + logLevel.toString();
			}
		} else {
			// error message
			responseBody = "please provide log level: {ALL,INFO,WARN,DEBUG,TRACE,ERROR,FATAL,OFF}";
		}
		
		// return
		Representation entity = new StringRepresentation(responseBody);
		getResponse().setEntity(entity);
	}
}
