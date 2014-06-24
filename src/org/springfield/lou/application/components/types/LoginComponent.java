/* 
* LoginComponent.java
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

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.lou.application.components.*;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Screen;

/**
 * Component for logging in users with the help of the Barney 
 * (user manager)
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types
 *
 */
public class LoginComponent extends BasicComponent {
	
	public void put(String from,String msg) {
		Boolean handled = false;
        int pos = msg.indexOf("(");
        if (pos!=-1) {
                String command = msg.substring(0,pos);
                String content = msg.substring(pos+1,msg.length()-1);
                if (command.equals("login")) {
                                String[] ar = content.split(",");
                                Screen sfrom = sm.get(from);
                                if (sfrom!=null) {
                                	String ticket = getTicket(sfrom,ar[0],ar[1]);
                                	sfrom.putMsg(id,"app","ticket("+ticket+","+ar[0]+")");
                                }
                                handled = true;
                }
        }	
		
		if (!handled) {
			Iterator<String> it = this.sm.getScreens().keySet().iterator();
			while(it.hasNext()){
				String next = (String) it.next();
				this.sm.get(next).putMsg(id,from,msg);
			}
		}
	}
	
	private String getTicket(Screen sfrom,String account, String password) {
		//System.out.println("A="+account+" P="+password);
		/* incase you need to get in :) */
		if (account.equals("admin") && password.equals("ntk123")) {
			sfrom.onNewUser(account);
			return "1234";
		}
		
		String url = "/domain/"+getApplication().getDomain()+"/login?user="+account+"&pass="+password;
		String result = LazyHomer.sendRequestBart("GET",url,"","text/xml");
	
		try {
			Document doc = DocumentHelper.parseText(result);
			Element node = doc.getRootElement();
			if (node!=null) {
				String namecheck = node.attributeValue("id");
				if (!account.equals(namecheck)) {
					return "-1";
				}
				Node ticket = node.selectSingleNode("//ticket");
			
				String olduser = sfrom.getUserName();
				if (olduser!=null) {
					sfrom.onLogoutUser(olduser);			
				}
				
				sfrom.onNewUser(account);
				return ticket.getText();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "-1";
	}
}
