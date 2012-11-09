package persistence.context;

import persistence.graph.Node;
import persistence.graph.ObjectGraph;
import util.CloneUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CacheBase {
	private static Log log = LogFactory.getLog(CacheBase.class);
	private Map<String, Node> nodeMappings;
	private Set<Node> headNodes;

	public CacheBase() {
		this.headNodes = new HashSet();
		this.nodeMappings = new HashMap();
	}

	public Node getNodeFromCache(String nodeId) {
		Node node = (Node) this.nodeMappings.get(nodeId);
		return node;
	}

	/**
	 * 1. clone the input node and put the clone into the persistence cache
	 * <p>
	 * 2. if input node does existing in cache, put parent/children nodes from
	 * the existingnode from persistence cache to the new clone, else just put
	 * it in cache
	 * 
	 * @param node
	 */
	public void addNodeToCache(Node node) {
		Object nodeDataCopy = CloneUtil.cloneDominoEntity(node.getData());
		node.setData(nodeDataCopy);
		// node already exists from persistence cache
		if (this.nodeMappings.containsKey(node.getNodeId())) {
			Node existingNode = (Node) this.nodeMappings.get(node.getNodeId());

			if (existingNode.getParents() != null) {
				if (node.getParents() == null) {
					node.setParents(new HashMap());
				}
				node.getParents().putAll(existingNode.getParents());
			}

			if (existingNode.getChildren() != null) {
				if (node.getChildren() == null) {
					node.setChildren(new HashMap());
				}
				node.getChildren().putAll(existingNode.getChildren());
			}

			this.nodeMappings.put(node.getNodeId(), node);
			logCacheEvent("ADDED TO ", node.getNodeId());
		} else {
			// node does not exist from persistence cache
			logCacheEvent("ADDED TO ", node.getNodeId());
			this.nodeMappings.put(node.getNodeId(), node);
		}

		if (!(node.isHeadNode()))
			return;

		node.getPersistenceCache().getMainCache().addHeadNode(node);

	}

	public void removeNodeFromCache(Node node) {
		if (getHeadNodes().contains(node)) {
			getHeadNodes().remove(node);
		}

		if (this.nodeMappings.get(node.getNodeId()) != null) {
			this.nodeMappings.remove(node.getNodeId());
		}

		logCacheEvent("REMOVED FROM ", node.getNodeId());
		node = null;
	}

	public void addGraphToCache(ObjectGraph graph,
			PersistenceCache persistenceCache) {
		for (String key : graph.getNodeMapping().keySet()) {
			Node thisNode = (Node) graph.getNodeMapping().get(key);
			addNodeToCache(thisNode);
			if ((!(thisNode.isHeadNode()))
					&& (persistenceCache.getMainCache().getHeadNodes()
							.contains(thisNode))) {
				persistenceCache.getMainCache().getHeadNodes().remove(thisNode);
			}
		}
		addHeadNode(graph.getHeadNode());
		System.out.println("CURRENT CACHE INSTANCE "
				+ persistenceCache.getMainCache() + " /CURRENT CACHE HEADNODE "
				+ graph.getHeadNode() + " /CURRENT CACHE SIZE "
				+ persistenceCache.getMainCache().size());
	}

	private void logCacheEvent(String eventType, String nodeId) {
		log.debug("Node: " + nodeId + ":: " + eventType
				+ " Persistence Context");
	}

	public void setNodeMappings(Map<String, Node> nodeMappings) {
		this.nodeMappings = nodeMappings;
	}

	public Set<Node> getHeadNodes() {
		return this.headNodes;
	}

	public void addHeadNode(Node headNode) {
		this.headNodes.add(headNode);
	}

	public int size() {
		return this.nodeMappings.size();
	}

	public Collection<Node> getAllNodes() {
		return this.nodeMappings.values();
	}

	public void clear() {
		this.nodeMappings.clear();
		this.headNodes.clear();
	}
}
