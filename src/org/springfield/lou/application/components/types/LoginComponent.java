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
import org.springfield.fs.FsNode;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.application.components.*;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Screen;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

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
                } else if (command.equals("signup")) {
                    Screen sfrom = sm.get(from);
                    if (sfrom!=null) {
                    	signup(sfrom,content);
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
	
	private void signup(Screen sfrom,String content) {
		String account = " "; // all are one space to make parser easer fill fail anyway	
		String email = " "; 
		String password = " ";	
		String repeatpassword = " ";	
		String[] params = content.split(",");
		if (params.length>0) {
			account = params[0];
		}
		if (params.length>1) {
			email = params[1];
		}
		if (params.length>2) {
			password = params[2];
		}
		if (params.length>3) {
			repeatpassword = params[3];
		}
		
		
		Html5ApplicationInterface app = sfrom.getApplication();
		ServiceInterface barney = ServiceManager.getService("barney");
		if (barney!=null) {
			// perform a few checks to see if this is a valid account
			String result = barney.get("approvedaccountname("+sfrom.getApplication().getDomain()+","+account+")", null, null);
			if (result.equals("false")) {
				app.executeActionlist(sfrom,"login/illegalaccountname");
				return;
			}
			
			result = barney.get("userexists("+sfrom.getApplication().getDomain()+","+account+")", null, null);
			if (result.equals("true")) {
				app.executeActionlist(sfrom,"login/accountinuse");
				return;
			}
			
			result = barney.get("validemail("+sfrom.getApplication().getDomain()+","+email+")", null, null);
			if (result.equals("false")) {
				app.executeActionlist(sfrom,"login/invalidemail");
				return;
			}
			if (!password.equals(repeatpassword)) {
				app.executeActionlist(sfrom,"login/differentpasswords");
				return;
			}
			result = barney.get("passwordquality("+sfrom.getApplication().getDomain()+","+password+")", null, null);
			if (result.equals("false")) {
				app.executeActionlist(sfrom,"login/passwordtoweak");
				return;
			}
			
			String random = barney.get("createaccount("+sfrom.getApplication().getDomain()+","+account+","+email+","+password+")", null, null);	
			result = barney.get("sendsignupmail("+sfrom.getApplication().getDomain()+","+account+","+random+","+sfrom.getApplication().getHtmlPath()+")", null, null);
			app.executeActionlist(sfrom,"login/signuplinksend");
		}

	}
	
	private String getTicket(Screen sfrom,String account, String password) {
		//System.out.println("A="+account+" P="+password);
		/* incase you need to get in :) */
		/*
		if (account.equals("admin") && password.equals("#@SQS!@##@#")) {
			System.out.println("ADMIN LOGIN");
			sfrom.onNewUser(account);
			return "1234";
		}
		*/
		
		ServiceInterface barney = ServiceManager.getService("barney");
		if (barney!=null) {
			System.out.println("Barney interface = "+barney);
			
			System.out.println("APPS="+sfrom.getApplication().getDomain());
			
			String ticket = barney.get("login("+sfrom.getApplication().getDomain()+","+account+","+password+")", null, null);
			if (ticket!=null && !ticket.equals("-1")) {
				//System.out.println("TICKET = "+ticket);
				//System.out.println("HTMLPATH="+sfrom.getApplication().getHtmlPath());
				sfrom.onNewUser(account);
				return ticket;
			}
			sfrom.onLoginFail(account);
			return "-1";
		}
		System.out.println("Can't find barney for login !!!!");
		sfrom.onLoginFail(account);
		return "-1"; // backwards compatible		
	}
}
