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
import java.util.Map;
import java.util.Set;

public class FlushManager {
	public void buildFlushStack(PersistenceCache pc) {
		MainCache mainCache = (MainCache) pc.getMainCache();
		new PersistenceCacheManager(pc).markAllNodesNotTraversed();
		Set headNodes = mainCache.getHeadNodes();
		for (Object obj : headNodes) {
			Node headNode = (Node) obj;
			System.out.println("add all headnodes from cache to flushstack: "+headNode);
			System.out.println("headnode has to be dirty to be flushed?? : "+headNode.isDirty());
			//hard code , set dirty true
			headNode.setDirty(true);
			if (headNode!=null&&headNode.isDirty())
				addNodesToFlushStack(pc, headNode);
		}
	}

	public void addNodesToFlushStack(PersistenceCache pc, Node node) {
		
		FlushStack flushStack = pc.getFlushStack();
		MainCache mainCache = (MainCache) pc.getMainCache();
		if (node==null)
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
				case 1:

					oneToOneChildren.put(nodeLink, children.get(nodeLink));
					break;
				case 2:

					oneToManyChildren.put(nodeLink, children.get(nodeLink));
					break;
				case 3:

					manyToOneChildren.put(nodeLink, children.get(nodeLink));
					break;
				case 4:

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
//						JoinTableMetadata jtmd = (JoinTableMetadata) nodeLink
//								.getLinkProperty(NodeLink.LinkProperty.JOIN_TABLE_METADATA);
//
//						if (jtmd != null) {
//							String joinColumnName = (String) jtmd
//									.getJoinColumns().toArray()[0];
//							String inverseJoinColumnName = (String) jtmd
//									.getInverseJoinColumns().toArray()[0];
//							Object entityId = ObjectGraphBuilder
//									.getEntityId(node.getNodeId());
//							Object childId = ObjectGraphBuilder
//									.getEntityId(childNode.getNodeId());
//
//							Set childValues = new HashSet();
//							childValues.add(childId);
//
//							JoinTableData.OPERATION operation = null;
//							if (node.getCurrentNodeState().getClass().equals(
//									ManagedState.class)) {
//								operation = JoinTableData.OPERATION.INSERT;
//							} else if (node.getCurrentNodeState().getClass()
//									.equals(RemovedState.class)) {
//								operation = JoinTableData.OPERATION.DELETE;
//							}
//
//							pc.addJoinTableDataIntoMap(operation, jtmd
//									.getJoinTableName(), joinColumnName,
//									inverseJoinColumnName, node.getDataClass(),
//									entityId, childValues);
//						}

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
					/*     */}
				/*     */
				/* 188 */Node childNode = mainCache.getNodeFromCache(nodeLink
						.getTargetNodeId());
				/*     */
				/* 192 */Map parents = childNode.getParents();
				/* 193 */for (Object obj1 : parents.keySet())
				/*     */{
					NodeLink parentLink = (NodeLink) obj1;
					/* 195 */Relation.ForeignKey multiplicity = parentLink
							.getMultiplicity();
					/* 196 */if (multiplicity
							.equals(Relation.ForeignKey.MANY_TO_ONE))
					/*     */{
						/* 198 */Node parentNode = (Node) parents
								.get(parentLink);
						/*     */
						/* 200 */if ((!(parentNode.isTraversed()))
								&& (parentNode.isDirty()))
						/*     */{
							/* 202 */addNodesToFlushStack(pc, parentNode);
							/*     */}
						/*     */}
					/*     */
					/*     */}
				/*     */
				/* 208 */if ((!(childNode.isTraversed()))
						&& (childNode.isDirty()))
				/*     */{
					/* 210 */addNodesToFlushStack(pc, childNode);
					/*     */}
				/* 212 */else if (!(childNode.isDirty()))
				/*     */{
					/* 214 */childNode.setTraversed(true);
					/* 215 */flushStack.push(childNode);
					/*     */}
				/*     */
				/*     */}
			/*     */
			/*     */}
		/*     */
		
		/* 223 */if ((node.isTraversed()) || (!(node.isDirty())))
			/*     */return;
		/* 225 */node.setTraversed(true);
		/* 226 */flushStack.push(node);
		/*     */}

	/*     */
	/*     */public void clearFlushStack(PersistenceCache pc)
	/*     */{
		/* 239 */FlushStack flushStack = pc.getFlushStack();
		/*     */
		/* 241 */if ((flushStack == null) || (flushStack.isEmpty()))
			/*     */return;
		/* 243 */flushStack.clear();
		/*     */}
	/*     */
}

