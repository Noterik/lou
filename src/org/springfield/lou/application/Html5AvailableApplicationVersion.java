/* 
* Html5AvailableApplicationVersion.java
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

package org.springfield.lou.application;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Html5AvailableApplicationVersion
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application
 *
 */
public class Html5AvailableApplicationVersion implements Comparable<Html5AvailableApplicationVersion> {

	private static Map<String, String> nodes = new HashMap<String, String>();

	private String id;
	private Boolean production = false;
	private Boolean development = false;
	private Html5AvailableApplication app;
	
	public Html5AvailableApplicationVersion(Html5AvailableApplication a) {
		app = a;
	}
	
	public void setId(String i) {
		this.id = i;
	}
	
	public String getId() {
		return id;
	}
	
	public String getStatus() {
		if (production && development) {
			return "production/development";
		} else if (production) {
			return "production";
		} else if (development) {
			return "development";
		}
		return "";
	}
	
	public int getNodeCount() {
		return nodes.size();
	}
	
	public void addNode(String ipnumber) {
		nodes.put(ipnumber, ipnumber); // might have real objects in the future
	}
	
	public void loadDevelopmentState(Boolean b) {
		development = b;
	}
	
	public void setDevelopmentState(Boolean b) {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return;
		System.out.println("SD1");
		if (b==development) return; // was already correct state
		String basepath = "/domain/internal/service/lou/apps/"+app.getId()+"/development";
		if (b==false) {
			// want to turn it off need to remove symlink
			smithers.delete(basepath,null,null);
			//LazyHomer.sendRequestBart("DELETE",basepath,null,null);
		} else {
			// want to turn it on need to symlink it
			String postpath = basepath+"/"+id;
			String newpath = "/domain/internal/service/lou/apps/"+app.getId()+"/versions/"+id;
			String newbody = "<fsxml><attributes><referid>"+newpath+"</referid></attributes></fsxml>"; 
			System.out.println("SD2a="+smithers.put(postpath+"/attributes",newbody,"text/xml"));
			//LazyHomer.sendRequest("PUT",postpath+"/attributes",newbody,"text/xml");
			System.out.println("SD2");
		}
		development = b;
	}
	
	public void loadProductionState(Boolean b) {
		production = b;
	}
	
	public void setProductionState(Boolean b) {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return;
		System.out.println("SP1");
		if (b==production) return; // was already correct state
		String basepath = "/domain/internal/service/lou/apps/"+app.getId()+"/production";
		if (b==false) {
			// want to turn it off need to remove symlink
			smithers.delete(basepath,null,null);
			//LazyHomer.sendRequestBart("DELETE",basepath,null,null);
		} else {
			// want to turn it on need to symlink it
			String postpath = basepath+"/"+id;
			String newpath = "/domain/internal/service/lou/apps/"+app.getId()+"/versions/"+id;
			String newbody = "<fsxml><attributes><referid>"+newpath+"</referid></attributes></fsxml>"; 
			//LazyHomer.sendRequest("PUT",postpath+"/attributes",newbody,"text/xml");
			System.out.println("SP2a="+smithers.put(postpath+"/attributes",newbody,"text/xml"));
			System.out.println("SP2");
		}
		production = b;
	}

	public Boolean isProductionVersion() {
 		return production;
	}
	
	public Boolean isDevelopmentVersion() {
 		return development;
	}
	
	public int getSyncedAmount() {
		// should return the amount of lou's that have this app deployed on sync (not perse production or development)
		return 100;
	}
	
	public int compareTo(Html5AvailableApplicationVersion v2)  {
		 String nid = id.replace("okt", "Oct");
		 nid = nid.replace("nov", "Nov");
		 nid = nid.replace("dec", "Dec");
		 nid = nid.replace("jan", "Jan");
		 nid = nid.replace("feb", "Feb");
		 nid = nid.replace("mar", "Mar");
		 nid = nid.replace("-", " ");
		 Date d1= new Date(nid);
		 
		 Long i1 = new Long(d1.getTime());
		 
		 nid = v2.getId().replace("okt", "Oct");
		 nid = nid.replace("nov", "Nov");
		 nid = nid.replace("dec", "Dec");
		 nid = nid.replace("jan", "Jan");
		 nid = nid.replace("feb", "Feb");
		 nid = nid.replace("mar", "Mar");
		 nid = nid.replace("-", " "); 
		 Date d2= new Date(nid);
		 
		 Long i2 = new Long(d2.getTime());	
		 return i2.compareTo(i1); 
   }
}
