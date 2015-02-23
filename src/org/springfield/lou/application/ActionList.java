package org.springfield.lou.application;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springfield.fs.FsNode;
import org.springfield.lou.screen.Capabilities;
import org.springfield.lou.screen.Screen;

public class ActionList {
	private String name;
	private ArrayList<String[]> cmds = null;
	private ArrayList<ArrayList<String[]>> blocks = new ArrayList<ArrayList<String[]>>();
	private Html5ApplicationInterface app;
	
	public ActionList(Html5ApplicationInterface a,String n) {
		app = a;
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void execute(Screen s) {
		execute(s,null);
	}
	
	public void startWhenBlock(String when) {
		cmds = new ArrayList<String[]>();
		cmds.add(when.split(" "));
	}
	
	public void endBlock() {
		blocks.add(cmds);
		cmds = null;
	}
	
	public void execute(Screen s,String content) {
		for (int j=0;j<blocks.size();j++) {
			cmds = blocks.get(j);
			Boolean whenvalid = true;
		//	System.out.println("WHEN="+cmds.get(0)[0]+"*");
			if (cmds.get(0)[0].equals(".when")) {
				whenvalid = whenCheck(s,cmds.get(0));
			} 
			if (whenvalid) {
				for (int i=0;i<cmds.size();i++) {
					String[] cmd = cmds.get(i);
					String action = cmd[0].toLowerCase();
					if (action.equals("addreferid"))	{ handleAddReferid(cmd[1],cmd[2]); }
					else if (action.equals("loadstylesheet"))	{ handleLoadStyleSheet(s,cmd[1],cmd[2]); }
					else if (action.equals("setrole"))	{ handleSetRole(s,cmd[1],cmd[2]); }
					else if (action.equals("loadcontent"))	{ handleLoadContent(s,cmd[1],cmd[2]); }
					else if (action.equals("removecontent"))	{ handleRemoveContent(s,cmd[1],cmd[2]); }
					else if (action.equals("setcontent"))	{ handleSetContent(s,cmd[1],cmd[2],cmd[3]); }
					else if (action.equals("setdiv"))	{ handleSetDiv(s,cmd[1],cmd[2],cmd[3]); }
					else if (action.equals("log"))	{ 
							//System.out.println("LOG="+action+" cmdl="+cmd.length);
							String msg = replaceParams(cmd[1],content);
							if (cmd.length>2) {
								handleLog(s,msg,cmd[2]); 
							} else { 
								handleLog(s,msg); 
							}
					} else if (action.equals("callserver"))	{ 
						handleCallServer(s,cmd[1],cmd[2],content);
					} else {
						System.out.println("unknown action command : "+action+" content="+content);
					}
				}
			}
		}
	}
	
	private Boolean whenCheck(Screen s,String[] when) {
		Capabilities cap = s.getCapabilities();
		for (int i=1;i<when.length;i++) {
			String check = when[i];
			String[] parts = check.split("=");
			String cmd = parts[0];
			String value = parts[1];
			if (cmd.equals("device")) {
				//System.out.println("DEVICE="+value);
				if (value.equals("desktop") && cap.getDeviceMode()==cap.MODE_GENERIC) return true;
				if (value.equals("ipad") && cap.getDeviceMode()==cap.MODE_IPAD_LANDSCAPE) {
					System.out.println("IPAD");
					return true;
				}
				if (value.equals("ipad") && cap.getDeviceMode()==cap.MODE_IPAD_PORTRAIT) {
					//System.out.println("IPAD");
					return true;
				}
				if (value.equals("atablet") && cap.getDeviceMode()==cap.MODE_ATABLET_LANDSCAPE) return true;
				if (value.equals("atablet") && cap.getDeviceMode()==cap.MODE_ATABLET_PORTRAIT) return true;
				if (value.equals("iphone") && cap.getDeviceMode()==cap.MODE_IPHONE_LANDSCAPE) return true;
				if (value.equals("iphone") && cap.getDeviceMode()==cap.MODE_IPHONE_PORTRAIT) return true;
				if (value.equals("aphone") && cap.getDeviceMode()==cap.MODE_APHONE_LANDSCAPE) return true;
				if (value.equals("aphone") && cap.getDeviceMode()==cap.MODE_APHONE_PORTRAIT) return true;
			}
		}
		return false;
	}
		
	private void handleCallServer(Screen s,String methodname,String scope,String content) {
		// example : callserver,open,screen,content
		try {
			Method method;
			if (content!=null && !content.equals("")) {
				method = app.getClass().getMethod(methodname,Screen.class,String.class);
				if (method!=null) {
					//System.out.println("METHOD FOUND="+method);
					method.invoke(app,s,content);
				} else {
					System.out.println("MISSING!!! METHOD ="+method);
				}
			} else {
				method = app.getClass().getMethod(methodname,Screen.class);
				if (method!=null) {
					//System.out.println("METHOD FOUND="+method);
					method.invoke(app,s);	
				} else {
					System.out.println("MISSING!!! METHOD ="+method);
				}
			}
		} catch(Exception e) {
			System.out.println("Action ServerCall error");
			e.printStackTrace();
		}
	}
	
	private void handleAddReferid(String name,String referid) {
		// example : addreferid,elementone,websiteserviceone/defaultoutput
		app.addReferid(name, referid);
	}
	
	private void handleLoadStyleSheet(Screen s,String scope,String name) {
		// example : loadstylesheet,screen,generic
		if (name.equals("generic")) {
			app.loadStyleSheet(s,app.getAppname());
		} else {
			app.loadStyleSheet(s,name);
		}
	}

	private void handleSetRole(Screen s,String scope,String role) {
		// example : setRole(screen,"mainscreen")
		if (scope.equals("screen")) {
			s.setRole(role.substring(1, role.length()-1));
		}
	}
	
	private void handleLoadContent(Screen s,String scope,String name) {
		// example : loadcontent,screen,titlepart
		if (scope.equals("screen")) {
			app.loadContent(s,name);
		}
	}

	private void handleRemoveContent(Screen s,String scope,String name) {
		// example : removecontent,screen,titlepart
		if (scope.equals("screen")) {
			s.removeContent(name);
		}
	}
	

	
	private void handleSetContent(Screen s,String scope,String name,String body) {
		// example : setcontent,defaultoutput,"Website app main"
		if (scope.equals("screen")) {
			s.setContent(name,body.substring(1, body.length()-1));
		}
	}
	
	private void handleSetDiv(Screen s,String scope,String name,String body) {
		if (scope.equals("screen")) {
			s.setDiv(name,body.substring(1, body.length()-1));
		}
	}
	
    private void handleLog(Screen s,String msg) {
    		handleLog(s,msg,"info");
    }
	
    private void handleLog(Screen s,String msg,String level) {
		FsNode n = new FsNode("log");
		
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		n.setId(f.format(new Date()));
		
		n.setProperty("level", level);
		n.setProperty("source", "maf");
		n.setProperty("msg", msg);
		if (s!=null) {
			n.setProperty("screen", s.getShortId());
			if (s.getUserName()!=null) {
				n.setProperty("user", s.getUserName());
			} else {
				n.setProperty("user","unknown");
			}
		} else {
			n.setProperty("screen", "application");
			n.setProperty("user", "unknown");
		}
		ApplicationManager.log(app,n);
    }
    
    private String replaceParams(String result,String content) {
    	System.out.println("result="+result+" params="+content);
    	
    	// if there is no $ in the command why decode the params?
    	int pos = result.indexOf("$");
    	if (pos!=-1) {
			String[] p = content.split(",");
			Map<String, String> params = new HashMap<String, String>();
			for (int i=0;i<p.length;i++) {
				String par  = p[i];
				int pos2 = par.indexOf("=");
				if (pos2!=-1) {
					String name = par.substring(0,pos2);
					String value = par.substring(pos2+1);
					System.out.println("NAME="+name+" VALUE="+value);
					params.put(name, value);
				}
			}
			// params are decoded lets see if we can replace things in the input
			for(Iterator<String> iter = params.keySet().iterator(); iter.hasNext(); ) {
				String pkey = (String)iter.next();
				String pvalue = params.get(pkey);
				result = result.replace("$"+pkey, pvalue);
			}
			System.out.println("result2="+result);
			
    	}
    	return result;
    }
	
	
	
	public void addCommand(String c) {
		if (cmds==null) cmds = new ArrayList<String[]>(); 
		// lets remove comments and preparse it
		try {
			String nc = c.substring(0,c.lastIndexOf(")"));
			//System.out.println("ADDED COMMAND1 "+nc+" to list "+name);
			nc = nc.replace("(", ","); // why doesn't first replace work ?
			//System.out.println("ADDED COMMAND2 "+nc+" to list "+name);
			cmds.add(nc.split(","));
		} catch(Exception e) {
			System.out.println("Action command error "+c+" in "+name);
		}
	}
}
