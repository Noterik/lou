package org.springfield.lou.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.servlet.LouServlet;

public class ActionListManager {
	protected Map<String, ActionList> actionlists = new HashMap<String, ActionList>();
	protected Html5Application app;
	
	public ActionListManager(Html5Application a) {
		app = a;
		readActionLists();
	}
	
	public void executeList(Screen s,String name) {
		int pos = name.indexOf("(");
        if (pos!=-1) {
            String content = name.substring(pos+1,name.length()-1);
            name = name.substring(0,pos);
    			System.out.println("execute action list "+name+" on "+app.getId());
    			ActionList maf = actionlists.get(name);
    			if (maf!=null) {
    				maf.execute(s,content);
    			}
        } else {
        		System.out.println("execute action list "+name+" on "+app.getId());
        		ActionList maf = actionlists.get(name);
        		if (maf!=null) {
        			maf.execute(s);
        		}
        }
	}
	
	private void readActionLists() {
		String actiondir = app.getHtmlPath()+"/actionlists";

		System.out.println("ACTIONDIR="+actiondir);
		File dir = new File(actiondir);
		if (!dir.exists()) return; // return if no actionlists dir
		
		String[] files = dir.list();

		for (int i=0;i<files.length;i++) {
			String filename = files[i];
			System.out.println("ACTION FILE="+filename);
			ActionList maf = new ActionList(app,filename.substring(0,filename.indexOf(".")));
			actionlists.put(maf.getName(), maf);
			try {
				BufferedReader br = new BufferedReader(new FileReader(actiondir+File.separator+filename));
				StringBuffer str = new StringBuffer();
				String command = br.readLine();
				while (command != null) {
					String urlmapping = command.toLowerCase();
					if (urlmapping.indexOf("seturltrigger")==0) {
						urlmapping = urlmapping.substring(urlmapping.indexOf("(")+1);
						urlmapping = urlmapping.substring(0,urlmapping.indexOf(")"));
						LouServlet.addUrlTrigger(urlmapping,maf.getName());
					} else {
						if (command.startsWith(".when ")) {
							maf.startWhenBlock(command);
						} else if (command.equals("")) {
							maf.endBlock();
						} else {
							maf.addCommand(command);
						}
					}
					command = br.readLine();
				}
				maf.endBlock();
			} catch(Exception e) {
				
			}
		}
	}
}
