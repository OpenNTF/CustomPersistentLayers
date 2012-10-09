package persistence.annotation.support;

/**
 * @author weihang chen
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Collection;

import java.util.Vector;

import dao.DaoBase;

import persistence.annotation.DocumentReferences;

import model.notes.Key;
import model.notes.ModelBase;
import net.sf.cglib.proxy.LazyLoader;
import util.JSFUtil;
import util.ReflectionUtils;

public class CollectionLazyLoader implements LazyLoader {
	final ModelBase ownerObj;
	final DocumentReferences referenceMetaData;
	final ConstructibleAnnotatedCollection constructibleAnnotatedCollection;

	public CollectionLazyLoader(ModelBase ownerObj,
			DocumentReferences documentReferences,
			ConstructibleAnnotatedCollection constructibleField)
			throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		this.ownerObj = ownerObj;
		this.referenceMetaData = documentReferences;
		this.constructibleAnnotatedCollection = constructibleField;
	}

	// this method is invoked, when underlying objects being accessed, and its
	// loaded once, no need to have boolean initialized to check?
	@SuppressWarnings("unchecked")
	public Object loadObject() throws Exception {
		// TODO Auto-generated method stub
		System.out
				.println("!!!!!!!!!!!!!!!!!!!!!!!!LAZY LOADING!!!!!!!!!!!!!!!!!!!!!!");
		DaoBase daoBase = JSFUtil.getDaoBase();
		String foreignKeyParam = referenceMetaData.foreignKey();
		Method fieldGetterMethod = ReflectionUtils.findMethod(ownerObj
				.getClass(), "get" + foreignKeyParam);
		Collection collection = constructibleAnnotatedCollection
				.getConstructor().newInstance();
		try {
			String foreignKeyValue = (String) fieldGetterMethod.invoke(
					ownerObj, new Object[0]);

			String viewName = referenceMetaData.viewName();
			Key key = new Key();
			key.appendEntry(foreignKeyValue);

			Vector list = daoBase.findAllByKey(key, null, viewName,
					constructibleAnnotatedCollection.getReturnType());
			for (Object o : list) {
				collection.add(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collection;

	}
}
