package org.springfield.lou;

import org.springfield.lou.application.ApplicationManager;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

public class ServiceHandler implements ServiceInterface{
	
	private static ServiceHandler instance;
	
	public static ServiceHandler instance() {
		if (instance==null) {
			instance = new ServiceHandler();
			ServiceManager.setService(instance);
		}
		return instance;
	}
	
	public String getName() {
		return "lou";
	}
	
	public String get(String uri,String fsxml,String mimetype) {
		System.out.println("SH="+uri);
		int pos = uri.indexOf("(");
		if (pos!=-1) {
			String command = uri.substring(0,pos);
			String values = uri.substring(pos+1);
			values = values.substring(0,values.length());
			String[] params = values.split(",");
			return handleGetCommand(command,params);
		}
		return null;
	}
	
	public String put(String path,String value,String mimetype) {
		return null;
	}
	
	public String post(String path,String fsxml,String mimetype) {
		return null;
	}
	
	public String delete(String path,String value,String mimetype) {
		return null;
	}
	
	private String handleGetCommand(String command,String[] params) {
		System.out.println("HG="+command);
		if (command.equals("getAppWAR")) return getAppWar(params[0],params[1]);
		return null;
	}
	
	private String getAppWar(String appname,String version) {
		String result = ApplicationManager.getApplicationWarAsString(appname,version);
		if (result!=null) {
			System.out.println("RETURNING STRING SIZE="+result.length());
			return result;
		} else { 
			System.out.println("CAN'T find APP");
			return null;
		}
	}
}
