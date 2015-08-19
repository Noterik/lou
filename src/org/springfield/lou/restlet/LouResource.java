/* 
* LouResource.java
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

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;

/**
 * LouResource
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.restlet
 *
 */
public class LouResource extends ServerResource {
	/**
	 * Request uri
	 */
	protected String uri; 
	
	public LouResource() {
		//constructor
	}	
	
	public void doInit(Context context, Request request, Response response) {
        super.init(context, request, response);
        
        // add representational variants allowed
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        // get request uri
        uri = getRequestUri();
	}
	
	// allowed actions: POST, PUT, GET, DELETE 
	public boolean allowPut() {return true;}
	public boolean allowPost() {return true;}
	public boolean allowGet() {return true;}
	public boolean allowDelete() {return true;}
	
	/**
	 * GET
	 */
	@Get
	public void handleGet() {
		String responseBody = "GET: " + uri;
		Representation entity = new StringRepresentation(responseBody);
		getResponse().setEntity(entity);
	}
	
	/**
	 * PUT
	 */
	@Put
	public void handlePut() {
		String responseBody = "PUT: " + uri;
		Representation entity = new StringRepresentation(responseBody);
		getResponse().setEntity(entity);
	}
	
	/**
	 * POST
	 */
	@Post
	public void handlePost() {
		String responseBody = "POST: " + uri;
		Representation entity = new StringRepresentation(responseBody);
		getResponse().setEntity(entity);
	}
	
	/**
	 * DELETE
	 */
	@Delete
	public void handleDelete() {
		String responseBody = "DELETE: " + uri;
		Representation entity = new StringRepresentation(responseBody);
		getResponse().setEntity(entity);
	}
	
	/**
	 * Get request uri
	 * @return
	 */
	private String getRequestUri() {
		 // get uri
        String reqUri = getRequest().getResourceRef().getPath();
        reqUri = reqUri.substring(reqUri.indexOf("/",1));
        if(reqUri.endsWith("/")) {
        	reqUri = reqUri.substring(0,reqUri.length()-1);
        }
        return reqUri;
	}
}