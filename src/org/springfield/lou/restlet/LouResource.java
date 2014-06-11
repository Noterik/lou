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
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * LouResource
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.restlet
 *
 */
public class LouResource extends Resource {
	/**
	 * Request uri
	 */
	protected String uri; 
	
	/**
	 * Sole constructor
	 * 
	 * @param context
	 * @param request
	 * @param response
	 */
	public LouResource(Context context, Request request, Response response) {
        super(context, request, response);
        
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
	@Override
    public Representation getRepresentation(Variant variant) {
		String responseBody = "GET: " + uri;
		Representation entity = new StringRepresentation(responseBody);
        return entity;
	}
	
	/**
	 * PUT
	 */
	public void put(Representation representation) {
		String responseBody = "PUT: " + uri;
		Representation entity = new StringRepresentation(responseBody);
		getResponse().setEntity(entity);
	}
	
	/**
	 * POST
	 */
	public void post(Representation representation) {
		String responseBody = "POST: " + uri;
		Representation entity = new StringRepresentation(responseBody);
		getResponse().setEntity(entity);
	}
	
	/**
	 * DELETE
	 */
	public void delete() {
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