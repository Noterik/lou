/* 
* FSSets.java
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * FSSets
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class FSSets {
	
	private Map<String, List<FsNode>> sets =  new HashMap<String, List<FsNode>>();;
	//private List<String> values = new ArrayList<String>();
	
	
	public FSSets(List<FsNode> nodes,String field) {
		// divide the nodes based on the field
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			String f=n.getProperty(field);
			if (f!=null) {
				// set the correct set
				List<FsNode> set = sets.get(f);
				if (set==null) {
					set = new ArrayList<FsNode>();
					sets.put(f, set);
				}
				set.add(n);
			}
		}	
	}
	
	public FSSets(List<FsNode> nodes) {
		// divide the nodes based on the field
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			String f=n.getName();
			if (f!=null) {
				// set the correct set
				List<FsNode> set = sets.get(f);
				if (set==null) {
					set = new ArrayList<FsNode>();
					sets.put(f, set);
				}
				set.add(n);
			}
		}	
	}
	
	public Iterator<String> getKeys() {
		return sets.keySet().iterator();
	}
	
	public int getSetSize(String name) {
		List<FsNode> set = sets.get(name);
		if (set!=null) {
			return set.size();
		} else {
			return 0;
		}
	}
}
