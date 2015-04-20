package org.springfield.lou.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicSliderUI.ActionScroller;

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
	
	public boolean executeList(Screen s,String name) {
		//System.out.println("EXECUTE LIST="+s+" name");
		if (name.indexOf("log(")!=-1 || name.equals("log")) { // some actionlists are ignored
			return true;
		}
		int pos = name.indexOf("(");
        if (pos!=-1) {
            String content = name.substring(pos+1,name.length()-1);
            name = name.substring(0,pos);
    			ActionList maf = actionlists.get(name);
    			if (maf!=null) {
    				maf.execute(s,content);
    			} else {
    				//System.out.println("can't find trigggered action list "+name);
    				return false;
    			}
        } else {
        		ActionList maf = actionlists.get(name);
        		if (maf!=null) {
        			maf.execute(s);
    			} else {
    				//System.out.println("can't find trigggered action list "+name);
    				return false;
        		}
        }
        return true;
	}
	
	private void readActionLists() {
		String actiondir = app.getHtmlPath()+"/actionlists";
		File dir = new File(actiondir);
		if (!dir.exists()) return; // return if no actionlists dir
		readActionDir(dir,actiondir,"");
	}
	
	private void readActionDir(File dir,String actiondir,String prefix) { // will be called recursive
		String[] files = dir.list();
		for (int i=0;i<files.length;i++) {
			String filename = files[i];
			File dircheck = new File(actiondir+File.separator+prefix+filename);
			if (dircheck.isDirectory()) {
				readActionDir(dircheck,actiondir,prefix+filename+"/");	
			} else {
			ActionList maf = new ActionList(app,prefix+filename.substring(0,filename.indexOf(".")));
			actionlists.put(maf.getName(), maf);
			try {
				BufferedReader br = new BufferedReader(new FileReader(actiondir+File.separator+prefix+filename));
				StringBuffer str = new StringBuffer();
				String command = br.readLine();
				while (command != null) {
					String urlmapping = command.toLowerCase();
					if (urlmapping.indexOf("seturltrigger")==0) {
						urlmapping = urlmapping.substring(urlmapping.indexOf("(")+1);
						urlmapping = urlmapping.substring(0,urlmapping.indexOf(")"));
					//	LouServlet.addUrlTrigger(urlmapping,maf.getName());
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
	
	public static void readActionListsDirForUrlTriggers(String actiondir) {
		File dir = new File(actiondir);
		if (!dir.exists()) return; // return if no actionlists dir
		readActionDirForUrlTriggers(dir,actiondir,"");
	}
	
	private static void readActionDirForUrlTriggers(File dir,String actiondir,String prefix) { // will be called recursive
		String[] files = dir.list();
		for (int i=0;i<files.length;i++) {
			String filename = files[i];
			File dircheck = new File(actiondir+File.separator+prefix+filename);
			if (dircheck.isDirectory()) {
				readActionDirForUrlTriggers(dircheck,actiondir,prefix+filename+"/");	
			} else {
				try {
					BufferedReader br = new BufferedReader(new FileReader(actiondir+File.separator+prefix+filename));
					StringBuffer str = new StringBuffer();
					String command = br.readLine();
					while (command != null) {
						String urlmapping = command.toLowerCase();
						if (urlmapping.indexOf("seturltrigger")==0) {
							urlmapping = urlmapping.substring(urlmapping.indexOf("(")+1);
							urlmapping = urlmapping.substring(0,urlmapping.indexOf(")"));

							String aname = filename.substring(0,filename.length()-4);
							if (!prefix.equals("")) {
								aname = prefix+aname;
							}
							LouServlet.addUrlTrigger(urlmapping,aname);
						}
						command = br.readLine();
					}
				} catch(Exception e) {}
			}
		}
	}

	
}
