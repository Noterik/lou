/* 
* FSList.java
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

package org.springfield.lou.fs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * FSList
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class FSList {
	private String path;
	private String id;
	private List<FsNode> nodes;
	private Map<String, List<FsNode>> QueryCache = new HashMap<String, List<FsNode>>(); 
	private Map<String, String> properties = new HashMap<String, String>(); // future use to store 'collection' properties from the filesystem
	

	public FSList(String uri) {
		path = uri;
		nodes = new ArrayList<FsNode>();
	}
	
	public FSList() {
		path = "";
	}
	
	public FSList(String uri,List<FsNode> list) {
		path = uri;
		nodes = list;
	}
	
	public void setPath(String p) {
		path = p;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setId(String i) {
		id = i;
	}
	
	public String getId() {
		return id;
	}
	public void deleteAll() {
		nodes = new ArrayList<FsNode>();
	}
	
	public int size() {
		if (nodes!=null) {
			return nodes.size();
		} else {
			return -1;
		}
	}
	
	public List<FsNode> getNodes() {
		return nodes;
	}
	
	public void addNode(FsNode n) {
		nodes.add(n);
		QueryCache = new HashMap<String, List<FsNode>>(); 

	}
	
	public FsNode getNode(String path) {
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			if (n.getPath().equals(path)) {
				System.out.println("FOUND="+n.getPath()+" "+path);
				return n;
			}
		}
		//System.out.println("NOT FOUND="+path);
		return null;
	}
	
	public List<FsNode> getNodesFiltered(String searchkey) {
		List<FsNode> result = QueryCache.get(searchkey);
		if (result!=null) return result;
		
		// result was not in cache !
		result = new ArrayList<FsNode>();
		List<String> searchkeys = smartSplit(searchkey);
		
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			if (matcher(n,searchkeys)) {
				result.add(n);
			}
		}
		QueryCache.put(searchkey, result);
		return result;
	}
	
	private boolean matcher(FsNode n, List<String> searchkeys) {
		String body = n.asIndex();
		for(Iterator<String> iter = searchkeys.iterator() ; iter.hasNext(); ) {
			String key = (String)iter.next();	
			if (body.indexOf(key)==-1) {
				return false;
			}
		}
		return true;
	}
	
	private List<String> smartSplit(String searchkey) {
		String input[] = searchkey.split(" ");
		List<String> output = new ArrayList<String>();
		
		for (int i=0;i<input.length;i++) {
			String key = input[i];
			// do we have a direct search ?
			if (key.indexOf("'")==0) {
				String longkey = key.substring(1);
				int notlast = -1;
				while (notlast==-1) {
					i++;
					longkey +=" "+input[i];
					notlast = input[i].indexOf("'");
				}
				longkey = longkey.substring(0,longkey.length()-1);
				key = longkey;
			}
			output.add(key);
		}
		return output;
	}
	
	public List<FsNode> getNodesFilteredAndSorted(String searchkey,String sortkey,String direction) {
		List<FsNode> cresult = QueryCache.get(searchkey+sortkey+direction);
		if (cresult!=null) return cresult;
		
		ArrayList<FSSortNode> result = new ArrayList<FSSortNode>();
		List<String> searchkeys = smartSplit(searchkey);
		
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
						
			if (matcher(n,searchkeys)) {
				String sv=n.getProperty(sortkey);
				if (sv==null) sv = "";
				result.add(new FSSortNode(n,sv,direction));
			}
		}
		Collections.sort(result);
		
		List<FsNode> endresult = new ArrayList<FsNode>();
		for(Iterator<FSSortNode> iter = result.iterator() ; iter.hasNext(); ) {
			FSSortNode n = (FSSortNode)iter.next();	
			endresult.add(n.node);
		}
		QueryCache.put(searchkey+sortkey+direction, endresult);
		return endresult;
	}
	
	public void clearQueryCache() {
		QueryCache.clear();
	}
	
	public List<FsNode> getNodesSorted(String sortkey,String direction) {
		List<FsNode> cresult = QueryCache.get(sortkey+direction);
		if (cresult!=null) return cresult;
		
		List<FSSortNode> result = new ArrayList<FSSortNode>();
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			String sv=n.getProperty(sortkey);
			if (sv==null) sv = "";
			result.add(new FSSortNode(n,sv,direction));
		}
		Collections.sort(result);
		
		List<FsNode> endresult = new ArrayList<FsNode>();
		for(Iterator<FSSortNode> iter = result.iterator() ; iter.hasNext(); ) {
			FSSortNode n = (FSSortNode)iter.next();	
			endresult.add(n.node);
		}
		QueryCache.put(sortkey+direction, endresult);
		return endresult;
	}

	
	public List<FsNode> getNodesByName(String name) {
		// create a sublist based on input
		List<FsNode> result = new ArrayList<FsNode>();
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			if (n.getName().equals(name)) {
				result.add(n);
			}
		}
		return result;
	}
	
	// give a list of a type but filter on searchkey
	public List<FsNode> getNodesByName(String name,String searchlabel,String searchkey) {
		// create a sublist based on input
		List<FsNode> result = new ArrayList<FsNode>();
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			if (n.getName().equals(name)) {
				String field = n.getProperty(searchlabel);
				if (field.indexOf(searchkey)!=-1) {
					result.add(n);
				}
			}
		}
		return result;
	}
}
