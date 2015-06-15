package org.springfield.lou.json;

import java.util.ArrayList;
import java.util.List;

public interface IJSONObservable {
	public void update();
	public void update(String field);
	public void update(ArrayList<String> field);
	public void addObserver(IJSONObserver observer);
	public List<IJSONObserver> getObservers();
}
