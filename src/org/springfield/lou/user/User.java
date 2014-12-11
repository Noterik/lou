/* 
* User.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springfield.lou.screen.Screen;

/**
 * User
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.user
 */
public class User {
	
	Map<String, Screen> screens;
	private String id;
	List<String> bookmarks = new ArrayList<String>();
	List<String> shared = new ArrayList<String>();	
	
	/**
	 * Constructor for the class UnkownUser
	 * @param id the desired id for this user
	 */
	public User(String id){
		this.id = id;
		screens = new HashMap<String, Screen>();
	}
	
	/**
	 * 
	 * @return the id of this user
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * if you don't understand from the name, i can't help you
	 */
	public void destroy(){
		id = null;
		screens = null;
	}
	
	/**
	 * 
	 * @return a Map<String, Screen> object containing the screens of this user
	 */
	public Map<String, Screen> getScreens(){
		return screens;
	}
	
	/**
	 * adds a screen for this user
	 * @param id the desired id for this screen
	 * @param caps the capabilities object to be associated with this screen
	 */
	public void addScreen(Screen s){
		System.out.println("USER ADDSCREEN");
		screens.put(s.getId(),  s);
	}
	
	public void removeScreen(String sid){
		screens.remove(sid);
	}
	
	/**
	 * 
	 * @param id
	 * @return the screen with the specified id of this user
	 */
	public Screen getScreen(String id){
		return screens.get(id);
	}
	
	public List<String> getBookmarks() {
		return bookmarks;
	}
	
	public List<String> getShared() {
		return shared;
	}
	
	public void addBookmark(String bookmark) {
		bookmarks.add(bookmark);
	}
	
	public void addShared(String share) {
		shared.add(share);
	}	
}
