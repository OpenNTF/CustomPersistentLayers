package persistence.graph;

import persistence.lifecycle.states.NodeState;
import persistence.metadata.KunderaMetadataManager;
import persistence.metadata.model.EntityMetadata;
import persistence.annotation.DocumentReferences;
import persistence.context.CacheBase;
import persistence.context.PersistenceCache;
import persistence.utils.DeepEquals;
import persistence.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.cglib.proxy.Enhancer;

import persistence.metadata.model.Relation;
import util.AnnotationUtil;
import util.JSFUtil;
import util.ReflectionUtils;

import model.notes.ModelBase;
import model.notes.ModelBaseAnnotation;

public class ObjectGraphBuilder {
	PersistenceCache persistenceCache;

	public ObjectGraph getObjectGraph(Object entity,
			NodeState initialNodeState, PersistenceCache persistenceCache) {
		ObjectGraph objectGraph = new ObjectGraph();
		this.persistenceCache = persistenceCache;
		// getNode method also builds the graph at the same time,if there is duplicated 
		Node headNode = getNode(entity, objectGraph, initialNodeState, null);
		System.out.println("!!!!!!!HEAD NODE IS SUPPOSED TO BE CACHED NODE if its found in cache"+headNode.getData());
		if (headNode != null) {
			objectGraph.setHeadNode(headNode);
		}

		Map<String, Node> nodeMap = objectGraph.getNodeMapping();
		System.out.println("CHECKING THE RESULT GRAPH SIZE" + nodeMap.size());
		Set<Entry<String, Node>> nodeSet = nodeMap.entrySet();
		Iterator<Entry<String, Node>> iter = nodeSet.iterator();
		while (iter.hasNext()) {
			Entry<String, Node> nodeEntry = iter.next();
			Node n = nodeEntry.getValue();
			System.out.println("CHECKING THE RESULT INDIVIDUAL NODE "
					+ n.getData());
		}
		return objectGraph;
	}

	private Node getNode(Object entity, ObjectGraph graph,
			NodeState initialNodeState, ArrayList replaceCollection) {
		// entitymetadata is stored for origainl classes, not enhanced
		// ones always need to perform check to get the real class
		Class realClass = JSFUtil.getRealClass(entity.getClass());
		EntityMetadata entityMetadata = persistence.metadata.KunderaMetadataManager
				.getEntityMetadata(realClass);
		if (entityMetadata == null) {
			return null;
		}

		String id = ((ModelBase) entity).getUnid();
		String nodeId = getNodeId(id);
		// graph is local graph with one head node, if there are duplicated
		// nodes, they still share the same relations, therefore no need to
		// build again for duplicated nodes
		if (graph.getNode(nodeId) == null) {
			if (replaceCollection instanceof Collection)
				replaceCollection.add(entity);
		}
		if (graph.getNode(nodeId) != null) {
			// IMPORTANT 1007 assign existing node data in graph to the compared
			// entity
			System.out
					.println("IMPORTANT ASSIGN EXISTING NODE FROM GRAPH TO CURRENT VISTING NODE");
			System.out.println("what is entity: " + entity);
			System.out.println("what is existing node data: "
					+ graph.getNode(nodeId).getData());
			if (replaceCollection instanceof Collection)
				replaceCollection.add(graph.getNode(nodeId).getData());
			return null;
		}
		// if a node is found in peristenceCache but not in the graph, since its
		// related nodes might not
		// exist in persistence cache, therefore need to build the graph
		Node node = null;
		Node nodeInPersistenceCache = this.persistenceCache.getMainCache()
				.getNodeFromCache(nodeId);
		if (nodeInPersistenceCache == null) {
			node = new Node(nodeId, entity, initialNodeState,
					this.persistenceCache);
		} else {
			node = nodeInPersistenceCache;
			// IMPORTANT 1007 assign existing node data in graph to the compared
			// entity
			System.out
					.println("IMPORTANT ASSIGN EXISTING NODE FROM CACHE TO CURRENT VISTING NODE");
			System.out.println("Input entity: " + entity);
			System.out.println("Cached node data: "
					+ nodeInPersistenceCache.getData());
			entity = nodeInPersistenceCache.getData();
		}
		graph.addNode(nodeId, node);
		// recursive place objects into the graph
		for (Relation relation : entityMetadata.getRelations()) {

			Field targetField = relation.getProperty();

			// if its lazy fetch, do not initialize or put into graph
			boolean isEager = AnnotationUtil.isEager(targetField);
			 if (!isEager)
				continue;
			Field relationTargetField = relation.getProperty();
			System.out.println("SHOULD BE DIFFERENT: " + relationTargetField);
			Object childObject = ReflectionUtils.getFieldObject(entity,
					relationTargetField);

			// what objectgraphbuilder does is to eliminate duplication and
			// build a unique graph

			System.out.println("PROCESS RELATION " + relation + " STARTS/"
					+ childObject);
			ArrayList replaceList = new ArrayList();
			if (childObject != null) {
				if (Collection.class.isAssignableFrom(childObject.getClass())) {
					Collection childrenObjects = (Collection) childObject;
					Iterator i = childrenObjects.iterator();
					while (i.hasNext()) {
						Object childObj = i.next();
						if (childObj != null) {
							addChildNodesToGraph(graph, node, relation,
									childObj, initialNodeState, replaceList);
						}
					}
				} else {
					addChildNodesToGraph(graph, node, relation, childObject,
							initialNodeState, null);
				}
			}
			// 1007 replace current collection with the updated
			// collection, so all children collection will share the
			// same entities from the graph
			System.out.println("REPLACE LIST: " + replaceList);
			System.out.println("entity: " + entity + "/assigned field: "
					+ relationTargetField + "/assigned value: " + replaceList);
			if (replaceList instanceof Collection) {
				ReflectionUtils.setFieldObject(entity, relationTargetField,
						replaceList);
				Collection tmp1 = (Collection) ReflectionUtils.getFieldObject(
						entity, relationTargetField);
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!");

				System.out.println("EEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!"+tmp1);
			}

			System.out.println("PROCESS RELATION " + relation + " ENDS");
		}

		return node;
	}

