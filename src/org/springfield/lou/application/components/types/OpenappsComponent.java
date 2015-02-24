/* 
* OpenappsComponent.java
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

package org.springfield.lou.application.components.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeListener;

import org.springfield.lou.application.ApplicationManager;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.application.components.BasicComponent;
import org.springfield.fs.*;
//import org.springfield.lou.maggie.MaggieLoader;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screen.ScreenManager;

/**
 * OpenappsComponent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types
 *
 */
public class OpenappsComponent extends BasicComponent {
	private String selectedapp = null;
	int option = 1000;
	String screenid = null;
	String userid = null;
	String source = null;
	String level = null;
	String searchkey = null;
	FSList loglist = null;
	
	public OpenappsComponent(){
		this.sm = new ScreenManager();
		ApplicationManager.setOpenappCallback(this);
	}
	
	public void update() {
		String body = "Open applicaitons :<br /><table>";
		body += "<tr><th>rest id</th><th>open screens</th><th>screen idcounter</th><th>user count</th><th>details</th><th>logger</th><th>locate</th></tr>";
		Set<String> keys = ApplicationManager.instance().getApplications().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = (String) it.next();
			Html5ApplicationInterface app = ApplicationManager.instance().getApplication(next);
			body +="<tr><td>"+app.getId()+"</td>";
			body +="<td>"+app.getScreenCount()+"</td>";
			body +="<td>"+app.getScreenIdCounter()+"</td>";
			body +="<td>"+app.getUserCount()+"</td>";
			body +="<td><input type=\"submit\" value=\"show\" onmouseup=\"components.openapps.getdetails('"+app.getId()+"')\"></td>";
			body +="<td><input type=\"submit\" value=\"logger\" onmouseup=\"components.openapps.openlogger('"+app.getId()+"')\"></td>";
			body +="<td><input type=\"submit\" value=\"locate\" onmouseup=\"components.openapps.locate('"+app.getId()+"')\"></td></tr>";
		}
		body += "</table>";
		setContent(body);
		if (selectedapp!=null) {
			updateDetails(selectedapp);
		}
	}
	
	public void updateDetails(String appid) {
		String body = "<div id=\"appdetails_content\"><br /><br />Open screens for : "+appid+"<br /><table align=\"center\">";
		body += "<tr><th>screen id</th><th>username</th><th>screen role</th><th>components</th><th>details</th></tr>";

		Html5ApplicationInterface app = ApplicationManager.instance().getApplication(appid);
		System.out.println("OPENAPP="+app);
		Set<String> keys = app.getScreenManager().getScreens().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = (String) it.next();
			Screen s = app.getScreen(next);
			String username = s.getUserName();
			if (username==null) username = "";
			String role = s.getRole();
			if (role.equals("unknown")) role = "";
			body +="<tr><td>"+s.getId()+"</td>";
			body +="<td>"+username+"</td>";
			body +="<td>"+s.getRole()+"</td>";
			body +="<td>"+s.getComponentManager().size()+"</td>";
			body +="<td>"+(new Date().getTime()-s.getLastSeen())/1000+"</td></tr>";
		}
		body += "</table></div>";		

		setContent("appdetails",body);
	}

	public void put(String from,String msg) {
		Boolean handled = false;
		System.out.println("MSG="+msg);
        int pos = msg.indexOf("(");
        if (pos!=-1) {
        	String command = msg.substring(0,pos);
        	String content = msg.substring(pos+1,msg.length()-1);
            if (command.equals("update")) {
            	update();
            	handled = true;
            } else if (command.equals("startlogger")) {
            	System.out.println("START LOGGER ="+content);
            	startLogger(content);
            	handled = true;
            } else if (command.equals("selectapp")) {
            	selectedapp = content;
    			update();
    			handled = true;
            } else if (command.equals("searchkey")) {
            		if (content.equals("")) { searchkey = null; } else { searchkey = content; }
            		logChange(loglist);
            		handled = true;
            } else if (command.equals("resetlog")) {
            		screenid = null;
            		userid = null;
            		source = null;
            		level = null;
            		searchkey = null;
        			loglist.deleteAll();
        			logChange(loglist);
        			handled = true;
            } else if (command.equals("closelog")) {
        			loglist.deleteAll();
        			setContent("logger","");
        			Screen s = app.getScreenManager().get(from);
        			s.putMsg("openapps","app","closelog()");
        			handled = true;
            } else if (command.equals("searchscreen")) {
        			if (content.equals("all")) { screenid = null; } else { screenid = content; }
        			logChange(loglist);
        			handled = true;
            } else if (command.equals("searchuser")) {
        			if (content.equals("all")) { userid = null; } else { userid = content; }
        			logChange(loglist);
        			handled = true;
            } else if (command.equals("searchsource")) {
        			if (content.equals("all")) { source = null; } else { source = content; }
        			logChange(loglist);
        			handled = true;
            } else if (command.equals("searchlevel")) {
        			if (content.equals("all")) { level = null; } else { level = content; }
        			logChange(loglist);
        			handled = true;
            } else if (command.equals("locate")) {
            	Html5ApplicationInterface app = ApplicationManager.instance().getApplication(content);
            	if (app!=null) {
            		Iterator<String> it = app.getScreenManager().getScreens().keySet().iterator();
            		while(it.hasNext()){
            			String next = (String) it.next();
            			app.getScreenManager().get(next).putMsg("notification","app","sound(pew)");
            		}
            	}
                handled = true;
            }
        }
		if (!handled) super.put(from, msg);
	}
	
	private void startLogger(String appname) {
		StringBuffer body = new StringBuffer();
		body.append("<table width=\"100%\"><tr>");
		body.append("<th>time</th>");
		body.append("<th>screen</th>");
		body.append("<th>user</th>");
		body.append("<th>source</th>");
		body.append("<th>level</th>");
		body.append("<th>log message, search in log fields <button onclick=\"eddie.putLou('openapps','closelog()')\">close</button></th>");
		body.append("</tr></table>");
		setContent("logger",body.toString());	
		ApplicationManager.startLogger(appname);
	}
	
	public void logChange(FSList log) {
		loglist = log;
		StringBuffer body = new StringBuffer();
		body.append("<div id=\"debugscrollarea\" style=\"overflow:scroll; height:100%;\">");
		body.append("<table width=\"100%\"><tr>");
		body.append("<th width=\"10%\">time/lines</th>");
		body.append("<th width=\"10%\">screen</th>");
		body.append("<th width=\"10%\">user</th>");
		body.append("<th width=\"10%\">source</th>");
		body.append("<th width=\"10%\">level</th>");
		body.append("<th width=\"50%\">log message, search in log fields <button onclick=\"eddie.putLou('openapps','resetlog()')\">reset</button><button onclick=\"eddie.putLou('openapps','closelog()')\">close</button></th></tr>");
		if (log!=null) {
		List<FsNode> nodes = null;
		if (searchkey==null) {
			nodes = log.getNodes();
		} else {
			nodes = log.getNodesFiltered(searchkey);			
		}
		
		body.append("<tr><th>"+setOptions(nodes,option)+"</th>");
		nodes = filterOptions(nodes,option);
		
		body.append("<th>"+setScreens(nodes,screenid)+"</th>");
		nodes = filterScreens(nodes,screenid);
		
		body.append("<th>"+setUsers(nodes,userid)+"</th>");
		nodes = filterUsers(nodes,userid);
		
		body.append("<th>"+setSource(nodes,source)+"</th>");
		nodes = filterSource(nodes,source);
		
		body.append("<th>"+setLevel(nodes,level)+"</th>");
		nodes = filterLevel(nodes,level);
		
		body.append("<th>"+setSearch(searchkey)+"</th>");
		body.append("</tr>");
        for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
            FsNode n = (FsNode)iter.next();
            body.append("<tr><td>"+n.getId()+"</td>");
            body.append("<td>"+n.getProperty("screen")+"</td>");
            body.append("<td>"+n.getProperty("user")+"</td>");

            body.append("<td>"+n.getProperty("source")+"</td>");
            body.append("<td>"+n.getProperty("level")+"</td>");
            body.append("<td style=\"text-align:left;\">"+n.getProperty("msg")+"</td></tr>");
        }
		}
        body.append("</table></div>");
        
		setContent("logger",body.toString());
	}
	
	private String setOptions(List<FsNode> nodes,int number) {
		return "";
	}
	
	private List<FsNode> filterOptions(List<FsNode> nodes,int number) {	
		return nodes;
	}

	private String setScreens(List<FsNode> nodes,String screenid) {
        FSSets sets = new FSSets(nodes,"screen");
        String body = "<select onchange=\"eddie.putLou('openapps','searchscreen('+this.options[this.selectedIndex].value+')')\">";
        if (screenid!=null) body += "<option value=\""+screenid+"\">"+screenid+" ("+sets.getSetSize(screenid)+")</option>";
        body += "<option value=\"all\">all ("+nodes.size()+")</option>";
                for (Iterator<String> iter = sets.getKeys() ; iter.hasNext(); ) {
                        String pname = iter.next();
                        int size = sets.getSetSize(pname);
                        body += "<option value=\""+pname+"\">"+pname+" ("+size+")</option>";
                }
        body +="</select>";
		return body;
	}
	
	private List<FsNode> filterScreens(List<FsNode> nodes,String screenid) {
		if (screenid==null) return nodes;
        List<FsNode> results = new ArrayList<FsNode>();
        for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
                FsNode n = (FsNode)iter.next();
                if (n.getProperty("screen").equals(screenid)) {
                        results.add(n);
                }
        }
        return results;
	}
	
	private String setUsers(List<FsNode> nodes,String userid) {
        FSSets sets = new FSSets(nodes,"user");
        String body = "<select onchange=\"eddie.putLou('openapps','searchuser('+this.options[this.selectedIndex].value+')')\">";
        if (userid!=null) body += "<option value=\""+userid+"\">"+userid+" ("+sets.getSetSize(userid)+")</option>";
        body += "<option value=\"all\">all ("+nodes.size()+")</option>";
                for (Iterator<String> iter = sets.getKeys() ; iter.hasNext(); ) {
                        String pname = iter.next();
                        int size = sets.getSetSize(pname);
                        body += "<option value=\""+pname+"\">"+pname+" ("+size+")</option>";
                }
        body +="</select>";
		return body;
	}
	
	private List<FsNode> filterUsers(List<FsNode> nodes,String userid) {	
		if (userid==null) return nodes;
        List<FsNode> results = new ArrayList<FsNode>();
        for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
                FsNode n = (FsNode)iter.next();
                if (n.getProperty("user").equals(userid)) {
                        results.add(n);
                }
        }
        return results;
	}
	
	private String setSource(List<FsNode> nodes,String source) {
        FSSets sets = new FSSets(nodes,"source");
        String body = "<select onchange=\"eddie.putLou('openapps','searchsource('+this.options[this.selectedIndex].value+')')\">";
        if (source!=null) body += "<option value=\""+source+"\">"+source+" ("+sets.getSetSize(source)+")</option>";
        body += "<option value=\"all\">all ("+nodes.size()+")</option>";
                for (Iterator<String> iter = sets.getKeys() ; iter.hasNext(); ) {
                        String pname = iter.next();
                        int size = sets.getSetSize(pname);
                        body += "<option value=\""+pname+"\">"+pname+" ("+size+")</option>";
                }
        body +="</select>";
		return body;
	}
	
	private List<FsNode> filterSource(List<FsNode> nodes,String source) {	
		if (source==null) return nodes;
        List<FsNode> results = new ArrayList<FsNode>();
        for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
                FsNode n = (FsNode)iter.next();
                if (n.getProperty("source").equals(source)) {
                        results.add(n);
                }
        }
        return results;
	}
	
	private String setLevel(List<FsNode> nodes,String level) {
        FSSets sets = new FSSets(nodes,"level");
        String body = "<select onchange=\"eddie.putLou('openapps','searchlevel('+this.options[this.selectedIndex].value+')')\">";
        if (level!=null) body += "<option value=\""+level+"\">"+level+" ("+sets.getSetSize(level)+")</option>";
        body += "<option value=\"all\">all ("+nodes.size()+")</option>";
                for (Iterator<String> iter = sets.getKeys() ; iter.hasNext(); ) {
                        String pname = iter.next();
                        int size = sets.getSetSize(pname);
                        body += "<option value=\""+pname+"\">"+pname+" ("+size+")</option>";
                }
        body +="</select>";
        return body;
	}
	
	private List<FsNode> filterLevel(List<FsNode> nodes,String level) {	
		if (level==null) return nodes;
        List<FsNode> results = new ArrayList<FsNode>();
        for(Iterator<FsNode> iter = nodes.iterator() ; iter.hasNext(); ) {
                FsNode n = (FsNode)iter.next();
                if (n.getProperty("level").equals(level)) {
                        results.add(n);
                }
        }
        return results;
	}
	
	private String setSearch(String searchkey) {
		if (searchkey==null) {
			return("<input id=\"logsearch\" value=\"\" size=\"25\" onchange=\"eddie.putLou('openapps','searchkey('+this.value+')')\" />");
		} else {
			return("<input id=\"logsearch\" value=\""+searchkey+"\" size=\"25\" onchange=\"eddie.putLou('openapps','searchkey('+this.value+')')\" />");
		}
	}
}
