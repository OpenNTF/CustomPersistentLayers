package persistence.graph;

import persistence.metadata.model.Relation;
import persistence.metadata.model.Relation.ForeignKey;
import java.util.HashMap;
import java.util.Map;

public class NodeLink {
	private String sourceNodeId;
	private String targetNodeId;
	private Relation.ForeignKey multiplicity;
	private Map<LinkProperty, Object> linkProperties;

	public NodeLink() {
	}

	public NodeLink(String sourceNodeId, String targetNodeId) {
		this.sourceNodeId = sourceNodeId;
		this.targetNodeId = targetNodeId;
	}

	public String getSourceNodeId() {
		return this.sourceNodeId;
	}

	public void setSourceNodeId(String sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}

	public String getTargetNodeId() {
		return this.targetNodeId;
	}

	public void setTargetNodeId(String targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

	public Relation.ForeignKey getMultiplicity() {
		return this.multiplicity;
	}

	public void setMultiplicity(Relation.ForeignKey multiplicity) {
		this.multiplicity = multiplicity;
	}

	public Map<LinkProperty, Object> getLinkProperties() {
		return this.linkProperties;
	}

	public void setLinkProperties(Map<LinkProperty, Object> linkProperties) {
		this.linkProperties = linkProperties;
	}

	public Object getLinkProperty(LinkProperty name) {
		if ((this.linkProperties == null) || (this.linkProperties.isEmpty())) {
			throw new IllegalStateException("Link properties not initialized");
		}

		return this.linkProperties.get(name);
	}

	public void addLinkProperty(LinkProperty name, Object propertyValue) {
		if (this.linkProperties == null) {
			this.linkProperties = new HashMap();
		}

		this.linkProperties.put(name, propertyValue);
	}

	public int hashCode() {
		int n = getSourceNodeId().hashCode() * getTargetNodeId().hashCode();
		return n;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NodeLink)) {
			return false;
		}

		NodeLink targetNodeLink = (NodeLink) obj;

		return new org.apache.commons.lang3.builder.EqualsBuilder().append(
				getSourceNodeId(), targetNodeLink.getSourceNodeId()).append(
				getTargetNodeId(), targetNodeLink.getTargetNodeId()).isEquals();
	}

	public String toString() {
		return this.sourceNodeId + "---(" + this.multiplicity + ")--->"
				+ this.targetNodeId;
	}

	public static enum LinkProperty {
		LINK_NAME, LINK_VALUE, IS_SHARED_BY_PRIMARY_KEY, IS_BIDIRECTIONAL, IS_RELATED_VIA_JOIN_TABLE, PROPERTY, BIDIRECTIONAL_PROPERTY, CASCADE, JOIN_TABLE_METADATA;
	}
}
