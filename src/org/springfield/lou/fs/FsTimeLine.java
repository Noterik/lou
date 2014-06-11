/* 
* FsTimeLine.java
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

import java.util.*;

import org.springfield.lou.homer.LazyMarge;
import org.springfield.lou.homer.MargeObserver;

/**
 * FsTimeLine
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class FsTimeLine implements MargeObserver {
	
	private ArrayList<String> observing = new ArrayList<String>();
	private Map<String, ArrayList<FsNode>> types = new HashMap<String, ArrayList<FsNode>>();
	private ArrayList<FsTimeLineObserver> observers = new ArrayList<FsTimeLineObserver>();

	public void addNodes(ArrayList<FsNode> nodes) {
		String path = "";
		for (int i=0;i<nodes.size();i++) {
			FsNode node = nodes.get(i);
			if(path.equals("")){
				String[] fullPath = node.getPath().split("/");
				for(int c = 0; c < fullPath.length - 1; c++){
					if(!fullPath[c].equals("")){
						path += "/" + fullPath[c];
					}
				}
			}
			ArrayList<FsNode> list = types.get(node.getName());
			if (list==null) {
				 list = new ArrayList<FsNode>();				
				 types.put(node.getName(), list);
			}
			list.add(node);
		}

		// now we need to sort all the lists !
		Iterator<String> it = types.keySet().iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			ArrayList<FsNode> list = types.get(k);
			Collections.sort(list);
		}
		
		System.out.println("-----------------------------");
		System.out.println("LISTEN TO: " + path);
		System.out.println("-----------------------------");
		if(!observing.contains(path + "/")){
			LazyMarge.addObserver(path + "/*", this);
		}
	}
	
	public Iterator<FsNode> getFsNodesByType(String type) {
		ArrayList<FsNode> list = types.get(type);
		
		if (list!=null) {
			return list.iterator();
		} else {
			return null;
		}
	}
	
	public void addObserver(FsTimeLineObserver observer){
		this.observers.add(observer);
	}
	
	public FsNode getCurrentFsNode(String type,long time) {
		FsNode match = null;
		ArrayList<FsNode> list = types.get(type);
		if (list!=null) {
			// should be better grrr
			for (int i=0;i<list.size();i++) {
				FsNode node = list.get(i);
				if (time>=node.getStarttime() && time<=(node.getDuration()+node.getStarttime())) {
					match = node;
				}
			}
		}
		return match;
	}
	
	public int getCurrentFsNodeNumber(String type,long time) {
		ArrayList<FsNode> list = types.get(type);
		int match = -1;
		if (list!=null) {
			// should be better grrr
			for (int i=0;i<list.size();i++) {
				FsNode node = list.get(i);
				if (time>=node.getStarttime() && time<=(node.getDuration()+node.getStarttime())) {
					match = i;
				}
			}
		}
		return match;
	}
	
	public FsNode getFsNodeById(String type, int number) {
		//frontend starts with 1 instead of 0 as index, correct for this
		number--;
		FsNode node = null;
		ArrayList<FsNode> list = types.get(type);
		if (list!=null) {
			if (number < list.size()) {		
				node = list.get(number);
			}
		}
		return node;
	}
	
	public void removeNode(String type, String id){
		ArrayList<FsNode> list = types.get(type);
		FsNode nodeToDelete = null;
		for(Iterator<FsNode> i = list.iterator(); i.hasNext();){
			FsNode node = i.next();
			if(node.getId().equals(id)){
				nodeToDelete = node;
			}
		}
		list.remove(nodeToDelete);
		Fs.deleteNode(nodeToDelete.getPath());
	}
	
	public FsNode getNode(String type, String id){
		ArrayList<FsNode> list = types.get(type);
		for(Iterator<FsNode> i = list.iterator(); i.hasNext();){
			FsNode node = i.next();
			if(node.getId().equals(id)){
				return node;
			}
		}
		return null;
	}
	
	public void removeNodes() {
		types = new HashMap<String, ArrayList<FsNode>>();
	}
	
	private void updateObservers(String method, String type, String id){
		for(int i = 0; i < observers.size(); i++){
			FsTimeLineObserver observer = observers.get(i);
			observer.timeLineUpdated(method, type, id);
		}
	}

	@Override
	public void remoteSignal(String from, String method, String url) {
		// TODO Auto-generated method stub
		String[] urlSplits = url.split(",");
		String sourceUrl = urlSplits[0];
		String[] sourceUrlSplits = sourceUrl.split("/");
		String type = sourceUrlSplits[sourceUrlSplits.length - 2];
		String id = sourceUrlSplits[sourceUrlSplits.length - 1];
		
		if(method.equals("DELETE")){
			ArrayList<FsNode> list = types.get(type);
			FsNode nodeToDelete = null;
			for(Iterator<FsNode> i = list.iterator(); i.hasNext();){
				FsNode node = i.next();
				if(node.getId().equals(id)){
					nodeToDelete = node;
				}
			}
			list.remove(nodeToDelete);
			updateObservers("DELETE", type, id);
		}else if(method.equals("PUT")){
			FsNode newNode = Fs.getNode(sourceUrl);
			ArrayList<FsNode> list = types.get(type);
			FsNode nodeToDelete = null;
			for(Iterator<FsNode> i = list.iterator(); i.hasNext();){
				FsNode node = i.next();
				if(node.getId().equals(newNode.getId())){
					nodeToDelete = node;
				}
			}
			list.remove(nodeToDelete);
			list.add(newNode);
			Collections.sort(list);
			updateObservers("PUT", type, id);
		}
		
		System.out.println("FsTimeLine.remoteSignal(from: " + from + ", method: " + method + ", url: " + url);
	}
}
