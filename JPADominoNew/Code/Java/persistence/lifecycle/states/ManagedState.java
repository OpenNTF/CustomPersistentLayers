package persistence.lifecycle.states;

import persistence.client.Client;
import persistence.client.EnhanceEntity;
import persistence.graph.Node;
import persistence.graph.ObjectGraphBuilder;
import persistence.lifecycle.NodeStateContext;
import persistence.metadata.MetadataManager;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.model.KunderaMetadata;
import persistence.core.EntityReader;
import persistence.context.CacheBase;
import persistence.context.PersistenceCache;
import java.util.List;
import javax.persistence.PersistenceContextType;

import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import lotus.domino.Session;
import model.notes.Key;

/**
 * 
 * @author weihang chen
 * 
 */
public class ManagedState extends NodeState {
	public void initialize(NodeStateContext nodeStateContext) {
	}

	public void handlePersist(NodeStateContext nodeStateContext) {
		recursivelyPerformOperation(nodeStateContext,
				NodeState.OPERATION.PERSIST);
	}

	public void handleRemove(NodeStateContext nodeStateContext) {
		moveNodeToNextState(nodeStateContext, new RemovedState());

		nodeStateContext.setDirty(true);
		// 1111 ADD THE SHOULD BE REMOVED NODE to the cache head nodes, so the
		// flush will proccess it change Kundera code
//		nodeStateContext.getPersistenceCache().getMainCache().addHeadNode(
//				(Node) nodeStateContext);
		// 1111
		recursivelyPerformOperation(nodeStateContext,
				NodeState.OPERATION.REMOVE);
	}

	public void handleRefresh(NodeStateContext nodeStateContext) {
		recursivelyPerformOperation(nodeStateContext,
				NodeState.OPERATION.REFRESH);
	}

	public void handleMerge(NodeStateContext nodeStateContext) {
		nodeStateContext.getPersistenceCache().getMainCache().addNodeToCache(
				(Node) nodeStateContext);
		recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.MERGE);
	}

	public void handleFind(NodeStateContext nodeStateContext) {
		Client client = nodeStateContext.getClient();
		Class nodeDataClass = nodeStateContext.getDataClass();
		EntityMetadata entityMetadata = MetadataManager
				.getEntityMetadata(nodeDataClass);

		String nodeId = nodeStateContext.getNodeId();
		String entityId = ObjectGraphBuilder.getEntityId(nodeStateContext
				.getNodeId());
		Object nodeData = null;
		EntityReader reader = client.getReader();
		// just put entityId in Key as temporary solution
		Key key = new Key();
		key.appendEntry(entityId);
		EnhanceEntity enhanceEntity = reader.findById(key, entityMetadata,
				entityMetadata.getRelationNames(), client);
		System.out.println("RETURNED ENHANCEENTITY: " + enhanceEntity
				+ " /id: " + enhanceEntity.getEntityId());
		if ((enhanceEntity != null) && (enhanceEntity.getEntity() != null)) {
			Object entity = enhanceEntity.getEntity();
			if ((((entityMetadata.getRelationNames() == null) || (entityMetadata
					.getRelationNames().isEmpty())))) {
				nodeData = entity;
			} else {
				nodeData = reader.recursivelyFindEntities(enhanceEntity,
						client, entityMetadata, nodeStateContext
								.getPersistenceDelegator());
			}
		}

		nodeStateContext.setData(nodeData);
		nodeStateContext.setDirty(false);
	}

	public void handleClose(NodeStateContext nodeStateContext) {
		handleDetach(nodeStateContext);
	}

	public void handleClear(NodeStateContext nodeStateContext) {
		handleDetach(nodeStateContext);
	}

	public void handleFlush(NodeStateContext nodeStateContext) {
		Client client = nodeStateContext.getClient();
		client.persist((Node) nodeStateContext);

		nodeStateContext.setDirty(false);
	}

	public void handleLock(NodeStateContext nodeStateContext) {
	}

	public void handleDetach(NodeStateContext nodeStateContext) {
		moveNodeToNextState(nodeStateContext, new DetachedState());

		recursivelyPerformOperation(nodeStateContext,
				NodeState.OPERATION.DETACH);
	}

	public void handleCommit(NodeStateContext nodeStateContext) {
		nodeStateContext.setCurrentNodeState(new DetachedState());
	}

	public void handleRollback(NodeStateContext nodeStateContext) {
		if (PersistenceContextType.EXTENDED.equals(nodeStateContext
				.getPersistenceCache().getPersistenceContextType())) {
			moveNodeToNextState(nodeStateContext, new TransientState());
		} else {
			if (!(PersistenceContextType.TRANSACTION.equals(nodeStateContext
					.getPersistenceCache().getPersistenceContextType()))) {
				return;
			}
			moveNodeToNextState(nodeStateContext, new DetachedState());
		}
	}

	public void handleGetReference(NodeStateContext nodeStateContext) {
	}

	public void handleContains(NodeStateContext nodeStateContext) {
	}
}
