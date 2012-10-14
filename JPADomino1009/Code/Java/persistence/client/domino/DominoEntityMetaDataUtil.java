package persistence.client.domino;

import persistence.annotation.DominoEntity;
import util.Assert;

public class DominoEntityMetaDataUtil {

	public static <E> String getFormName(Class<E> entityClass) {
		DominoEntity entityClassMetaData = entityClass
				.getAnnotation(DominoEntity.class);
		Assert.notNull(entityClassMetaData,
				"Entity Meta Data could not be found from class "
						+ entityClass.getName());
		String formName = entityClassMetaData.formName();
		return formName;
	}

	public static <E> String getViewName(Class<E> entityClass) {
		DominoEntity entityClassMetaData = entityClass
				.getAnnotation(DominoEntity.class);
		Assert.notNull(entityClassMetaData,
				"Entity Meta Data could not be found from class "
						+ entityClass.getName());
		String ViewName = entityClassMetaData.viewName();
		return ViewName;
	}

}
