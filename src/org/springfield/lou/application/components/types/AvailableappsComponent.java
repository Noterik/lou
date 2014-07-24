/* 
* AvailableappsComponent.java
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

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.springfield.lou.application.ApplicationManager;
import org.springfield.lou.application.Html5AvailableApplication;
import org.springfield.lou.application.Html5AvailableApplicationVersion;
import org.springfield.lou.application.components.BasicComponent;
import org.springfield.lou.screen.ScreenManager;

/**
 * AvailableappsComponent
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types
 *
 */
public class AvailableappsComponent extends BasicComponent {
	private String selectedapp = null;
	
	public AvailableappsComponent(){
		this.sm = new ScreenManager();
	}
	
	public void update() {
		ApplicationManager.instance().loadAvailableApps();
		String body = "Available applicaitons :<br /><table align=\"center\" width=\"80%\">";
		body += "<tr><th>id</th><th>versions</th><th>production</th><th>development</th><th>status</th><th>details</th></tr>";

		Set<String> keys = ApplicationManager.instance().getAvailableApplications().keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String next = (String) it.next();
			//System.out.println("APPNAME="+next);
			Html5AvailableApplication vapp = ApplicationManager.instance().getAvailableApplication(next);
			body +="<tr><td>"+vapp.getId()+"</td>";
			body +="<td>"+vapp.getVersionsCount()+"</td>";
			String pv= vapp.getProductionVersion();
			String dv= vapp.getDevelopmentVersion();
			if (pv==null) pv ="";
			if (dv==null) dv ="";
			body +="<td>"+pv+" ("+vapp.getProductionVersionCount()+")</td>";
			body +="<td>"+dv+" ("+vapp.getDevelopmentVersionCount()+")</td>";
			body +="<td>"+vapp.getStatus()+"</td>";

			body +="<td><input type=\"submit\" value=\"show\" onmouseup=\"components.availableapps.getdetails('"+vapp.getId()+"')\"></td>";
		}
		body += "<tr><td colspan=\"2\"></td><td>action</td><td><input type=\"submit\" value=\"upload\" onmouseup=\"components.availableapps.uploadnew()\"></td>";
		body += "<td colspan=\"2\"></td></tr></table>";
		setContent(body);
	}
	
	public void updateDetails(String appid) {
		setContent("appdetails","loading");
		long starttime = new Date().getTime(); // we track the request time for debugging only
		ApplicationManager.instance().loadAvailableApps();
		//System.out.println("UPDATE DETAIL CALLED "+appid);
		long endtime = new Date().getTime();
		StringBuffer body = new StringBuffer();
		body.append("<div id=\"appdetails_content\"><br /><br />Application details for : "+appid+"<br /><table align=\"center\" width=\"50%\">");
		Html5AvailableApplication vapp = ApplicationManager.instance().getAvailableApplication(appid);

		body.append("<tr><th>id</th><td>"+vapp.getId()+"</td></tr>");
		body.append("<tr><th>versions</th><td>"+vapp.getVersionsCount()+"</td></tr>");
		body.append("<tr><th>auto deploy</th><td><select onchange=\"components.appdetails.setAutoDeploy('"+vapp.getId()+"',this.options[this.selectedIndex].value)\"><option value=\""+vapp.getAutoDeploy()+"\">"+vapp.getAutoDeploy()+"</option><option>development</option><option>production</option><option>development/production</option><option>none</option></select></td></tr>");
		body.append("<tr><th>action</th><td><input type=\"submit\" value=\"upload\" onmouseup=\"components.availableapps.upload('"+vapp.getId()+"')\"></td></tr>");
		body.append("<tr><th colspan=\"2\"><input type=\"submit\" value=\"done\" onmouseup=\"components.appdetails.done()\"></th></tr>");
		body.append("</table>");
		body.append("<br /><br />verions ordered by date<br />");
		body.append("<table align=\"center\" width=\"70%\" >");
		body.append("<tr><th>version</th><th>synced</th><th></th><th>status</th><th colspan=\"3\">actions</th></tr>");

		Iterator<Html5AvailableApplicationVersion> it = vapp.getOrderedVersions();
		while(it.hasNext()){
			Html5AvailableApplicationVersion version = it.next();
			String status = version.getStatus();
			String synced = version.getSyncedAmount()+"%";
			if (status.equals("production/development")) {
				body.append("<tr><td><font color=\"green\">"+version.getId()+"</font></td>");
				body.append("<td>"+synced+"</td><td><input type=\"submit\" value=\"details\" onmouseup=\"components.availableapps.showsyncednodes('"+vapp.getId()+","+version.getId()+"')\"></td>");	
				body.append("<td><font color=\"green\">"+status+"</font></td>");		
				body.append("<td colspan=\"3\"></td>");
				body.append("</tr></font>");
			} else if (status.equals("development")) {
				body.append("<tr><td><font color=\"orange\">"+version.getId()+"</font></td>");
				body.append("<td>"+synced+"</td><td><input type=\"submit\" value=\"details\" onmouseup=\"components.availableapps.showsyncednodes('"+vapp.getId()+","+version.getId()+"')\"></td>");	
				body.append("<td><font color=\"orange\">"+status+"</font></td>");
				body.append("<td></td><td><input type=\"submit\" value=\"prod\" onmouseup=\"components.availableapps.makeproduction('"+vapp.getId()+","+version.getId()+"')\"></td><td></td>");
				body.append("</tr></font>");
			} else if (status.equals("production")) {
				body.append("<tr><td><font color=\"red\">"+version.getId()+"</font></td>");
				body.append("<td>"+synced+"</td><td><input type=\"submit\" value=\"details\" onmouseup=\"components.availableapps.showsyncednodes('"+vapp.getId()+","+version.getId()+"')\"></td>");	
				body.append("<td><font color=\"red\">"+status+"</font></td>");
				body.append("<td><input type=\"submit\" value=\"devel\" onmouseup=\"components.availableapps.makedevelopment('"+vapp.getId()+","+version.getId()+"')\"></td><td></td><td></td>");
				body.append("</tr></font>");
			} else {
				body.append("<tr><td>"+version.getId()+"</td>");
				body.append("<td>"+synced+"</td><td><input type=\"submit\" value=\"details\" onmouseup=\"components.availableapps.showsyncednodes('"+vapp.getId()+","+version.getId()+"')\"></td>");	
				body.append("<td>"+status+"</td>");
				body.append("<td><input type=\"submit\" value=\"devel\" onmouseup=\"components.availableapps.makedevelopment('"+vapp.getId()+","+version.getId()+"')\"></td><td><input type=\"submit\" value=\"prod\" onmouseup=\"components.availableapps.makeproduction('"+vapp.getId()+","+version.getId()+"')\"></td><td><input type=\"submit\" value=\"delete\" onmouseup=\"components.availableapps.deleteversion('"+vapp.getId()+","+version.getId()+"')\"></td>");
				body.append("</tr>");
			}			
		}
		body.append("</table></div>");
		setContent("appdetails",body.toString());
		//long endtime = new Date().getTime();
		//System.out.println("QUERYTIME="+(endtime-starttime));
	}

	public void put(String from,String msg) {
		//System.out.println("MSG="+msg);
		Boolean handled = false;
        int pos = msg.indexOf("(");
        if (pos!=-1) {
                String command = msg.substring(0,pos);
                String content = msg.substring(pos+1,msg.length()-1);
                if (command.equals("update")) {
                	update();
                    handled = true;
                } else if (command.equals("selectapp")) {
    				updateDetails(content);
                    handled = true;
                } else if (command.equals("upload")) {
    				ApplicationManager.instance().upload(content);
    				update(); // update the window with apps;
    				updateDetails(content);
                    handled = true;
                } else if (command.equals("uploadnew")) {
    				ApplicationManager.instance().uploadnew();
    				update(); // update the window with apps;
                    handled = true;
                } else if (command.equals("deleteversion")) {
    				String[] a = content.split(","); 
    				ApplicationManager.instance().deleteVersion(a[0],a[1]);
    				update(); // update the window with apps;
    				updateDetails(a[0]);
                    handled = true;
                } else if (command.equals("makeproduction")) {
    				String[] a = content.split(","); 
    				ApplicationManager.instance().makeProduction(a[0],a[1]);
    				update(); // update the window with apps;
    				updateDetails(a[0]);
                    handled = true;
                } else if (command.equals("setautodeploy")) {
    				String[] a = content.split(","); 
    				ApplicationManager.instance().setAutoDeploy(a[0],a[1]);
    				update(); // update the window with apps;
    				updateDetails(a[0]);
                    handled = true;
                } else if (command.equals("makedevelopment")) {
    				String[] a = content.split(","); 
    				ApplicationManager.instance().makeDevelopment(a[0],a[1]);
    				update(); // update the window with apps;
    				updateDetails(a[0]);
                    handled = true;
                }
        }
		if (!handled) super.put(from, msg);
	}
}