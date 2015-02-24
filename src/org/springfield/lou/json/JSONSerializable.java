package org.springfield.lou.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public class JSONSerializable implements IJSONSerializable {

	@Override
	public JSONObject toJSON() {
		return toJSON(new ArrayList<String>());
	}

	@Override
	public JSONObject toJSON(String field) {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(field);
		return toJSON(fields);
	}

	@Override
	public JSONObject toJSON(ArrayList<String> fields) {
		JSONObject json = new JSONObject();		
		
		for(Method method : this.getClass().getMethods()){
			if(method.isAnnotationPresent(JSONField.class)){
				Annotation annotation = method.getAnnotation(JSONField.class);
				JSONField field = (JSONField) annotation;
				String fieldName = field.field();
				
				if(!fieldName.equals("") && (fields.size() == 0 || fields.contains(fieldName))){
					try {
						json.put(fieldName, method.invoke(this));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return json;
	}
	
	public String toString(){
		return this.toJSON().toString();
	}
}
