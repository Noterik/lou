/* 
* DebuggerComponent.java
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

import java.util.*;

import org.springfield.lou.application.components.BasicComponent;
import org.springfield.lou.application.components.types.debugger.*;
import org.springfield.lou.homer.MargeObserver;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screen.ScreenManager;

/**
 * Comodore 64 style console for debugging purposes
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.application.components.types
 *
 */
public class DebuggerComponent extends BasicComponent implements MargeObserver {
	
	List<String> buffer = new ArrayList<String>();
	int maxlines = 60;
	String currentpath = "/domain/webtv/";
	private enum commandlist { ls,dir,cp,cat,put,pwd,cd,clear,quit,peek,poke,start,mkdir,rm,save,load,ln,filecomplete,history,next,prev,trace,dpost; }
	private String[] ignorelist = {"depth","start","limit","totalResultsAvailable","totalResultsReturned"};
	private String[] silentcommands = {"start","prev","next","history","filecomplete"};
	
	public void put(String from,String msg) {
		String[] cmds = msg.split(" ");
		String command = cmds[0];
		String newcommand = null;
		try {
			switch (commandlist.valueOf(command)) {
				case prev: newcommand = History.prev(); break;
				case next: newcommand = History.next(); break;
				case history: History.history(buffer,cmds); break;
				case filecomplete: newcommand = Filecomplete.execute(buffer, currentpath, cmds); break;
				case dir: Ls.execute(buffer, currentpath,cmds,ignorelist); break;
				case ls : Ls.execute(buffer, currentpath,cmds,ignorelist); break;
				case pwd : Pwd.execute(buffer, currentpath, cmds); break;
				case cd : currentpath = Cd.execute(buffer, currentpath, cmds); break;
				case dpost : Dpost.execute(buffer, currentpath, cmds); break;
				case cp : Cp.execute(buffer, currentpath, cmds); break;
				case cat : Cat.execute(buffer, currentpath, cmds); break;
				case poke : Poke.execute(sm, buffer, from, cmds); break;
				case clear : buffer = Clear.execute(); break;
				case mkdir : Mkdir.execute(buffer, currentpath, cmds); break;
				case start : buffer = Start.execute();break;
				case quit : Quit.execute(sm, buffer, from);break;
				case save : Save.execute(buffer, currentpath, cmds);break;
				case load : Load.execute(buffer, currentpath, cmds);break;
				case put : Put.execute(buffer, currentpath, cmds);break;
				case rm : Rm.execute(buffer, currentpath, cmds);break;
				case ln : Ln.execute(buffer, currentpath, cmds);break;
				case trace : Trace.execute(buffer, this, cmds);break;
			} 
		} catch(Exception e) {
			buffer.add("> illegal command !");	
			e.printStackTrace();
		}
		if (newcommand==null && !Arrays.asList(silentcommands).contains(command)) History.add(cmds);
		int j = 0;
		if (buffer.size()>maxlines) j = buffer.size()-maxlines;
		String body ="";
		for (int i=j;i<buffer.size();i++) {
			String line = buffer.get(i);
			/* need to move to html5 div instead of textarea
			if (line.indexOf("> ")==0) {
				line = "<font color=\"green\">"+line+"</font>";
			}
			*/
			body += line+"\n";
		}
		//setContent("debugger_output",body);
		ScreenManager sm = getScreenManager();
		Screen sfrom = sm.get(from);
		sfrom.putMsg("debugger","app","html("+body+")");
		sfrom.putMsg("debugger","app","prompt("+currentpath+"$)");
		if (newcommand!=null) sfrom.putMsg("debugger","app","newcommand("+newcommand+")");
	}
	
	public void remoteSignal(String from,String method,String url) {
		Trace.remoteSignal(buffer,from,method,url);
		int j = 0;
		if (buffer.size()>maxlines) j = buffer.size()-maxlines;
		String body ="";
		for (int i=j;i<buffer.size();i++) {
			String line = buffer.get(i);
			/* need to move to html5 div instead of textarea
			if (line.indexOf("> ")==0) {
				line = "<font color=\"green\">"+line+"</font>";
			}
			*/
			body += line+"\n";
		}
		//setContent("debugger_output",body);
		super.put("app","html("+body+")");
	}	
}
