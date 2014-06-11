/* 
* FsNode.java
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

import org.springfield.lou.homer.LazyHomer;

/**
 * FsNode
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class FsNode implements Comparable<FsNode>  {
	
	private String name;
	private String id;
    private String referid;
	private String path;
	private float starttime;
	private float duration;
	private String imageBaseUri;
	private String asindex;
	
	private Map<String, String> properties = new HashMap<String, String>();

	public void setPath(String p) {
		path = p;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setId(String i) {
		id = i;
	}
	
	public String getId() {
		return id;
	}
	
    public void setReferid(String i) {
    	System.out.println("SET REFERID="+i);
        referid = i;
    }

    public String getReferid() {
        return referid;
    }
	
	public float getStarttime() {
		return starttime;
	}
	
	public float getDuration() {
		return duration;
	}
	
	public void setImageBaseUri(String imageBaseUri) {
		this.imageBaseUri = imageBaseUri;
	}
	
	public void setProperty(String name,String value) {
		properties.put(name, value);
		if (name.equals("starttime")) {
			starttime = Float.parseFloat(value);
		} else if (name.equals("duration")) {
			duration = Float.parseFloat(value);
		}
		
		// ok lets kill the search index
		asindex=null;
		asIndex();
	}
	
	public String getScreenShotUrl() {
		String times = getShotsFormat(getStarttime()/1000);
		if (imageBaseUri != null) {
			return imageBaseUri+"/"+times;
		}
		//TODO: change this to a generic 404 image?
		return times;
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}

	public String getProperty(String name,String def) {
		String value =  properties.get(name);
		if (value!=null) {
			return value;
		} else {
			return def;
		}
	}
	
	public Iterator<String> getKeys() {
		return properties.keySet().iterator();
	}
	
	public int compareTo(FsNode n) throws ClassCastException {
		 Float f1 = getStarttime();
		 Float f2 = n.getStarttime();
		 return f1.compareTo(f2);
   }
	
	private String getShotsFormat(double seconds) {
		String result = null;
		int sec = 0;
		int hourSecs = 3600;
		int minSecs = 60;
		int hours = 0;
		int minutes = 0;
		while (seconds >= hourSecs) {
			hours++;
			seconds -= hourSecs;
		}
		while (seconds >= minSecs) {
			minutes++;
			seconds -= minSecs;
		}
		sec = new Double(seconds).intValue();
		result = "h/" + hours;
		result += "/m/" + minutes ;
		result += "/sec" + sec + ".jpg";
		return result;
	}
	
	public String asXML(){
		String xml = "<" + this.getName() + " id=\"" + this.getId() + "\"";
		if(this.getReferid() != null){
			xml += " referid=\"" + this.getReferid() + "\"";
		}
		xml += ">";
		
		xml += "<properties";
		if(!this.getKeys().hasNext()){
			xml += " /";
		}
		xml += ">";
		
		for(Iterator<String> i = this.getKeys(); i.hasNext();){
			String key = i.next();
			xml += "<" + key + ">";
			xml += this.getProperty(key);
			xml += "</" + key + ">";
		}
		
		xml += "</properties>";
		xml += "</" + this.getName() + ">";
		
		return xml;
	}
	
	public String asIndex() {
		if (asindex!=null) return asindex;
		
		StringBuffer buf  = new StringBuffer();
		
		for(Iterator<String> i = this.getKeys(); i.hasNext();){
			String key = i.next();
			buf.append((this.getProperty(key).toLowerCase()));
		}		
		asindex = buf.toString();
		return asindex;
	}
}
