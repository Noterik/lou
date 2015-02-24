package org.springfield.lou.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

public class SessionContext implements ISessionContext{
	
	private List<ISession> sessions;
	private Html5Application app;
	
	public SessionContext(Html5Application app){
		this.app = app;
		this.sessions = new ArrayList<ISession>();
	}

	@Override
	public ISession getSession(Screen s) {
		// TODO Auto-generated method stub
		for(Iterator<ISession> i = sessions.iterator(); i.hasNext();){
			ISession session = i.next();
			if(session.hasScreen(s)){
				return session;
			}
		}
		return null;
	}

	@Override
	public ISession createSession(Screen s) {
		// TODO Auto-generated method stub
		ISession session = new Session(s, app);
		this.sessions.add(session);
		return session;
	}

	@Override
	public List<ISession> getSessions() {
		// TODO Auto-generated method stub
		return sessions;
	}

	@Override
	public Html5Application getApp() {
		// TODO Auto-generated method stub
		return app;
	}
	
	
	
}
