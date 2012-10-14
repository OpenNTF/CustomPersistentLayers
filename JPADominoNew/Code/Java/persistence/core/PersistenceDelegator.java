package persistence.core;

import persistence.exception.KunderaException;
import persistence.client.Client;
import persistence.client.domino.DominoDBClient;
import persistence.client.domino.DominoEntityReader;

import persistence.graph.Node;
import persistence.graph.NodeLink;
import persistence.graph.NodeLink.LinkProperty;
import persistence.graph.ObjectGraph;
import persistence.graph.ObjectGraphBuilder;
import persistence.lifecycle.states.ManagedState;
import persistence.lifecycle.states.RemovedState;
import persistence.lifecycle.states.TransientState;
import persistence.loader.ClientFactory;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.model.Relation;
import persistence.context.CacheBase;
import persistence.context.FlushManager;
import persistence.context.FlushStack;
import persistence.context.MainCache;
import persistence.context.PersistenceCache;
import persistence.context.PersistenceCacheManager;
import persistence.context.jointable.JoinTableData;
import persistence.context.jointable.JoinTableData.OPERATION;
import persistence.event.EntityEventDispatcher;
import util.JSFUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.FlushModeType;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Query;

import lotus.domino.Database;
import lotus.domino.NotesException;
import model.Theme;
import model.notes.Key;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import exception.notes.CreateException;
import exception.notes.EmptyKeyException;
import exception.notes.ViewNotFoundException;

public class PersistenceDelegator {
	private static final Log log = LogFactory
			.getLog(PersistenceDelegator.class);
	private boolean closed = false;
	private EntityManagerSession session;
	private Map<String, Client> clientMap;
	private EntityEventDispatcher eventDispatcher;
	boolean isRelationViaJoinTable;
	private FlushModeType flushMode = FlushModeType.AUTO;
	private ObjectGraphBuilder graphBuilder;
	private FlushManager flushManager;
	private boolean isTransactionInProgress;
	private PersistenceCache persistenceCache;

	public PersistenceDelegator(EntityManagerSession session,
			PersistenceCache pc) {
		this.session = session;
		this.eventDispatcher = new EntityEventDispatcher();
		this.graphBuilder = new ObjectGraphBuilder();
		this.flushManager = new FlushManager();
		this.persistenceCache = pc;
	}

	public void persist(Object e) {
		EntityMetadata metadata = getMetadata(e.getClass());
		getEventDispatcher().fireEventListeners(metadata, e, PrePersist.class);
		ObjectGraph graph = this.graphBuilder.getObjectGraph(e,
				new TransientState(), getPersistenceCache());
		Node headNode = graph.getHeadNode();
		if (headNode.getParents() == null) {
			headNode.setHeadNode(true);
			getPersistenceCache().getMainCache().addHeadNode(headNode);
		}
		headNode.persist();
		flush();
		graph.getNodeMapping().clear();
		graph = null;
		getEventDispatcher().fireEventListeners(metadata, e, PostPersist.class);
		log.debug("Data persisted successfully for entity : " + e.getClass());
	}

	public <E> E find(Class<E> entityClass, Object primaryKey) {

		boolean isCached = true;
		EntityMetadata entityMetadata = getMetadata(entityClass);
		String nodeId = ObjectGraphBuilder.getNodeId(primaryKey, entityClass);
		MainCache mainCache = (MainCache) getPersistenceCache().getMainCache();

		Node node = mainCache.getNodeFromCache(nodeId);
		System.out.println("!!!!!!!is node found from cache?? " + node);
		// if node can not be found from cache, create a new one, and populate
		// all its children objects as well

		if (node == null) {
			isCached = false;
			node = new Node(nodeId, entityClass, new ManagedState(),
					getPersistenceCache());
			DominoDBClient client = (DominoDBClient) getClient(entityMetadata);
			node.setClient(client);
			node.setPersistenceDelegator(this);
			node.find();
		}

		Object nodeData = node.getData();
		if (nodeData == null)
			return null;

		if (!isCached) {
			ObjectGraph graph = new ObjectGraphBuilder().getObjectGraph(
					nodeData, new ManagedState(), getPersistenceCache());
			// JSFUtil.pushData(graph, node.getNodeId());

			// cache the graph
			System.out.println("------------------------ADD GRAPH TO CACHE STARTS-----------------------");
			getPersistenceCache().getMainCache().addGraphToCache(graph,
					getPersistenceCache());
			System.out.println("------------------------ADD GRAPH TO CACHE ENDS-----------------------");

		}
		return (E) nodeData;
	}

