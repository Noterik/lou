/* 
* Html5AvailableApplication.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springfield.fs.Fs;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * Html5AvailableApplication
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application
 *
 */
public class Html5AvailableApplication {
	private Map<String, Html5AvailableApplicationVersion> versions = new HashMap<String, Html5AvailableApplicationVersion>();

	private String id;
	private String production = null;
	private String development = null;
	private String autodeploy = "none";
	
	public void setId(String i) {
		this.id = i;
	}
	
	public String getId() {
		return id;
	}
	

	
	public int getVersionsCount() {
		return versions.size();
	}
	
	public String getProductionVersion() {
		if (production==null) {
			for(Iterator<String> iter = getVersions().keySet().iterator() ; iter.hasNext(); ) {
				String vname = (String)iter.next();
				Html5AvailableApplicationVersion vv = getVersion(vname);
				if (vv.isProductionVersion()) {
					production = vv.getId();
				}
			}
		}
		return production;
	}
	
	public void deleteCaches() {
		production=null;
		development=null;
	}
	
	public int getProductionVersionCount() {
		for(Iterator<String> iter = getVersions().keySet().iterator() ; iter.hasNext(); ) {
			String vname = (String)iter.next();
			Html5AvailableApplicationVersion vv = getVersion(vname);
			if (vv.isProductionVersion()) {
				return vv.getNodeCount();
			}
		}
		return 0;
	}
	
	public String getDevelopmentVersion() {
		if (development==null) {
			for(Iterator<String> iter = getVersions().keySet().iterator() ; iter.hasNext(); ) {
				String vname = (String)iter.next();
				Html5AvailableApplicationVersion vv = getVersion(vname);
				if (vv.isDevelopmentVersion()) {
					development = vv.getId();
				}
			}
		}
		return development;
	}
	
	public boolean deleteVersion(String version) {
		Html5AvailableApplicationVersion v = getVersion(version);
		if (v!=null) {
			String basepath = "/domain/internal/service/lou/apps/"+getId()+"/versions/"+version;
			System.out.println("DELETE VERSION URL="+basepath);
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers==null) return false;
			smithers.delete(basepath,null,null);
			//LazyHomer.sendRequestBart("DELETE",basepath,null,null);
			versions.remove(v.getId());	
		}
		return true;
	}
	
	public int getDevelopmentVersionCount() {
		for(Iterator<String> iter = getVersions().keySet().iterator() ; iter.hasNext(); ) {
			String vname = (String)iter.next();
			Html5AvailableApplicationVersion vv = getVersion(vname);
			if (vv.isDevelopmentVersion()) {
				return vv.getNodeCount();
			}
		}
		return 0;
	}
	
	public String getStatus() {
		return "100%";
	}
	
	public void loadAutoDeploy(String mode) {
		autodeploy=mode;
	}
	
	public void setAutoDeploy(String mode) {
		autodeploy = mode;
		String path = "/domain/internal/service/lou/apps/"+getId();
		Fs.setProperty(path,"autodeploy",mode);
	}
	
	public String getAutoDeploy() {
		return autodeploy;
	}
	
	
	
	public void addVersion(Html5AvailableApplicationVersion newv) {
		versions.put(newv.getId(), newv);
	}
	
    public Map<String, Html5AvailableApplicationVersion> getVersions(){
    	return versions;
    }
    
    public Iterator<Html5AvailableApplicationVersion> getOrderedVersions(){
    	ArrayList<Html5AvailableApplicationVersion> sorted = new ArrayList<Html5AvailableApplicationVersion>(versions.values());
    	Collections.sort(sorted);
    	return sorted.iterator();
    }
    
    public Html5AvailableApplicationVersion getVersion(String name) {
    	return versions.get(name);
    }
    
    public Html5AvailableApplicationVersion getVersionByUrl(String url) {
    	String name = url.substring(url.indexOf("/versions/")+10);
    	return versions.get(name);
    }
	

}
