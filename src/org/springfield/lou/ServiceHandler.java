package org.springfield.lou;

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
	
	public String get(String path,String fsxml,String mimetype) {
		return "this is a test from remote "+LazyHomer.myip;
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
	
}
