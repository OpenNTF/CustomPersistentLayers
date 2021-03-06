package persistence.metadata.processor;

import persistence.annotation.DominoEntity;
import persistence.metadata.MetadataProcessor;
import persistence.metadata.model.EntityMetadata;

public class DominoEntityProcessor implements MetadataProcessor {

	public final void process(Class<?> clazz, EntityMetadata metadata) {
		if (!clazz.isAnnotationPresent(DominoEntity.class))
			return;
		DominoEntity dominoEntityAnno = clazz.getAnnotation(DominoEntity.class);
		metadata.setFormName(dominoEntityAnno.formName());
		metadata.setViewName(dominoEntityAnno.viewName());
		metadata.setDbName(dominoEntityAnno.DBName());
		metadata.setIntercepted(dominoEntityAnno.intercept());

	}

}
