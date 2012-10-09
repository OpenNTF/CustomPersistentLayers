package persistence.utils;

import org.cloner.Cloner;

public class ObjectUtils {
	public static final Object deepCopy(Object objectToCopy) {
		Object destObject = Cloner.deepClone(objectToCopy, objectToCopy
				.getClass());
		return destObject;
	}
}
