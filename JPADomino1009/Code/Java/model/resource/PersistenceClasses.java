package model.resource;

import java.util.HashMap;
import java.util.Map;

import model.CSS;
import model.Theme;

public class PersistenceClasses {

	public static Map<String, Class<?>> persistenceClasses;

	public static Map<String, Class<?>> getPersistenceClasses() {
		if (persistenceClasses == null) {
			persistenceClasses = new HashMap<String, Class<?>>();
			persistenceClasses.put("model.Theme", Theme.class);
			persistenceClasses.put("model.CSS", CSS.class);
		}
		return persistenceClasses;
	}

}
