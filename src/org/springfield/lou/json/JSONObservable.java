package org.springfield.lou.json;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;

public class JSONObservable extends JSONSerializable implements IJSONObservable {
	
	private ArrayList<IJSONObserver> observers;
	
	public JSONObservable(){
		observers = new ArrayList<IJSONObserver>();
	}
	
	
	private void updateObservers(JSONObject object){
		for(Iterator<IJSONObserver> i = observers.iterator(); i.hasNext();){
			try{
				i.next().update(object);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update() {
		updateObservers(this.toJSON());
	}

	@Override
	public void update(String field) {
		updateObservers(this.toJSON(field));
	}

	@Override
	public void update(ArrayList<String> fields) {
		updateObservers(this.toJSON(fields));
	}

	@Override
	public void addObserver(IJSONObserver observer) {
		observers.add(observer);
	}

}
