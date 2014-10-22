/* 
* BasicComponent.java
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

package org.springfield.lou.application.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.location.Location;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screen.ScreenManager;

/**
 * BasicComponent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components
 *
 */
public class BasicComponent extends Observable implements ComponentInterface, Observer {
	
	protected String id;
	protected ScreenManager sm;
	protected Html5ApplicationInterface app;
	protected Map<String, String> properties;

	public BasicComponent(){
		this.sm = new ScreenManager();
		this.properties = new HashMap<String, String>();
	}
	
	public void setApplication(Html5ApplicationInterface a) {
		this.app = a;
	}
	
	public Html5ApplicationInterface getApplication() {
		return app;
	}
	
	public void setId(String name) {
		this.id = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void put(String from,String content) {
		if(content.indexOf("url(")!=-1){
			String vid = content.replace("url(", "").replace(")", "");
			content = "src(";
			if(vid.equals("t12")){ content += "/domain/springfieldwebtv/user/david/collection/3/presentation/10/)";}
			else if(vid.equals("t22")){content += "/domain/springfieldwebtv/user/admin/collection/2/presentation/2)";}
			else if(vid.equals("rbb2")){content += "/domain/linkedtv/user/rbb/collection/8/presentation/1)";}
			else if(vid.equals("avro2")){content += "/domain/linkedtv/user/avro/collection/2/presentation/3)";}
			else if(vid.equals("remix2")){content += "/domain/springfieldwebtv/user/admin/collection/12/presentation/1/)";}
			else if(vid.equals("youtube")){content += "/domain/springfieldwebtv/user/david/collection/3/presentation/23)";}
			else if(vid.equals("t2")){content += "http://stream12.noterik.com/progressive/stream12/domain/springfieldwebtv/user/admin/video/6/rawvideo/3/raw.mp4)";}
			else if(vid.equals("rbb")){content += "http://stream6.noterik.com/progressive/stream6/domain/linkedtv/user/rbb/video/59/rawvideo/3/raw.mp4)";}
			else if(vid.equals("avro")){content += "http://stream7.noterik.com/progressive/stream7/domain/linkedtv/user/avro/video/5/rawvideo/3/raw.mp4)";}
			else if(vid.equals("remix")){content += "http://stream9.noterik.com/progressive/stream9/domain/springfieldwebtv/user/admin/video/82/rawvideo/3/raw.mp4)";}
			else{content += "http://video-js.zencoder.com/oceans-clip.mp4)";}
		}
		else if(content.indexOf("setdata(")!=-1){
			this.handlePutProperties(content);
		}
		else if(content.indexOf("getdata(")!=-1){
			content = this.handleGetProperties(content);
			System.out.println("GETDATA CONTENT:: " + content);
		}
		//System.out.println("sending message: "+content);
		
		String[] sr = new String[sm.getScreens().keySet().size()];
		sr = sm.getScreens().keySet().toArray(sr);
		for(String s : sr) {
		  //  System.out.println(s);
			Screen t = sm.get(s);
			if (t!=null) t.putMsg(id,from,content);
		}
		
		/*
		Iterator<String> it = this.sm.getScreens().keySet().iterator();
		while(it.hasNext()){
			String next = (String) it.next();
		//	System.out.println("ID="+id+"F="+from+" C="+content);
			this.sm.get(next).putMsg(id,from,content);
		}	
		*/	
	}
	
	public void putOnScope(Screen scopescreen,String from,String content) {
		if(content.indexOf("url(")!=-1){
			String vid = content.replace("url(", "").replace(")", "");
			content = "src(";
			if(vid.equals("t12")){ content += "/domain/springfieldwebtv/user/david/collection/3/presentation/10/)";}
			else if(vid.equals("t22")){content += "/domain/springfieldwebtv/user/admin/collection/2/presentation/2)";}
			else if(vid.equals("rbb2")){content += "/domain/linkedtv/user/rbb/collection/8/presentation/1)";}
			else if(vid.equals("avro2")){content += "/domain/linkedtv/user/avro/collection/2/presentation/3)";}
			else if(vid.equals("remix2")){content += "/domain/springfieldwebtv/user/admin/collection/12/presentation/1/)";}
			else if(vid.equals("youtube")){content += "/domain/springfieldwebtv/user/david/collection/3/presentation/23)";}
			else if(vid.equals("t2")){content += "http://stream12.noterik.com/progressive/stream12/domain/springfieldwebtv/user/admin/video/6/rawvideo/3/raw.mp4)";}
			else if(vid.equals("rbb")){content += "http://stream6.noterik.com/progressive/stream6/domain/linkedtv/user/rbb/video/59/rawvideo/3/raw.mp4)";}
			else if(vid.equals("avro")){content += "http://stream7.noterik.com/progressive/stream7/domain/linkedtv/user/avro/video/5/rawvideo/3/raw.mp4)";}
			else if(vid.equals("remix")){content += "http://stream9.noterik.com/progressive/stream9/domain/springfieldwebtv/user/admin/video/82/rawvideo/3/raw.mp4)";}
			else{content += "http://video-js.zencoder.com/oceans-clip.mp4)";}
		}
		else if(content.indexOf("setdata(")!=-1){
			this.handlePutProperties(content);
		}
		else if(content.indexOf("getdata(")!=-1){
			content = this.handleGetProperties(content);
			System.out.println("GETDATA CONTENT:: " + content);
		}
		//System.out.println("sending message: "+content);
		Iterator<String> it = this.sm.getScreens().keySet().iterator();
		while(it.hasNext()){
			String next = (String) it.next();
		//	System.out.println("ID="+id+"F="+from+" C="+content);
			Screen s = sm.get(next);
			
			Location loc =s.getLocation();
			Location scopeloc = scopescreen.getLocation();
			if (loc!=null && scopeloc!=null) {
				String locid = loc.getId(); 
				String slocid = scopeloc.getId(); 
				if (locid.equals(slocid)) {
					s.putMsg(id,from,content);
				}
			} else {
				System.out.println("BasicComponent : LOCATION IS NULL SHOULD NOT HAPPEN !");
				s.putMsg(id,from,content);
			}
		}		
	}
	
	public ScreenManager getScreenManager(){
		return this.sm;
	}
	
	public void setContent(String content) {
		Iterator<String> it = this.sm.getScreens().keySet().iterator();
		while(it.hasNext()){
			String next = (String) it.next();
			this.sm.get(next).setContent(id, content);
			
		}
	}
	
	public void setContent(String id,String content) {
		Iterator<String> it = this.sm.getScreens().keySet().iterator();
		while(it.hasNext()){
			String next = (String) it.next();
			this.sm.get(next).setContent(id, content);
		}
	}
	
	public void putProperty(String varName, String varValue){
		this.properties.put(varName, varValue);
    }

    public String getProperty(String var){
    	return this.properties.get(var);
    }
	
    public void handlePutProperties(String content){
		String pn = content.substring(content.indexOf("setdata(")+8, content.indexOf(","));
		String pv = content.substring(content.indexOf(",")+1, content.indexOf(")"));
		this.properties.put(pn, pv);
    }
    
    public String handleGetProperties(String content){
		String pn = content.substring(content.indexOf("getdata(")+8, content.indexOf(")"));
		return "setdata(" + pn + "," + this.properties.get(pn) + ")";
    }
    
    public Map<String,String> getProperties(){
    	return this.properties;
    }

	@Override
	public void update(Observable o, Object arg) {
		String[] ar1 = ((String)arg).split(":");
		String[] ar2 = ((String)ar1[1]).split(",");
        this.put(ar1[0], "notify("+ar2[0]+","+ar2[1]+")");		
	}
	
	public void change(Object arg){
		setChanged();
		notifyObservers(arg);
	}	
}
