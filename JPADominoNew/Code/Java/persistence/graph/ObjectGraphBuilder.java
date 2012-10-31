package persistence.graph;

import persistence.lifecycle.states.NodeState;
import persistence.metadata.MetadataManager;
import persistence.metadata.model.EntityMetadata;
import persistence.annotation.resource.FetchType;
import persistence.context.PersistenceCache;
import persistence.utils.DeepEquals;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import persistence.metadata.model.Relation;
import util.CloneUtil;
import util.JSFUtil;
import util.ReflectionUtils;

import model.notes.ModelBase;

public class ObjectGraphBuilder {
	PersistenceCache persistenceCache;

	// builds the graph by populating new nodes or fetching them from cache
	public ObjectGraph getObjectGraph(Object entity,
			NodeState initialNodeState, PersistenceCache persistenceCache) {
		ObjectGraph objectGraph = new ObjectGraph();
		this.persistenceCache = persistenceCache;
		System.out.println("----------------------SUB GRAPH BUILD FOR ENTITY "
				+ entity + " STARTS----------------------");
		Node headNode = getNode(entity, objectGraph, initialNodeState);
		
		System.out.println("what is this headNode: "+headNode);
		System.out.println("what is this headNode: "+headNode);
		System.out.println("what is this headNode: "+headNode);
		System.out.println("what is this headNode: "+headNode);

		if (headNode != null) {
			objectGraph.setHeadNode(headNode);
		}

		// PRINT TO CHECK
		Map<String, Node> nodeMap = objectGraph.getNodeMapping();
		Set<Entry<String, Node>> nodeSet = nodeMap.entrySet();
		Iterator<Entry<String, Node>> iter = nodeSet.iterator();
		while (iter.hasNext()) {
			Entry<String, Node> nodeEntry = iter.next();
			Node n = nodeEntry.getValue();
			System.out.print("subgraph node: " + n.getData() + "/");
		}
		// PUSH THIS HEAD NODE TO XPAGES REQUESTMAP, TO VIASUALIZE THE GRAPH
		JSFUtil.pushData(objectGraph, headNode.getNodeId());
		// PRINT TO CHECK END
		System.out.println("----------------------SUB GRAPH BUILD FINISHS "
				+ objectGraph + " IN SIZE OF " + nodeMap.size()
				+ "----------------------");
		return objectGraph;
	}

	// 1. returns a cache node as headNode if it's found in cache
	// 2. assign cached data back to the pojo Fields
	private Node getNode(Object entity, ObjectGraph graph,
			NodeState initialNodeState) {
		// entitymetadata is stored for origainl classes, not enhanced
		// ones always need to perform check to get the real class
		Class realClass = JSFUtil.getRealClass(entity.getClass());
		EntityMetadata entityMetadata = persistence.metadata.MetadataManager
				.getEntityMetadata(realClass);

		if (entityMetadata == null) {
			return null;
		}

		String id = ((ModelBase) entity).getUnid();
		String nodeId = getNodeId(id);
		// graph is local graph with one head node, if there are duplicated
		// nodes, they still share the same relations, therefore no need to
		// build again for duplicated nodes
		// if a node is found in peristenceCache but not in the graph, since its
		// related nodes might not
		// exist in persistence cache, therefore need to build the graph
		Node node = null;
		Node nodeInPersistenceCache = this.persistenceCache.getMainCache()
				.getNodeFromCache(nodeId);
		System.out.println("!?????????????????????????????????????? "+entity);
		System.out.println("!?????????????????????????????????????? "+entity);
		System.out.println("!?????????????????????????????????????? "+entity);
		System.out.println("!?????????????????????????????????????? "+entity);
		
		Object nodeDataCopy = CloneUtil.cloneDominoEntity(entity);
		Node nodeInGraph = graph.getNode(nodeId);

		// if it already exists in the graph return null
		if (nodeInGraph != null && nodeInPersistenceCache == null) {
			return null;
		}

		if (nodeInPersistenceCache == null) {
			node = new Node(nodeId, nodeDataCopy, initialNodeState,
					this.persistenceCache);
		} else {
			node = nodeInPersistenceCache;
			// //////////???????????????????
			// ////////???????????????????
			// ////////???????????????????//////////???????????????????
			// deep equals do not work yet
			if (!(DeepEquals.deepEquals(node.getData(), entity))) {
				node.setData(nodeDataCopy);
				node.setDirty(true);
			} else {
				node.setDirty(false);
			}

		}

		graph.addNode(nodeId, node);

		// recursive place objects into the graph
		System.out.println("relation amounts for entity: "
				+ entityMetadata.getRelations().size());
		for (Relation relation : entityMetadata.getRelations()) {
			// do not invoke any getter if the relation is lazy
			Field relationTargetField = relation.getProperty();
			if (FetchType.LAZY == relation.getFetchType())
				continue;
			Object childObject = ReflectionUtils.getFieldObject(entity,
					relationTargetField);

			// what objectgraphbuilder does is to eliminate duplication and
			// build a unique graph

			System.out.println("PROCESS RELATION " + relation + " STARTS/"
					+ childObject);
			if (childObject != null) {
				if (Collection.class.isAssignableFrom(childObject.getClass())) {
					Collection childrenObjects = (Collection) childObject;
					Iterator i = childrenObjects.iterator();
					while (i.hasNext()) {
						Object childObj = i.next();
						if (childObj != null) {
							addChildNodesToGraph(graph, node, relation,
									childObj, initialNodeState);
						}
					}
				} else {
					addChildNodesToGraph(graph, node, relation, childObject,
							initialNodeState);
				}
			}

			System.out.println("PROCESS RELATION " + relation + " ENDS");
		}

		return node;
	}

	private void addChildNodesToGraph(ObjectGraph graph, Node parentNode,
			Relation relation, Object childObject, NodeState initialNodeState) {
		Node childNode = getNode(childObject, graph, initialNodeState);

		if (childNode == null) {
			return;
		}
		NodeLink nodeLink = new NodeLink(parentNode.getNodeId(), childNode
				.getNodeId());
		nodeLink.setMultiplicity(relation.getType());

		EntityMetadata metadata = MetadataManager.getEntityMetadata(parentNode
				.getDataClass());
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
