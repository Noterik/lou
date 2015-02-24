package org.springfield.lou.session;

import java.util.List;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

public interface ISession {
	public List<Screen> getScreens();
	public List<Screen> getScreensByRole(String role);
	public Html5Application getApp();
	public boolean hasScreen(Screen s);
}