	public <E> List<E> find(Class<E> entityClass, Object[] primaryKeys) {
		List entities = new ArrayList();
		Set pKeys = new HashSet(Arrays.asList(primaryKeys));
		for (Iterator i$ = pKeys.iterator(); i$.hasNext();) {
			Object primaryKey = i$.next();
			entities.add(find(entityClass, primaryKey));
		}
		return entities;
	}

	public void remove(Object e) {
		EntityMetadata metadata = getMetadata(e.getClass());
		getEventDispatcher().fireEventListeners(metadata, e, PreRemove.class);
		ObjectGraph graph = this.graphBuilder.getObjectGraph(e,
				new ManagedState(), getPersistenceCache());
		Node headNode = graph.getHeadNode();
		if (headNode.getParents() == null) {
			headNode.setHeadNode(true);
		}
		headNode.remove();
		flush();
		getEventDispatcher().fireEventListeners(metadata, e, PostRemove.class);
		log.debug("Data removed successfully for entity : " + e.getClass());
	}

	public void flush() {
		// by default flush mode is FlushModeType.AUTO
		System.out.println("what is flush mode: " + getFlushMode());
		if (FlushModeType.COMMIT.equals(getFlushMode())) {
			return;
		}
		if (!(FlushModeType.AUTO.equals(getFlushMode()))) {
			return;
		}
		this.flushManager.buildFlushStack(getPersistenceCache());
		System.out.println("FLUSHSTACK IS BUILT");
		FlushStack fs = getPersistenceCache().getFlushStack();
		System.out.println("what is flushstack: " + fs);
		log
				.debug("Flushing following flush stack to database(s) (showing stack objects from top to bottom):\n"
						+ fs);
		Node node;
		while (!(fs.isEmpty())) {
			node = (Node) fs.pop();
			if ((node.isInState(ManagedState.class))
					|| (node.isInState(RemovedState.class))) {
				// metadata is null, hardcode through it
				// EntityMetadata metadata = getMetadata(node.getDataClass());
				// node.setClient(getClient(metadata));
				node.setClient(getClient(null));
				node.flush();
				Map parents = node.getParents();
				Map children = node.getChildren();
				if ((parents != null) && (!(parents.isEmpty()))) {
					for (Object obj : parents.keySet()) {
						NodeLink parentNodeLink = (NodeLink) obj;
						parentNodeLink.addLinkProperty(
								NodeLink.LinkProperty.LINK_VALUE,
								ObjectGraphBuilder
										.getEntityId(node.getNodeId()));
					}
				}
				if ((children != null) && (!(children.isEmpty()))) {
					for (Object obj : children.keySet()) {
						NodeLink childNodeLink = (NodeLink) obj;
						childNodeLink.addLinkProperty(
								NodeLink.LinkProperty.LINK_VALUE,
								ObjectGraphBuilder
										.getEntityId(node.getNodeId()));
					}
				}
			}
		}

		Map joinTableDataMap = getPersistenceCache().getJoinTableDataMap();
		for (Iterator i$ = joinTableDataMap.values().iterator(); i$.hasNext();) {
			System.out.println("joinTableDataMap");
			JoinTableData jtData = (JoinTableData) i$.next();
			// EntityMetadata m =
			// KunderaMetadataManager.getEntityMetadata(jtData
			// .getEntityClass());
			EntityMetadata m = null;
			Client client = getClient(m);
			// if (JoinTableData.OPERATION.INSERT.equals(jtData.getOperation()))
			// {
			// client.persistJoinTable(jtData);
			// } else if (JoinTableData.OPERATION.DELETE.equals(jtData
			// .getOperation())) {
			// for (i$ = jtData.getJoinTableRecords().keySet().iterator(); i$
			// .hasNext();) {
			// Object pk = i$.next();
			// client.deleteByColumn(jtData.getJoinTableName(), m
			// .getIdColumn().getName(), pk);
			// }
			// }
		}

		JoinTableData jtData;
		EntityMetadata m;
		Client client;
		Iterator i$;
		joinTableDataMap.clear();
	}

