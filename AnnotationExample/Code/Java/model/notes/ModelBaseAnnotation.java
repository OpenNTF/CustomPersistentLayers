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
			Constructor<Collection<Object>> constructor = findCtor(collectionField);
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
								referenceMetaData, constructibleField));
				collectionSetterMethod.invoke(this, collection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void initDoiminoProperty(Class clazz) {
		ArrayList<Field> dominoPropertyFieldList = (ArrayList<Field>) ReflectionUtils
				.eachField(clazz, new Predicate<Field>() {
					public boolean apply(Field input) {
						if (ReflectionUtils.hasAnnotation(input,
								DominoProperty.class)) {
							customizeAccessor(input, (DominoProperty) input
									.getAnnotation(DominoProperty.class));
							return true;
						}
						return false;
					}
				});
	}

	@SuppressWarnings("unused")
	private void customizeAccessor(Field propertyField,
			DominoProperty referenceMetaData) {
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
							baseEntity.close();
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

	final static Map<String, Class<? extends Collection>> _collectionFallbacks = new HashMap<String, Class<? extends Collection>>();
	static {
		_collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
		_collectionFallbacks.put(List.class.getName(), ArrayList.class);
		// _collectionFallbacks.put(Set.class.getName(), LinkedHashSet.class);
		_collectionFallbacks.put(Set.class.getName(), HashSet.class);
		_collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
		_collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
		_collectionFallbacks.put("java.util.Deque", LinkedList.class);
		_collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
	}

	private Constructor<Collection<Object>> findCtor(Field field)
			throws SecurityException, NoSuchMethodException {
		Class<?> collectionClass = field.getType();
		Constructor<Collection<Object>> ctor = null;
		if (collectionClass.isInterface()) {
			@SuppressWarnings("rawtypes")
			Class<? extends Collection> fallback = _collectionFallbacks
					.get(collectionClass.getName());
			if (fallback == null) {
				throw new IllegalArgumentException(
						"Can not find a deserializer for non-concrete Collection type "
								+ collectionClass.getName());
			}
			ctor = (Constructor<Collection<Object>>) fallback.getConstructor();
		} else {
			ctor = (Constructor<Collection<Object>>) collectionClass
					.getConstructor();
		}
		return ctor;
	}

	protected String getFormName() {
		DominoEntity entityClassMetaData = this.getClass().getAnnotation(
				DominoEntity.class);
		String formName = entityClassMetaData.formName();
		return formName;
	}

	public boolean isIntercepted() {
		DominoEntity entityClassMetaData = this.getClass().getAnnotation(
				DominoEntity.class);
		boolean isIntercepted = entityClassMetaData.intercept();
		return isIntercepted;
	}

}
