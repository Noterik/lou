/* 
* FSListManager.java
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.http.HttpHelper;

/**
 * FSListManager
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class FSListManager {
	private static Map<String, FSList> lists = new HashMap<String, FSList>();
	
	public static FSList get(String uri) {
		// see if we already have it loaded
		FSList list = lists.get(uri);
		if (list==null && uri.indexOf("*")==-1) {
			List<FsNode> l=getNodes(uri,2,0,0);
			list = new FSList(uri,l);
			lists.put(uri, list);
		}
		return list;
	}
	
	public static FSList add(String uri, FSList biglist) {
		List<FsNode> nodes=getNodes(uri,2,0,0);
		for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
			FsNode n = (FsNode)iter.next();	
			n.asIndex(); // small speedup for later
			biglist.addNode(n);
		}
		return biglist;
	}
	
	public static void put(String uri,FSList list) {
		lists.put(uri,list);
	}
	
	public static FSList get(String uri, int start, int limit) {
		// see if we already have it loaded
		String cacheKey = uri + "/start/"+start+"/limit/"+limit;
		FSList list = lists.get(cacheKey);
		if (list==null) {
			//
			List<FsNode> l=getNodes(uri,2,start,limit);
			list = new FSList(uri,l);
			lists.put(cacheKey, list);
		}
		return list;
	}
	
	public static List<FsNode> getNodes(String path,int depth, int start, int limit) {
		//System.out.println("T="+new Date().getTime());
		List<FsNode> result = new ArrayList<FsNode>();
		String limitStr = "";
		if(limit>0) {
			limitStr = "<limit>"+limit+"</limit>";
		}
		String xml = "<fsxml><properties><start>"+start+"</start>"+limitStr+"<depth>"+depth+"</depth></properties></fsxml>";
		
		String nodes = "";
		if (path.indexOf("http://")==-1) {
			nodes = LazyHomer.sendRequestBart("GET",path,xml,"text/xml");
		} else {
			nodes = HttpHelper.sendRequest("GET", path, "text/xml", "text/xml").toString();
			path = path.substring(path.indexOf("/domain/"));
		}
 		try { 
 			
			Document doc = DocumentHelper.parseText(nodes);

			if (isMainNode(path)) {
				System.out.println("IS MAIN NODE "+new Date().getTime());
				for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
					Element node = (Element)iter.next();
					FsNode nn = new FsNode();
					if (!node.getName().equals("properties")) {
						nn.setName(node.getName());
						nn.setId(node.attribute("id").getText());
						nn.setPath(path+"/"+nn.getId());
						if (node.attribute("referid")!=null) {
							nn.setReferid(node.attribute("referid").getText());
						}
						result.add(nn);
						for(Iterator<Node> iter3 = node.nodeIterator(); iter3.hasNext(); ) {
							Element p2 = (Element)iter3.next();
							if (p2.getName().equals("properties")) {
								for(Iterator<Node> iter4 = p2.nodeIterator(); iter4.hasNext(); ) {
									Object o  = iter4.next();
									if (o instanceof Element) {
										Element p3 = (Element)o;
										String pname = p3.getName();
										String pvalue = p3.getText();
										//System.out.println("NODE NAME="+pname+" "+pvalue);
										nn.setProperty(pname, pvalue);
									}
								}
							}
						}

					} else { // so this is the property node
					}
				}
			} else {
				for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
					Element node = (Element)iter.next();
					for(Iterator<Node> iter2 = node.nodeIterator(); iter2.hasNext(); ) {
						Element node2 = (Element)iter2.next();
						FsNode nn = new FsNode();
						if (!node2.getName().equals("properties")) {
							//System.out.println("NAME2="+node2.getName());
							nn.setName(node2.getName());
							nn.setId(node2.attribute("id").getText());
							nn.setPath(path+"/"+nn.getName()+"/"+nn.getId());
							if (node.attribute("referid")!=null) {
								nn.setReferid(node.attribute("referid").getText());
							}
							result.add(nn);
							for(Iterator<Node> iter3 = node2.nodeIterator(); iter3.hasNext(); ) {
								Element p2 = (Element)iter3.next();
								if (p2.getName().equals("properties")) {
									for(Iterator<Node> iter4 = p2.nodeIterator(); iter4.hasNext(); ) {
										Object o  = iter4.next();
										if (o instanceof Element) {
											Element p3 = (Element)o;
											String pname = p3.getName();
											String pvalue = p3.getText();
											nn.setProperty(pname, pvalue);
										}
									}
								}
							}

						}
					}
				}
			}
 		} catch(Exception e) {
 			e.printStackTrace();
 		}
		//System.out.println("T2="+new Date().getTime());
		return result;
	}
	
	public static boolean isMainNode(String path) {
		int r = path.split("/").length;
		if  ( r % 2 == 0 ) return true;
		return false;
	}	
}
