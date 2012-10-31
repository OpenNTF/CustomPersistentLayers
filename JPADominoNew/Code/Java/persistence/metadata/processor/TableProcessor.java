package persistence.metadata.processor;

import persistence.annotation.DominoProperty;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.processor.relation.RelationMetadataProcessor;
import persistence.metadata.processor.relation.RelationMetadataProcessorFactory; //import persistence.metadata.validator.EntityValidatorImpl;
//import persistence.property.PropertyAccessorHelper;
import java.lang.reflect.Field;
import java.util.HashMap;
import javax.persistence.PersistenceException;

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
		HashMap<String, Class<?>> fieldTypeMap = new HashMap<String, Class<?>>();
		for (Field field : clazz.getDeclaredFields()) {
			addRelationIntoMetadata(clazz, field, metadata);
			if (field.isAnnotationPresent(DominoProperty.class)) {
				fieldTypeMap.put(field.getName(), field.getType());
			}
		}
		updateView(fieldTypeMap);

	}

	private void updateView(HashMap<String, Class<?>> fieldTypeMap) {

		// DominoImplicitObjectFactory
		// DesignerImplicitObjectFactory
		// Agent myagent = DxlFactory.eINSTANCE
		// .createAgent();
		// org.openntf.model.dxl.Trigger mytrigger =
		// org.openntf.model.dxl.impl.DxlFactoryImpl.eINSTANCE
		// .createTrigger();
		// org.openntf.model.dxl.Schedule myschedule =
		// org.openntf.model.dxl.impl.DxlFactoryImpl.eINSTANCE
		// .createSchedule();
		// myschedule.setType(ScheduleTypes.BYMINUTES);
		// myschedule.setHours(BigInteger.valueOf(0));
		// myschedule.setMinutes(BigInteger.valueOf(5));
		// mytrigger.setType(AgentTriggerTypes.SCHEDULED);
		// myagent.setTrigger(mytrigger);

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
