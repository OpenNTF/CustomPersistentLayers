package model.resource;

import java.util.HashMap;
import java.util.Map;

import model.Location;
import model.Tool;
import model.ToolBox;

/**
 * instead of using a persistence.xml, all Pojo classes should be registered
 * here, so that annotated meta-data will be stored at application scope
 * 
 * @author weihang chen
 * 
 */
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
