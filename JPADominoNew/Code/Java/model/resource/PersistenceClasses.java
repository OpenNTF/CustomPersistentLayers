package model.resource;

import java.util.HashMap;
import java.util.Map;

import model.Location;
import model.Tool;
import model.ToolBox;

public class PersistenceClasses {

	public static Map<String, Class<?>> persistenceClasses;

	public static Map<String, Class<?>> getPersistenceClasses() {
		if (persistenceClasses == null) {
			persistenceClasses = new HashMap<String, Class<?>>();
			persistenceClasses.put("model.Location", Location.class);
			persistenceClasses.put("model.ToolBox", ToolBox.class);
			persistenceClasses.put("model.Tool", Tool.class);
		}
		return persistenceClasses;
	}
}
