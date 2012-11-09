package persistence.graph;

import persistence.client.Client;
import persistence.lifecycle.NodeStateContext;
import persistence.lifecycle.states.NodeState;
import persistence.lifecycle.states.TransientState;
import persistence.core.PersistenceDelegator;
import persistence.context.PersistenceCache;
import java.util.HashMap;
import java.util.Map;

public class Node implements NodeStateContext {
	private String nodeId;
	private Object data;
	private NodeState currentNodeState;
	private Class<?> dataClass;
	private Map<NodeLink, Node> parents;
	private Map<NodeLink, Node> children;
	private boolean traversed;
	private boolean dirty;
	private boolean isHeadNode;
	Client client;
	private PersistenceCache persistenceCache;
	PersistenceDelegator pd;

	public Node(String nodeId, Object data, PersistenceCache pc) {
		initializeNode(nodeId, data);
		setPersistenceCache(pc);

		this.currentNodeState = new TransientState();
	}

	public Node(String nodeId, Object data, NodeState initialNodeState,
			PersistenceCache pc) {
		initializeNode(nodeId, data);
		setPersistenceCache(pc);

		if (initialNodeState == null) {
			this.currentNodeState = new TransientState();
		} else {
			this.currentNodeState = initialNodeState;
		}
	}

	public Node(String nodeId, Class<?> nodeDataClass,
			NodeState initialNodeState, PersistenceCache pc) {
		this.nodeId = nodeId;
		this.dataClass = nodeDataClass;
		setPersistenceCache(pc);

		if (initialNodeState == null) {
			this.currentNodeState = new TransientState();
		} else {
			this.currentNodeState = initialNodeState;
		}
	}

	private void initializeNode(String nodeId, Object data) {
		this.nodeId = nodeId;
		this.data = data;
		this.dataClass = data.getClass();
	}

	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Class getDataClass() {
		return this.dataClass;
	}

	public void setDataClass(Class dataClass) {
		this.dataClass = dataClass;
	}

	public NodeState getCurrentNodeState() {
		return this.currentNodeState;
	}

	public void setCurrentNodeState(NodeState currentNodeState) {
		this.currentNodeState = currentNodeState;
	}

	public Map<NodeLink, Node> getParents() {
		return this.parents;
	}

	public void setParents(Map<NodeLink, Node> parents) {
		this.parents = parents;
	}

	public Map<NodeLink, Node> getChildren() {
		return this.children;
	}

	public void setChildren(Map<NodeLink, Node> children) {
		this.children = children;
	}

	public boolean isHeadNode() {
		return this.isHeadNode;
	}

	public void setHeadNode(boolean isHeadNode) {
		this.isHeadNode = isHeadNode;
	}

	public Node getParentNode(String parentNodeId) {
		NodeLink link = new NodeLink(parentNodeId, getNodeId());

		if (this.parents == null) {
			return null;
		}

		return ((Node) this.parents.get(link));
	}

	public Node getChildNode(String childNodeId) {
		NodeLink link = new NodeLink(getNodeId(), childNodeId);

		if (this.children == null) {
			return null;
		}

		return ((Node) this.children.get(link));
	}

	public void addParentNode(NodeLink nodeLink, Node node) {
		if ((this.parents == null) || (this.parents.isEmpty())) {
			this.parents = new HashMap();
		}
		this.parents.put(nodeLink, node);
	}

	public void addChildNode(NodeLink nodeLink, Node node) {
		if ((this.children == null) || (this.children.isEmpty())) {
			this.children = new HashMap();
		}
		this.children.put(nodeLink, node);
	}

	public boolean isTraversed() {
		return this.traversed;
	}

	public void setTraversed(boolean traversed) {
		this.traversed = traversed;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public Client getClient() {
		return this.client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public PersistenceDelegator getPersistenceDelegator() {
		return this.pd;
	}

	public void setPersistenceDelegator(PersistenceDelegator pd) {
		this.pd = pd;
	}

	public String toString() {
		return "[" + this.nodeId + "]";
	}
	
	public String getSimpleName(){
		//mode.Location@1111222
		//location1111 first 4 digits enough to identify
		String name=data.toString();
		name=name.substring(name.indexOf(".")+1).replace("@", "");
		return name;
	}

	public boolean equals(Object otherNode) {
		return super.equals(otherNode);
	}

	public void persist() {
		getCurrentNodeState().handlePersist(this);
	}

	public void remove() {
		getCurrentNodeState().handleRemove(this);
	}

	public void refresh() {
		getCurrentNodeState().handleRefresh(this);
	}

	public void merge() {
		getCurrentNodeState().handleMerge(this);
	}

	public void detach() {
		getCurrentNodeState().handleDetach(this);
	}

	public void close() {
		getCurrentNodeState().handleClose(this);
	}

	public void lock() {
		getCurrentNodeState().handleLock(this);
	}

	public void commit() {
		getCurrentNodeState().handleCommit(this);
	}

	public void rollback() {
		getCurrentNodeState().handleRollback(this);
	}

	public void find() {
		NodeState state = getCurrentNodeState();
		state.handleFind(this);
	}

	public void getReference() {
		getCurrentNodeState().handleGetReference(this);
	}

	public void contains() {
		getCurrentNodeState().handleContains(this);
	}

	public void clear() {
		getCurrentNodeState().handleClear(this);
	}

	public void flush() {
		//1107 delete
		System.out.println("is it dirty? "+isDirty());
		setDirty(true);
		//1107
		if (!(isDirty()))
			return;
		getCurrentNodeState().handleFlush(this);
	}

	public boolean isInState(Class<?> stateClass) {
		return getCurrentNodeState().getClass().equals(stateClass);
	}

	public PersistenceCache getPersistenceCache() {
		return this.persistenceCache;
	}

	public void setPersistenceCache(PersistenceCache persistenceCache) {
		this.persistenceCache = persistenceCache;
	}

}