	public <E> E merge(E e) {
		log.debug("Merging Entity : " + e);
		EntityMetadata m = getMetadata(e.getClass());
		getEventDispatcher().fireEventListeners(m, e, PreUpdate.class);
		ObjectGraph graph = this.graphBuilder.getObjectGraph(e,
				new ManagedState(), getPersistenceCache());

		Node headNode = graph.getHeadNode();
		if (headNode.getParents() == null) {
			headNode.setHeadNode(true);
		}
		headNode.merge();

		flush();

		getEventDispatcher().fireEventListeners(m, e, PostUpdate.class);
		return (E) headNode.getData();
	}

	public Client getClient(EntityMetadata m) {
		// IndexManager indexManager = null;
		DominoEntityReader entityReader = new DominoEntityReader();
		Database dominoDb = DominoUtils.getCurrentDatabase();
		DominoDBClient dbClient = new DominoDBClient(dominoDb, entityReader,
				"hardcoded info");
		return dbClient;

		/**
		 * choose dbclient based on entityMetadata, origianl code form Kundera
		 */
		/*
		 * Client client = null; String persistenceUnit =
		 * m.getPersistenceUnit(); if ((this.clientMap == null) ||
		 * (this.clientMap.isEmpty())) { this.clientMap = new HashMap(); client
		 * = ClientResolver.discoverClient(persistenceUnit);
		 * this.clientMap.put(persistenceUnit, client); } else if
		 * (this.clientMap.get(persistenceUnit) == null) { client =
		 * ClientResolver.discoverClient(persistenceUnit);
		 * this.clientMap.put(persistenceUnit, client); } else { client =
		 * (Client) this.clientMap.get(persistenceUnit); } return client;
		 */
	}

	private EntityManagerSession getSession() {
		return this.session;
	}

	private EntityEventDispatcher getEventDispatcher() {
		return this.eventDispatcher;
	}

	public <E> List<E> find(Class<E> entityClass,
			Map<String, String> embeddedColumnMap) {
		EntityMetadata entityMetadata = getMetadata(entityClass);

		List entities = new ArrayList();
		entities = getClient(entityMetadata).find(entityClass,
				embeddedColumnMap);
		return entities;
	}

	// public Query createQuery(String jpaQuery) {
	// Query query = new QueryResolver()
	// .getQueryImplementation(jpaQuery, this);
	// return query;
	// }

	public final boolean isOpen() {
		return (!(this.closed));
	}

	public final void close() {
		this.eventDispatcher = null;
		if ((this.clientMap != null) && (!(this.clientMap.isEmpty()))) {
			for (Client client : this.clientMap.values()) {
				client.close();
			}
			this.clientMap.clear();
			this.clientMap = null;
		}
		this.closed = true;
	}

	public final void clear() {
		new PersistenceCacheManager(getPersistenceCache())
				.clearPersistenceCache();
	}

	public EntityMetadata getMetadata(Class<?> clazz) {
		// return KunderaMetadataManager.getEntityMetadata(clazz);
		return null;
	}

	// public String getId(Object entity, EntityMetadata metadata) {
	// try {
	// return PropertyAccessorHelper.getId(entity, metadata);
	// } catch (PropertyAccessException e) {
	// throw new KunderaException(e);
	// }
	// }

	public void store(Object id, Object entity) {
		this.session.store(id, entity);
	}

	public void store(List entities, EntityMetadata entityMetadata) {
		for (Iterator i$ = entities.iterator(); i$.hasNext();) {
			Object o = i$.next();
			// this.session.store(getId(o, entityMetadata), o);
		}
	}

	public EntityReader getReader(Client client) {
		return client.getReader();
	}

	public FlushModeType getFlushMode() {
		return this.flushMode;
	}

	public void setFlushMode(FlushModeType flushMode) {
		this.flushMode = flushMode;
	}

	public boolean isTransactionInProgress() {
		return this.isTransactionInProgress;
	}

	public PersistenceCache getPersistenceCache() {
		return this.persistenceCache;
	}

	public void begin() {
		this.isTransactionInProgress = true;
	}

	public void commit() {
		flush();
		this.isTransactionInProgress = false;
	}

	public void rollback() {
		this.isTransactionInProgress = false;
	}

	public boolean getRollbackOnly() {
		return false;
	}

	public void setRollbackOnly() {
	}

	public boolean isActive() {
		return this.isTransactionInProgress;
	}

}
