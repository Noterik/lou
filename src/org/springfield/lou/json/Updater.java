package org.springfield.lou.json;

import org.json.simple.JSONObject;
import org.springfield.lou.screen.Screen;

public class Updater implements IJSONObserver {
	
	private Screen s;
	private String comp;
	private String command;
	
	public Updater(Screen s, String comp, String command){
		this.s = s;
		this.comp = comp;
		this.command = command;
	}

	@Override
	public void update(JSONObject json) {
		// TODO Auto-generated method stub
		String commandStr = command + "(" + json + ")";
		s.putMsg(comp, "app", commandStr);
	}

}
