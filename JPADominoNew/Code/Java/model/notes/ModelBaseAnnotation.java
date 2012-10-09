package model.notes;

import java.util.*;

import persistence.annotation.DocumentReferences;
import persistence.annotation.DominoEntity;
import persistence.annotation.DominoProperty;

import util.JSFUtil;
import util.ReflectionUtils;
import util.Predicate;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dao.DaoBase;

import net.sf.cglib.proxy.Enhancer;

import persistence.annotation.resource.CascadeType;
import persistence.annotation.resource.FetchType;
import persistence.annotation.support.CollectionLazyLoader;
import persistence.annotation.support.ConstructibleAnnotatedCollection;

/**
 * 
 * @author weihang chen
 * 
 */

/**
 * class responsiblity: define how operation being acted on a java object should
 * be populated to its children objects
 */
public class ModelBaseAnnotation implements Serializable {
	private static final long serialVersionUID = -3624643058461028606L;

	protected void initFields() {
		// ONE TO MANY ANNOTATION
		initDocumentRef();
		// not used
		// initDoiminoProperty(clazz);
		// CLASS ENTITY ANNOTATION
		// GETER SETTER ANNOTATION
	}

	// go through all fields find @DocumentReferences
	@SuppressWarnings("unchecked")
	private void initDocumentRef() {
		Class clazz = this.getClass();

		ArrayList<Field> hitFieldList = (ArrayList<Field>) ReflectionUtils
				.eachField(clazz, new Predicate<Field>() {
					public boolean apply(Field input) {
						if (ReflectionUtils.hasAnnotation(input,
								DocumentReferences.class)) {
							populateRefDocuments(
									input,
									(DocumentReferences) input
											.getAnnotation(DocumentReferences.class));

							return true;
						}
						return false;
					}
				});
	}

	/**
	 * collectionField is the field be assigned with @DocumentReferencs
	 * annotation. cglib lazyloader acts on this collection to deliver lazy
	 * fetch function.
	 * 
	 */
	private void populateRefDocuments(Field collectionField,
			DocumentReferences referenceMetaData) {

		Class<?> collectionClass = collectionField.getType();
		Method collectionSetterMethod = ReflectionUtils
				.getFieldSetterMethod(collectionField);
		Method collectionGetterMethod = ReflectionUtils
				.getFieldGetterMethod(collectionField);
		// ex. ArrayList<CSS> list=new ArrayList<CSS>(); CSS is the returnType
		Class<?> returnType = ReflectionUtils
				.resolveReturnType(collectionGetterMethod);
		try {
			Constructor<Collection<Object>> constructor = ReflectionUtils
					.findCtor(collectionField);
			ConstructibleAnnotatedCollection constructibleField = new ConstructibleAnnotatedCollection(
					collectionField, constructor, returnType);
			if (FetchType.EAGER.equals(referenceMetaData.fetch())) {
				DaoBase daoBase = JSFUtil.getDaoBase();
				String foreignKeyParam = referenceMetaData.foreignKey();
				Method fieldGetterMethod = ReflectionUtils.findMethod(this
						.getClass(), "get" + foreignKeyParam);
				Collection collection = constructor.newInstance();
				String foreignKeyValue = (String) fieldGetterMethod.invoke(
						this, new Object[0]);
				String viewName = referenceMetaData.viewName();
				Key key = new Key();
				key.appendEntry(foreignKeyValue);
				// /////////
				// /////////
				// /////////
				Vector list = daoBase.findAllByKey(key, null, viewName,
						returnType);
				for (Object o : list) {
					collection.add(o);
				}
				collectionSetterMethod.invoke(this, collection);
			} else if (FetchType.LAZY.equals(referenceMetaData.fetch())) {
				// CGLIB lazyloader
				Object collection = Enhancer.create(collectionClass,
						new CollectionLazyLoader((ModelBase) this,
								null, constructibleField,null,null));
				collectionSetterMethod.invoke(this, collection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void persistRefDocuments() {
		Class clazz = this.getClass();
		ArrayList<Field> hitFieldList = (ArrayList<Field>) ReflectionUtils
				.eachField(clazz, new Predicate<Field>() {
					public boolean apply(Field input) {
						if (ReflectionUtils.hasAnnotation(input,
								DocumentReferences.class)) {
							persistRefDocuments1(
									input,
									(DocumentReferences) input
											.getAnnotation(DocumentReferences.class));

							return true;
						}
						return false;
					}
				});
	}

	/**
	 *only one cascade approach is implemented at the moment, children objects
	 * will be save/updated together with owner object
	 **/
	private void persistRefDocuments1(Field collectionField,
			DocumentReferences referenceMetaData) {
		// perform interface check - deleted 0717
		// Class<?> collectionClass = collectionField.getType();
		// if (collectionClass.isInterface()
		// && Collection.class.isAssignableFrom(collectionField.getType())) {
		if (Collection.class.isAssignableFrom(collectionField.getType())) {
			for (CascadeType cascadeType1 : referenceMetaData.cascade()) {
				if (CascadeType.SAVE_UPDATE.equals(cascadeType1)) {
					try {
						Method collectionSetterMethod = ReflectionUtils
								.getFieldGetterMethod(collectionField);
						// asert
						Collection collection1 = (Collection) collectionSetterMethod
								.invoke(this, null);
						Iterator iter = collection1.iterator();
						while (iter.hasNext()) {
							ModelBase baseEntity = (ModelBase) iter.next();
							baseEntity.persist();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		else {
			// throw new CollectionNotSupportedException(
			// "COLLECTION IS NOT SUPPORTED, INTERFACE ONLY SET/LIST");

		}
	}

}
