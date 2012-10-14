package persistence.metadata;

import persistence.metadata.model.ApplicationMetadata;
import persistence.metadata.model.CoreMetadata;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.model.KunderaMetadata;
import persistence.metadata.model.MetamodelImpl; //import persistence.metadata.model.PersistenceUnitMetadata;
import persistence.proxy.EntityEnhancerFactory; //import persistence.proxy.LazyInitializerFactory;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KunderaMetadataManager {
	private static Log log = LogFactory.getLog(KunderaMetadataManager.class);

	// public static PersistenceUnitMetadata getPersistenceUnitMetadata(
	// String persistenceUnit) {
	// return KunderaMetadata.INSTANCE.getApplicationMetadata()
	// .getPersistenceUnitMetadata(persistenceUnit);
	// }

	public static MetamodelImpl getMetamodel(String persistenceUnit) {
		KunderaMetadata kunderaMetadata = KunderaMetadata.getInstance();

		MetamodelImpl metamodel = (MetamodelImpl) kunderaMetadata
				.getApplicationMetadata().getMetamodel(persistenceUnit);

		return metamodel;
	}

	public static MetamodelImpl getMetamodel(String[] persistenceUnits) {
		KunderaMetadata kunderaMetadata = KunderaMetadata.getInstance();

		MetamodelImpl metamodel = null;
		for (String pu : persistenceUnits) {
			metamodel = (MetamodelImpl) kunderaMetadata
					.getApplicationMetadata().getMetamodel(pu);

			if (metamodel != null) {
				return metamodel;
			}

		}

		return metamodel;
	}

	public static EntityMetadata getEntityMetadata(String persistenceUnit,
			Class entityClass) {
		return getMetamodel(persistenceUnit).getEntityMetadata(entityClass);
	}

	public static EntityMetadata getEntityMetadata(Class entityClass) {
		List<String> persistenceUnits = KunderaMetadata.getInstance()
				.getApplicationMetadata().getMappedPersistenceUnit(entityClass);
		if (persistenceUnits != null) {
			for (String pu : persistenceUnits) {
				MetamodelImpl metamodel = getMetamodel(pu);
				EntityMetadata metadata = metamodel
						.getEntityMetadata(entityClass);
				if (metadata != null) {
					return metadata;
				}
			}
		}
		log
				.warn("No Entity metadata found for the class "
						+ entityClass
						+ ". Any CRUD operation on this entity will fail."
						+ "If your entity is for RDBMS, make sure you put fully qualified entity class"
						+ " name under <class></class> tag in persistence.xml for RDBMS "
						+ "persistence unit. Returning null value.");

		return null;
	}

	public static EntityMetadata getEntityMetadata1(Class entityClass) {
		// hard coded string name, need to identical to the one being used in
		// configuration "persistenceXMLString"
		String DOMINOPERSISTENUNIT="DOMINOJPATEST";
		MetamodelImpl metaModel = getMetamodel(DOMINOPERSISTENUNIT);
		System.out.println("metamodel is: " + metaModel);
		EntityMetadata metadata = metaModel.getEntityMetadata(entityClass);
		return metadata;
	}

	// public static LazyInitializerFactory getLazyInitializerFactory() {
	// return KunderaMetadata.INSTANCE.getCoreMetadata()
	// .getLazyInitializerFactory();
	// }

	public static EntityEnhancerFactory getEntityEnhancerFactory() {
		return KunderaMetadata.getInstance().getCoreMetadata()
				.getEnhancedProxyFactory();
	}
}
