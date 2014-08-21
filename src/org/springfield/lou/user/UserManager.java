/* 
* UserManager.java
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

package org.springfield.lou.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * UserManager
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.user
 */
public class UserManager {
		
	Map<String, User> users;	

	/**
	 * Constructor for the UserManager class
	 */
	public UserManager (){
		users = new HashMap<String, User>();
	}
	
	public void addUser(User user){
		users.put(user.getId(), user);		
	}
	
	public void removeUser(User user){
		users.remove(user.getId());		
	}
	
	/**
	 * checks if the user manager has a user with the specified id
	 * @param id
	 * @return true if a user with this id exists, else false
	 */
	public boolean hasUser(String id){
		return users.containsKey(id);
	}
	
	/**
	 * 
	 * @param id
	 * @return an UnknownUser object of the user with the specified id
	 */
	public User getUser(String id){
		return users.get(id);
	}
	
    public int size(){
    	return users.size();
    }
    
    public Iterator<String> getUsers() {
    	return users.keySet().iterator();
    }
}
