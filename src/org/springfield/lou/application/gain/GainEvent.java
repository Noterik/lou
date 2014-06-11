/* 
* GainEvent.java
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

package org.springfield.lou.application.gain;

/**
 * GainEvent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.types.secondscreen
 *
 */
public class GainEvent {
	private String _applicationid;
	private String _object;
	private String _userid;
	private String _type;
	private String _action;
	private String _result;
	
	public void setType(String applicationid) {
		_type = applicationid;
	}
	
	public String getType() {
		return _type;
	}
	
	public void setAction(String action) {
		_action = action;
	}
	
	public String getAction() {
		return _action;
	}
	
	public void setObject(String object) {
		_object = object;
	}
	
	public String getObject() {
		return _object;
	}
		
	
	public void setApplicationId(String applicationid) {
		_applicationid = applicationid;
	}
	
	public String getApplicationId() {
		return _applicationid;
	}
	
	public void setUserId(String userid) {
		_userid = userid;
	}
	
	public String getUserId() {
		return _userid;
	}
	
	public void setResult(String result) {
		_result = result;
	}
	
	public String getResult() {
		return _result;
	}
}