	private void addChildNodesToGraph(ObjectGraph graph, Node parentNode,
			Relation relation, Object childObject, NodeState initialNodeState,
			ArrayList replaceCollection) {
		Node childNode = getNode(childObject, graph, initialNodeState,
				replaceCollection);

		if (childNode == null) {
			return;
		}
		NodeLink nodeLink = new NodeLink(parentNode.getNodeId(), childNode
				.getNodeId());
		nodeLink.setMultiplicity(relation.getType());

		EntityMetadata metadata = KunderaMetadataManager
				.getEntityMetadata(parentNode.getDataClass());
		nodeLink.setLinkProperties(getLinkProperties(metadata, relation));

		childNode.addParentNode(nodeLink, parentNode);

		parentNode.addChildNode(nodeLink, childNode);

	}

	private Map<NodeLink.LinkProperty, Object> getLinkProperties(
			EntityMetadata metadata, Relation relation) {
		Map linkProperties = new HashMap();

		linkProperties.put(NodeLink.LinkProperty.LINK_NAME, getMappedName(
				metadata, relation));
		linkProperties.put(NodeLink.LinkProperty.IS_SHARED_BY_PRIMARY_KEY,
				Boolean.valueOf(relation.isJoinedByPrimaryKey()));
		linkProperties.put(NodeLink.LinkProperty.IS_BIDIRECTIONAL, Boolean
				.valueOf(!(relation.isUnary())));
		linkProperties.put(NodeLink.LinkProperty.IS_RELATED_VIA_JOIN_TABLE,
				Boolean.valueOf(relation.isRelatedViaJoinTable()));
		linkProperties.put(NodeLink.LinkProperty.PROPERTY, relation
				.getProperty());

		linkProperties.put(NodeLink.LinkProperty.CASCADE, relation
				.getCascades());

		// if (relation.isRelatedViaJoinTable())
		// {
		// linkProperties.put(NodeLink.LinkProperty.JOIN_TABLE_METADATA,
		// relation.getJoinTableMetadata());
		// }

		return linkProperties;
	}

	public String getMappedName(EntityMetadata parentMetadata, Relation relation) {
		if (relation != null) {
			String joinColumn = relation.getJoinColumnName();
			// if (joinColumn == null)
			// {
			// Class clazz = relation.getTargetEntity();
			// EntityMetadata metadata =
			// KunderaMetadataManager.getEntityMetadata(clazz);
			// joinColumn =
			// (relation.getType().equals(Relation.ForeignKey.ONE_TO_MANY)) ?
			// parentMetadata.getIdColumn().getName() :
			// metadata.getIdColumn().getName();
			// }

			return joinColumn;
		}
		return null;
	}

	public static String getNodeId(String entityId) {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("$");
		strBuffer.append(entityId);
		return strBuffer.toString();
	}

	public static String getNodeId(Object pk, Class<?> objectClass) {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("$");
		strBuffer.append(pk);
		return strBuffer.toString();
	}

	public static String getEntityId(String nodeId) {
		return nodeId.substring(nodeId.indexOf("$") + 1, nodeId.length());
	}
}
