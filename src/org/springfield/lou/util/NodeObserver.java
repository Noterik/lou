/* 
* NodeObserver.java
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

package org.springfield.lou.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springfield.fs.FSXMLStrainer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.homer.LazyMarge;
import org.springfield.lou.homer.MargeObserver;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * NodeObserver
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class NodeObserver implements MargeObserver {
	
	private String node;
	private FSXMLStrainer strainer;
	private Document xml;
	public static enum OrderDirection {
		ASC,
		DESC
	};
	
	public NodeObserver(String node){
		System.out.println("NodeObserver(" + node + ")");
		this.node = node;
		LazyMarge.addObserver(node + "/*", this);
		this.sendInitialState();
	}
	
	public NodeObserver(String node, FSXMLStrainer strainer){
		System.out.println("NodeObserver(" + node + ", " + strainer + ")");
		this.node = node;
		LazyMarge.addObserver(node + "/*", this);
		this.strainer = strainer;
		this.sendInitialState();
	}
	
	private void sendInitialState(){
		System.out.println("NodeObserver.sendInitialState()");
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return;
		String responseStr = smithers.get(node, "<fsxml><properties><depth>2</depth></properties></fsxml>", "text/xml");
	
	//	String responseStr = LazyHomer.sendRequestBart("GET", node, "<fsxml><properties><depth>2</depth></properties></fsxml>", "text/xml");
		try {
			Document response = DocumentHelper.parseText(responseStr);
			xml = filterAllowed(response);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public Document get(String xpath){
		System.out.println("NodeObserver.get(" + xpath + ")");
		List<Node> results = xml.selectNodes(xpath);
		Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("fsxml");
        for(Iterator<Node> i = results.iterator(); i.hasNext();){
        	Node el = (Node) i.next().clone();
        	el.detach();
        	root.add(el);
        }
        return doc;
	}
	
	public void setOrder(Comparator<Node> sorting, OrderDirection direction){
		System.out.println("NodeObserver.setOrder(" + sorting + "," + direction + ")");
		List<Node> results = xml.selectNodes("/fsxml/*");
		System.out.println("RESULTS: " + results.size());
		Collections.sort(results, sorting);
		if(direction.equals(OrderDirection.DESC)){
			System.out.println("SORT DESCENDING");
			Collections.sort(results, Collections.reverseOrder(sorting));
		}else{
			System.out.println("SORT ASCENDING");
			Collections.sort(results, sorting);
		}
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("fsxml");
		for(Iterator<Node> i = results.iterator(); i.hasNext();){
        	Node el = (Node) i.next().clone();
        	el.detach();
        	root.add(el);
        }
		xml = doc;
	};
	
	private Document filterAllowed(Document xmlDoc){
		System.out.println("NodeObserver.filterAll(" + xmlDoc + ")");
		if(strainer != null){
			return strainer.getFilteredFSXML(xmlDoc);
		}
		return xmlDoc;
	}
	
	@Override
	public void remoteSignal(String from, String method, String url) {
		// TODO Auto-generated method stub
		System.out.println("NodeObserver.remoteSignal(" + from + ", " + method + ", " + url + ")");
		if(method.equals("PUT") || method.equals("DELETE")){
			String[] urls = url.split(",");
			String updatedUrl = urls[0];
			
			
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers==null) return;
			
			String updatedNode = smithers.get(updatedUrl, "<fsxml><properties><depth>4</depth></properties></fsxml>", "text/xml");
			try {
				Document response = DocumentHelper.parseText(updatedNode);
				List<Node> errors = response.selectNodes("//error");
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
