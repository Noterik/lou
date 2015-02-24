package org.springfield.lou.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

public class Session implements ISession {
	
	private List<Screen> screens;
	private Html5Application app;
	
	public Session(Screen s, Html5Application app){
		this.screens = new ArrayList<Screen>();
		this.screens.add(s);
		this.app = app;
	}

	@Override
	public List<Screen> getScreens() {
		return this.screens;
	}

	@Override
	public List<Screen> getScreensByRole(String role) {
		// TODO Auto-generated method stub
		List<Screen> filteredList = new ArrayList<Screen>();
		for(Iterator<Screen> i = this.screens.iterator(); i.hasNext();){
			Screen s = i.next();
			if(s.getRole().equals(role)){
				filteredList.add(s);
			}
		}
		return filteredList;
	}

	@Override
	public boolean hasScreen(Screen s) {
		// TODO Auto-generated method stub
		for(Iterator<Screen> i = this.screens.iterator(); i.hasNext();){
			Screen containedScreen = i.next();
			if(containedScreen.getId().equals(s.getId())){
				return true;
			}
		}
		return false;
	}

	@Override
	public Html5Application getApp() {
		// TODO Auto-generated method stub
		return app;
	}
	
}
