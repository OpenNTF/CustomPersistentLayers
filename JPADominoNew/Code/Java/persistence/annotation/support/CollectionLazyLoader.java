package persistence.annotation.support;

/**
 * @author weihang chen
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Vector;

import dao.DaoBase;

import persistence.annotation.DocumentReferences;
import persistence.core.PersistenceDelegator;
import persistence.graph.ObjectGraph;
import persistence.graph.ObjectGraphBuilder;
import persistence.lifecycle.states.ManagedState;
import persistence.metadata.KunderaMetadataManager;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.model.Relation;

import model.notes.Key;
import model.notes.ModelBase;
import net.sf.cglib.proxy.LazyLoader;
import util.CommonUtil;
import util.JSFUtil;
import util.ReflectionUtils;

public class CollectionLazyLoader implements LazyLoader {
	final ModelBase ownerObj;
	final Relation relation;
	final ConstructibleAnnotatedCollection constructibleAnnotatedCollection;
	final PersistenceDelegator persistenceDelegator;
	final EntityMetadata entityMetadata;

	public CollectionLazyLoader(ModelBase ownerObj, Relation relation,
			ConstructibleAnnotatedCollection constructibleField,
			PersistenceDelegator persistenceDelegator,
			EntityMetadata entityMetadata) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		this.ownerObj = ownerObj;
		this.relation = relation;
		this.constructibleAnnotatedCollection = constructibleField;
		this.persistenceDelegator = persistenceDelegator;
		this.entityMetadata = entityMetadata;
	}

	// this method is invoked, when underlying objects being accessed, and its
	// loaded once, no need to have boolean initialized to check?
	@SuppressWarnings("unchecked")
	public Object loadObject() throws Exception {
		// TODO Auto-generated method stub
		System.out
				.println("!!!!!!!!!!!!!!!!!!!!!!!!LAZY LOADING STARTS!!!!!!!!!!!!!!!!!!!!!!");
		if (ownerObj == null || relation == null
				|| constructibleAnnotatedCollection == null
				|| persistenceDelegator == null)
			return null;

		persistenceDelegator.getClient(entityMetadata);
		String foreignKey = relation.getDominoForeignKey();
		String viewName = relation.getDominoView();
		Method foreignKeyGetter = ReflectionUtils.findMethod(ownerObj
				.getClass(), "get"
				+ CommonUtil.firstCharToUpperCase(foreignKey));
		Collection collection = constructibleAnnotatedCollection
				.getConstructor().newInstance();
		Object foreignKeyFieldValue = ReflectionUtils.invokeGetterMethod(
				ownerObj, foreignKeyGetter);

		if (foreignKeyFieldValue == null)
			return null;
		Key key = new Key();
		key.appendEntry(foreignKeyFieldValue.toString());
		// childs = childClient.findAll(childClass, key);

		DaoBase daoBase = JSFUtil.getDaoBase();

		try {
			Vector list = daoBase.findAllByKey(key, null, viewName,
					constructibleAnnotatedCollection.getReturnType());
			for (Object o : list) {
				collection.add(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList replaceCollection = new ArrayList();
		for (Object nodeData : collection) {
			ObjectGraph graph = new ObjectGraphBuilder().getObjectGraph(
					nodeData, new ManagedState(), persistenceDelegator
							.getPersistenceCache());
			// the head node being returned is always the one from cache if its
			// found in cache
			replaceCollection.add(graph.getHeadNode().getData());
			// merge the graph
			persistenceDelegator.getPersistenceCache().getMainCache()
					.addGraphToCache(graph,
							persistenceDelegator.getPersistenceCache());
		}
		System.out
				.println("!!!!!!!!!!!!!!!!!!!!!!!!LAZY LOADING ENDS!!!!!!!!!!!!!!!!!!!!!!compare the collection before and after");
		System.out.println("before======>");

		for (Object o : collection)
			System.out.println(o);
		System.out.println("after======>");

		for (Object o : replaceCollection)
			System.out.println(o);
		// return collection;
		return replaceCollection;
	}
}
