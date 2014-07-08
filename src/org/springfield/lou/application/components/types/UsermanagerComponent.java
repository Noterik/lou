/* 
* UsermanagerComponent.java
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

import java.util.List;

import org.springfield.lou.application.components.BasicComponent;
import org.springfield.fs.*;
import org.springfield.lou.homer.LazyMarge;
import org.springfield.lou.homer.MargeObserver;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

/**
 * UsermanagerComponent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types
 *
 */
public class UsermanagerComponent extends BasicComponent implements MargeObserver {

	private enum commandlist { showusers,showuser,setusersetting; }

	private static FsNode usernode = null;
	private static String username = null;
	
	public void put(String from,String msg) {
        int pos = msg.indexOf("(");
        String command = msg.substring(0,pos);
        String content = msg.substring(pos+1,msg.length()-1);
		try {
			switch (commandlist.valueOf(command)) {
				case showusers: showUsers(content); break;
				case showuser: showUser(content); break;
				case setusersetting: setUserSetting(content); break;
			}
		} catch(Exception e) {
			
		}
	}
	
	private void showUser(String user) {
		System.out.println("USER SHOW "+user);
		String body ="<form onsubmit=\"return false\"><table>";
		body += "<tr><th colspan=\"3\">account settings for "+user+"</th></tr>";
		String nodepath="/domain/"+getApplication().getDomain()+"/user/"+user+"/account/default";
		FsNode node = Fs.getNode(nodepath);
		if (node==null) {
			setContent("usermanager_userproperties","");
			return;
		}
		
		// test is we can track changes in smithers.
		usernode = node; 
		username = user;
		LazyMarge.addObserver(usernode.getPath(), this);
		
		String firstname = node.getProperty("firstname","");
		String lastname = node.getProperty("lastname","");
		String password = node.getProperty("password","");
		String email = node.getProperty("email","");
		String phoneNum = node.getProperty("phoneNum","");
		String role = node.getProperty("role","");
		String birthdata = node.getProperty("birthdata","");
		
		body += createPropertyLine("firstname", firstname, user, "");
		body += createPropertyLine("lastname", lastname, user, "");
		body += createPropertyLine("password", password, user, "password");
		body += createPropertyLine("email", email, user, "");
		body += createPropertyLine("phoneNum", phoneNum, user, "");
		body += createPropertyLine("role", role, user, "");
		body += createPropertyLine("birthdata", birthdata, user, "");
		body += "</table></form>";
		setContent("usermanager_userproperties",body);
	}
	
	private String createPropertyLine(String name,String value,String user,String type) {
		String line = "<tr><td>"+name+"</td><td><input id=\""+name+"\" value=\""+value+"\" /></td><td><input type=\"submit\" value=\"save\" onmouseup=\"return components.usermanager.saveProperty('"+user+"','"+name+"',form."+name+".value)\"/></td></tr>";
		if (type.equals("password")) {
			line = "<tr><td>"+name+"</td><td><input type=\"password\" id=\""+name+"\" value=\""+value+"\" /></td><td><input type=\"submit\" value=\"save\" onmouseup=\"return components.usermanager.saveProperty('"+user+"','"+name+"',form."+name+".value)\"/></td></tr>";
		}
		return line;
	}
		
	private void showUsers(String searchkey) {
		System.out.println("SHOW USERS"+searchkey);
		String body ="<table>";
		body += "<tr><th colspan=\"2\">account name</th></tr>";
		//List<FsNode> nodes = Fs.getNodes("/domain/"+getApplication().getDomain()+"/user",0);
		
		FSList fslist = FSListManager.get("/domain/"+getApplication().getDomain()+"/user",0,false);
		List<FsNode> nodes = null;
		if (searchkey!=null && !searchkey.equals("") && !searchkey.equals("*")) {
			body += "<tr><td colspan=\"2\"><input name=\"key\" size=\"12\" value=\""+searchkey+"\" onchange=\"eddie.putLou('usermanager','showusers('+this.value+')')\"/></td></tr>";

			nodes = fslist.getNodesByIdMatch(searchkey);
		} else {
			body += "<tr><td colspan=\"2\"><input name=\"key\" size=\"12\" value=\"*\" onchange=\"eddie.putLou('usermanager','showusers('+this.value+')')\"/></td></tr>";
			nodes = fslist.getNodes();
		}
		
		int max = nodes.size();
		if (max>20) max = 20; // until we have length filter
		
		for (int i=0;i<max;i++) {
			FsNode node = nodes.get(i);
			body += "<tr><td colspan=\"2\" onmouseover=\"this.className='on'\" onmouseout=\"this.className='off'\" onmouseup=\"eddie.putLou('usermanager','showuser("+node.getId()+")')\">"+node.getId()+"</td></tr>";
		}
		body += "<tr><td><input name=\"newaccountname\" size=\"12\"/></td><td><input type=\"submit\" value=\"add\" /></td></tr>";
		body += "</table>";
		setContent("usermanager_userlist",body);
	}
	
	public void remoteSignal(String from,String method,String url) {
		//System.out.println("FROM="+from+" METHOD="+method+" URL="+url);
		if (usernode!=null && usernode.getPath().equals(url)) {
			FsNode newnode = Fs.getNode(url);
			if (Fs.changedProperties(usernode,newnode).hasNext()) {
				// at least one change ! so update
				showUser(username);
			}
		}
	}
	
	private void setUserSetting(String content) {
		System.out.println("USETSETTING="+content);
		String[] params = content.split(",");
		if (params.length==3) {
			String user = params[0];
			String name = params[1];
			String value = params[2];
			String path = "/domain/"+getApplication().getDomain()+"/user/"+user+"/account/default";
			System.out.println("PATH="+path+" P="+name+" V="+value);
			if (name.equals("password")) {
				System.out.println("SETTING PASSWORD");
				ServiceInterface barney = ServiceManager.getService("barney");
				if (barney!=null) {
					System.out.println("Barney interface = "+barney);			
					String result = barney.put("setpassword("+getApplication().getDomain()+","+user+")", value, null);
					Fs.setProperty(path, name, "$shadow");
				}
			} else {
				ServiceInterface barney = ServiceManager.getService("barney");
				if (barney!=null) {
					Fs.setProperty(path, name, value);	
				}
			}
		}
	}
}
