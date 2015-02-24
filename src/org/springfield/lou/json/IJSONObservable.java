package org.springfield.lou.json;

import java.util.ArrayList;

public interface IJSONObservable {
	public void update();
	public void update(String field);
	public void update(ArrayList<String> field);
	public void addObserver(IJSONObserver observer);
}
