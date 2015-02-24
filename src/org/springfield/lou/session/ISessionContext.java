package org.springfield.lou.session;

import java.util.List;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

public interface ISessionContext {
	public ISession getSession(Screen s);
	public List<ISession> getSessions();
	public ISession createSession(Screen s);
	public Html5Application getApp();
}
