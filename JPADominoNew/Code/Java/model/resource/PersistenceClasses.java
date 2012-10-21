package model.resource;

import java.util.HashMap;
import java.util.Map;

import model.CSS;
import model.Location;
import model.Theme;

public class PersistenceClasses {

	public static Map<String, Class<?>> persistenceClasses;

	public static Map<String, Class<?>> getPersistenceClasses() {
		if (persistenceClasses == null) {
			persistenceClasses = new HashMap<String, Class<?>>();
			persistenceClasses.put("model.Theme", Theme.class);
			persistenceClasses.put("model.CSS", CSS.class);
			persistenceClasses.put("model.Location", Location.class);
			
		}
		return persistenceClasses;
	}

}
