package persistence.graph;

import java.util.HashMap;
import java.util.Map;

public class ObjectGraph {
	private Node headNode;
	private Map<String, Node> nodeMapping;

	public ObjectGraph() {
		this.nodeMapping = new HashMap();
	}

	public void addNode(String nodeId, Node node) {
		this.nodeMapping.put(nodeId, node);
	}

	public Node getNode(String nodeId) {
		return ((Node) this.nodeMapping.get(nodeId));
	}

	public Node getHeadNode() {
		return this.headNode;
	}

	public void setHeadNode(Node headNode) {
		this.headNode = headNode;
	}

	public Map<String, Node> getNodeMapping() {
		return this.nodeMapping;
	}
}
