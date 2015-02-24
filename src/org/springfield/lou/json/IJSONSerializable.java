package org.springfield.lou.json;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public interface IJSONSerializable {
	public JSONObject toJSON();
	public JSONObject toJSON(String field);
	public JSONObject toJSON(ArrayList<String> fields);
}
