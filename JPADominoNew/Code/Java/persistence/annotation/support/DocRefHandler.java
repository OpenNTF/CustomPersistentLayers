package persistence.annotation.support;

/**
 * @author weihang chen
 * not used
 */
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import dao.DaoBase;

import persistence.annotation.DocumentReferences;
import persistence.annotation.resource.FetchType;
import util.JSFUtil;
import util.ReflectionUtils;

import model.notes.Key;

public class DocRefHandler implements InvocationHandler, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8695242238131092304L;
	boolean lazyReferences;
	final Object ownerObj;
	final DocumentReferences referenceMetaData;
	final Field collectionField;
	boolean initialized;
	@SuppressWarnings("unchecked")
	Collection collection;
	final ConstructibleAnnotatedCollection constructibleAnnotatedCollection;

	public DocRefHandler(Object ownerObj, Field collectionField,
			DocumentReferences documentReferences,
			ConstructibleAnnotatedCollection constructibleField)
			throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		this.initialized = false;
		this.ownerObj = ownerObj;
		this.collectionField = collectionField;
		this.referenceMetaData = documentReferences;
		this.constructibleAnnotatedCollection = constructibleField;
		this.collection = constructibleField.getConstructor().newInstance();
		if (FetchType.EAGER.equals(documentReferences.fetch())) {
			init();
		}
	}

	/**
	 * load children collection, set initialized
	 * **/
	@SuppressWarnings("unchecked")
	protected void init() {
		// System.out
		// .println("-------------INITIALIZE REFERENCE DOCUMENTS----------------");
		// DaoBase daoBase = (DaoBase) JSFUtil.getBindingValue("#{DaoBase}");
		// String foreignKeyParam = referenceMetaData.foreignKey();
		// Method method2 = ReflectionUtils.findMethod(ownerObj.getClass(),
		// "get"
		// + foreignKeyParam);
		//
		// try {
		// String foreignKeyValue = (String) method2.invoke(ownerObj, new
		// Object[0]);
		// String viewName = referenceMetaData.viewName();
		// Key key = new Key();
		// key.appendEntry(foreignKeyValue);
		// Vector list = daoBase.findAllByKey(key, null, viewName, CSS.class);
		// for (Object o : list) {
		// collection.add(o);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		this.initialized = true;
	}

	public Object invoke(Object paramObject, Method method, Object[] args)
			throws Throwable {
		// if its lazy fetch , start loading entries now
		if (!initialized)
			init();
		return method.invoke(collection, args);
	}

}
