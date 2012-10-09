package persistence.utils;

import org.cloner.Cloner;

public class DominoObjectUtils {
	public static final Object deepCopy(Object objectToCopy) {
		Object destObject = Cloner.deepClone(objectToCopy, objectToCopy
				.getClass());
		return destObject;
	}
}
