package persistence.annotation.support;

import persistence.annotation.DominoEntity;

public class DominoEntityHelper {
	public static <T> boolean isIntercepted(Class<T> clazz) {
		DominoEntity entityClassMetaData = clazz
				.getAnnotation(DominoEntity.class);
		if (entityClassMetaData != null)
			return entityClassMetaData.intercept();
		return false;
	}

	public static <T> String getDBName(Class<T> clazz) {
		DominoEntity entityClassMetaData = clazz
				.getAnnotation(DominoEntity.class);
		if (entityClassMetaData != null)
			return entityClassMetaData.DBName();
		return "";
	}

	public static <T> String getFormName(Class<T> clazz) {
		DominoEntity entityClassMetaData = clazz
				.getAnnotation(DominoEntity.class);
		if (entityClassMetaData != null)
			return entityClassMetaData.formName();
		return "";
	}
}
