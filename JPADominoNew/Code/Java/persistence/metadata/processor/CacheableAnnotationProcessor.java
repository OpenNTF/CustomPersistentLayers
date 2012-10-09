package persistence.metadata.processor;

import persistence.metadata.MetadataProcessor;
import persistence.metadata.model.EntityMetadata;
import javax.persistence.Cacheable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * second level cache is not implemented
 * @author weihang chen
 *
 */
public class CacheableAnnotationProcessor implements MetadataProcessor {
	private static Log log = LogFactory
			.getLog(CacheableAnnotationProcessor.class);

	public final void process(Class<?> entityClass, EntityMetadata metadata) {
		Cacheable cacheable = (Cacheable) entityClass
				.getAnnotation(Cacheable.class);

		if (null == cacheable)
			return;
		metadata.setCacheable(cacheable.value());
	}
}

/*
 * Location: C:\Users\SWECWI\Desktop\SECRET
 * WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:
 * com.impetus.kundera.metadata.processor.CacheableAnnotationProcessor Java
 * Class Version: 6 (50.0) JD-Core Version: 0.5.3
 */