package persistence.metadata.processor;

import persistence.annotation.DocumentReferences;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.processor.relation.RelationMetadataProcessor;
import persistence.metadata.processor.relation.RelationMetadataProcessorFactory; //import persistence.metadata.validator.EntityValidatorImpl;
//import persistence.property.PropertyAccessorHelper;
import java.lang.reflect.Field;
import javax.persistence.PersistenceException;

import model.Theme;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Weihang Chen this processor populate metadata for relations and query
 */
public class TableProcessor extends AbstractEntityFieldProcessor {
	private static final Log LOG = LogFactory.getLog(TableProcessor.class);

	public TableProcessor() {
		// this.validator = new EntityValidatorImpl();
	}

	public void process(Class clazz, EntityMetadata metadata) {
		LOG.debug("Processing @Entity(" + clazz.getName()
				+ ") for Persistence Object.");
		populateMetadata(metadata, clazz);
	}

	private void populateMetadata(EntityMetadata metadata, Class<?> clazz) {
		for (Field f : clazz.getDeclaredFields()) {
			addRelationIntoMetadata(clazz, f, metadata);
		}

	}

	private void addRelationIntoMetadata(Class<?> entityClass,
			Field relationField, EntityMetadata metadata) {
		RelationMetadataProcessor relProcessor = null;
		try {
			relProcessor = RelationMetadataProcessorFactory
					.getRelationMetadataProcessor(relationField);

			if (relProcessor != null) {
				relProcessor.addRelationIntoMetadata(relationField, metadata);
			}
		} catch (PersistenceException pe) {
			throw new RuntimeException("Error with relationship in @Entity("
					+ entityClass + "." + relationField.getName()
					+ "), reason: " + pe.getMessage());
		}
	}
}
