package persistence.configure;

import persistence.metadata.MetadataBuilder;
import persistence.metadata.model.ApplicationMetadata;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.model.KunderaMetadata;
import persistence.metadata.model.MetamodelImpl;
import util.CommonUtil;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.metamodel.Metamodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import model.resource.PersistenceClasses;

class MetamodelConfigurationTemp implements Configuration {
	private final String DOMINOPERSISTENUNIT = "DOMINOJPATEST";
	private static Log log = LogFactory
			.getLog(MetamodelConfigurationTemp.class);

	MetamodelConfigurationTemp() {
	}

	public void configure() {
		log.debug("Loading Entity Metadata...");
		loadEntityMetadata();
	}

	private void loadEntityMetadata() {

		KunderaMetadata kunderaMetadata = KunderaMetadata.getInstance();
		ApplicationMetadata appMetadata = kunderaMetadata
				.getApplicationMetadata();
		// metamodelImpl implements javax.persistence.metamodel,populate three
		// maps in metamodel
		Metamodel metamodel = new MetamodelImpl();
		Map<Class<?>, EntityMetadata> entityMetadataMap = ((MetamodelImpl) metamodel)
				.getEntityMetadataMap();
		Map<String, Class<?>> entityNameToClassMap = PersistenceClasses
				.getPersistenceClasses();
		((MetamodelImpl) metamodel)
				.setEntityNameToClassMap(entityNameToClassMap);
		Map puToClazzMap = new HashMap();
		scanClassAndPutMetadata(entityMetadataMap, entityNameToClassMap,
				puToClazzMap);
		// important, assigne value to applicationmetadata, current version does
		// not handle all the aspects
		((MetamodelImpl) metamodel).setEntityMetadataMap(entityMetadataMap);
		appMetadata.getMetamodelMap().put(DOMINOPERSISTENUNIT, metamodel);
		appMetadata.setClazzToPuMap(puToClazzMap);

	}

	// this method originally go through the xml, for now hard code through it
	private void scanClassAndPutMetadata(
			Map<Class<?>, EntityMetadata> entityMetadataMap,
			Map<String, Class<?>> entityNameToClassMap,
			Map<String, List<String>> clazzToPuMap) {
		Set<Entry<String, Class<?>>> set = entityNameToClassMap.entrySet();
		Iterator<Entry<String, Class<?>>> iter = set.iterator();
		System.out.println("----------CLASS SCANNING STARTS-----------");
		while (iter.hasNext()) {
			Entry<String, Class<?>> entityNameClassEntry = iter.next();
			Class<?> clazz = entityNameClassEntry.getValue();
			EntityMetadata metadata = (EntityMetadata) entityMetadataMap
					.get(clazz);
			if (null == metadata) {
				log.debug("Metadata not found in cache for " + clazz.getName()
						+ " / use metadatbuilder to build metadata");

				MetadataBuilder metadataBuilder = new MetadataBuilder("", "");
				try {
					metadata = metadataBuilder.buildEntityMetadata(clazz);
					metadata.setPersistenceUnit(DOMINOPERSISTENUNIT);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				System.out.println("METHOD SIGNATURE: "
						+ CommonUtil.getMethodName(this.getClass().toString())
						+ " /METHOD DESCRIPTION: entity metadata is added "
						+ metadata);

				if (metadata != null) {
					entityMetadataMap.put(clazz, metadata);
					mapClazztoPu(clazz, DOMINOPERSISTENUNIT, clazzToPuMap);
				}

			}
		}
		System.out.println("----------CLASS SCANNING ENDS-----------");

	}

	private Map<String, List<String>> mapClazztoPu(Class<?> clazz, String pu,
			Map<String, List<String>> clazzToPuMap) {
		List puCol = new ArrayList(1);
		if (clazzToPuMap == null) {
			clazzToPuMap = new HashMap();
		} else if (clazzToPuMap.containsKey(clazz.getName())) {
			puCol = (List) clazzToPuMap.get(clazz.getName());
		}

		if (!(puCol.contains(pu))) {
			puCol.add(pu);
			clazzToPuMap.put(clazz.getName(), puCol);
		}

		return clazzToPuMap;
	}
}
