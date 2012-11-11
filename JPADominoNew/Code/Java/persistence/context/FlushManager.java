package persistence.context;

import persistence.graph.Node;
import persistence.graph.NodeLink;
import persistence.graph.NodeLink.LinkProperty;
import persistence.graph.ObjectGraphBuilder;
import persistence.lifecycle.states.ManagedState;
import persistence.lifecycle.states.RemovedState;
import persistence.metadata.model.Relation;
import persistence.metadata.model.Relation.ForeignKey;
import persistence.context.jointable.JoinTableData;
import persistence.context.jointable.JoinTableData.OPERATION;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import model.Location;

public class FlushManager {

	/**
	 * 1. mark all the nodes in the persistence cache as not traversed - new
	 * PersistenceCacheManager(pc).markAllNodesNotTraversed();
	 * <p>
	 * 2. go through all the head nodes and invoke addNodesToFlushStack(pc,
	 * headNode) on each of them
	 * 
	 * @param pc
	 */
	public void buildFlushStack(PersistenceCache pc) {
		MainCache mainCache = (MainCache) pc.getMainCache();
		System.out.println("EEEEEEEMAIN CACHE: "+mainCache+"/size: "+mainCache.size());
		new PersistenceCacheManager(pc).markAllNodesNotTraversed();
		Set headNodes = mainCache.getHeadNodes();
		for (Object obj : headNodes) {
			System.out.println("is there any headnode?? "+obj);
			Node headNode = (Node) obj;
			System.out.println("head node is not dirty therefore stack size is 0?? "+headNode.isDirty());
			System.out.println("head node is not dirty therefore stack size is 0?? "+headNode.isDirty());System.out.println("head node is not dirty therefore stack size is 0?? "+headNode.isDirty());
			if (headNode != null && headNode.isDirty())
				addNodesToFlushStack(pc, headNode);
		}
	}

	/**
	 * recursivly add nodes to stock
	 * 
	 * @param pc
	 * @param node
	 */
	public void addNodesToFlushStack(PersistenceCache pc, Node node) {

		FlushStack flushStack = pc.getFlushStack();
		MainCache mainCache = (MainCache) pc.getMainCache();
		if (node == null)
			return;
		Map children = node.getChildren();

		if (children != null) {
			Map oneToOneChildren = new HashMap();
			Map oneToManyChildren = new HashMap();
			Map manyToOneChildren = new HashMap();
			Map manyToManyChildren = new HashMap();

			for (Object obj : children.keySet()) {
				NodeLink nodeLink = (NodeLink) obj;
				Relation.ForeignKey multiplicity = nodeLink.getMultiplicity();
				switch (multiplicity.ordinal()) {
				case 0:
					oneToOneChildren.put(nodeLink, children.get(nodeLink));
					break;
				case 1:
					oneToManyChildren.put(nodeLink, children.get(nodeLink));
					break;
				case 2:
					manyToOneChildren.put(nodeLink, children.get(nodeLink));
					break;
				case 3:
					manyToManyChildren.put(nodeLink, children.get(nodeLink));
				}

			}

			for (Object obj : oneToManyChildren.keySet()) {
				NodeLink nodeLink = (NodeLink) obj;
				Node childNode = mainCache.getNodeFromCache(nodeLink
						.getTargetNodeId());
				if (!(childNode.isTraversed())) {
					addNodesToFlushStack(pc, childNode);
				}
			}

			for (Object obj : manyToManyChildren.keySet()) {
				NodeLink nodeLink = (NodeLink) obj;
				Node childNode = mainCache.getNodeFromCache(nodeLink
						.getTargetNodeId());

				if (childNode != null) {
					if ((node.isDirty()) && (!(node.isTraversed()))) {
						// JoinTableMetadata jtmd = (JoinTableMetadata) nodeLink
						// .getLinkProperty(NodeLink.LinkProperty.JOIN_TABLE_METADATA);
						//
						// if (jtmd != null) {
						// String joinColumnName = (String) jtmd
						// .getJoinColumns().toArray()[0];
						// String inverseJoinColumnName = (String) jtmd
						// .getInverseJoinColumns().toArray()[0];
						// Object entityId = ObjectGraphBuilder
						// .getEntityId(node.getNodeId());
						// Object childId = ObjectGraphBuilder
						// .getEntityId(childNode.getNodeId());
						//
						// Set childValues = new HashSet();
						// childValues.add(childId);
						//
						// JoinTableData.OPERATION operation = null;
						// if (node.getCurrentNodeState().getClass().equals(
						// ManagedState.class)) {
						// operation = JoinTableData.OPERATION.INSERT;
						// } else if (node.getCurrentNodeState().getClass()
						// .equals(RemovedState.class)) {
						// operation = JoinTableData.OPERATION.DELETE;
						// }
						//
						// pc.addJoinTableDataIntoMap(operation, jtmd
						// .getJoinTableName(), joinColumnName,
						// inverseJoinColumnName, node.getDataClass(),
						// entityId, childValues);
						// }

					}

					if (!(childNode.isTraversed())) {
						addNodesToFlushStack(pc, childNode);
					}

				}

			}

			for (Object obj : oneToOneChildren.keySet()) {
				NodeLink nodeLink = (NodeLink) obj;
				if (!(node.isTraversed())) {
					node.setTraversed(true);
					flushStack.push(node);

					Node childNode = mainCache.getNodeFromCache(nodeLink
							.getTargetNodeId());
					addNodesToFlushStack(pc, childNode);
				}

			}

			for (Object obj : manyToOneChildren.keySet()) {
				NodeLink nodeLink = (NodeLink) obj;
				if (!(node.isTraversed())) {
					node.setTraversed(true);
					flushStack.push(node);
				}
				Node childNode = mainCache.getNodeFromCache(nodeLink
						.getTargetNodeId());

				Map parents = childNode.getParents();
				for (Object obj1 : parents.keySet()) {
					NodeLink parentLink = (NodeLink) obj1;
					Relation.ForeignKey multiplicity = parentLink
							.getMultiplicity();
					if (multiplicity.equals(Relation.ForeignKey.MANY_TO_ONE)) {
						Node parentNode = (Node) parents.get(parentLink);

						if ((!(parentNode.isTraversed()))
								&& (parentNode.isDirty())) {
							addNodesToFlushStack(pc, parentNode);
						}
					}

				}

				if ((!(childNode.isTraversed())) && (childNode.isDirty())) {
					addNodesToFlushStack(pc, childNode);
				} else if (!(childNode.isDirty())) {
					childNode.setTraversed(true);
					flushStack.push(childNode);
				}

			}
		}
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!! node: "+node.getData().toString());
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!! istraversed()" +
		// node.isTraversed());
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!! isdirty()" +
		// node.isDirty());
		if ((node.isTraversed()) || (!(node.isDirty())))
			return;
		node.setTraversed(true);
		flushStack.push(node);
	}

	public void clearFlushStack(PersistenceCache pc) {
		FlushStack flushStack = pc.getFlushStack();
		if ((flushStack == null) || (flushStack.isEmpty()))
			return;
		flushStack.clear();
	}
}
