package persistence.lifecycle.states;
 
/*     */ import persistence.client.Client;
/*     */ import persistence.graph.Node;
/*     */ import persistence.graph.ObjectGraphBuilder;
/*     */ import persistence.lifecycle.NodeStateContext;
/*     */ import persistence.context.CacheBase;
/*     */ import persistence.context.PersistenceCache;
/*     */ import javax.persistence.PersistenceContextType;
/*     */ 
/*     */ public class RemovedState extends NodeState
/*     */ {
/*     */   public void initialize(NodeStateContext nodeStateContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void handlePersist(NodeStateContext nodeStateContext)
/*     */   {
/*  40 */     moveNodeToNextState(nodeStateContext, new ManagedState());
/*     */ 
/*  44 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.PERSIST);
/*     */   }
/*     */ 
/*     */   public void handleRemove(NodeStateContext nodeStateContext)
/*     */   {
/*  54 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.REMOVE);
/*     */   }
/*     */ 
/*     */   public void handleRefresh(NodeStateContext nodeStateContext)
/*     */   {
/*  64 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.REFRESH);
/*     */   }
/*     */ 
/*     */   public void handleMerge(NodeStateContext nodeStateContext)
/*     */   {
/*  70 */     throw new IllegalArgumentException("Merge operation not allowed in Removed state");
/*     */   }
/*     */ 
/*     */   public void handleFind(NodeStateContext nodeStateContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void handleClose(NodeStateContext nodeStateContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void handleClear(NodeStateContext nodeStateContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void handleFlush(NodeStateContext nodeStateContext)
/*     */   {
/*  96 */     Client client = nodeStateContext.getClient();
/*     */ 
/*  98 */     Node node = (Node)nodeStateContext;
/*  99 */     String entityId = ObjectGraphBuilder.getEntityId(node.getNodeId());
/* 100 */     client.delete(node.getData(), entityId);
/*     */ 
/* 103 */     nodeStateContext.setDirty(false);
/*     */ 
/* 106 */     nodeStateContext.getPersistenceCache().getMainCache().removeNodeFromCache(node);
/*     */   }
/*     */ 
/*     */   public void handleLock(NodeStateContext nodeStateContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void handleDetach(NodeStateContext nodeStateContext)
/*     */   {
/* 121 */     moveNodeToNextState(nodeStateContext, new DetachedState());
/*     */ 
/* 125 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.DETACH);
/*     */   }
/*     */ 
/*     */   public void handleCommit(NodeStateContext nodeStateContext)
/*     */   {
/* 131 */     nodeStateContext.setCurrentNodeState(new TransientState());
/*     */   }
/*     */ 
/*     */   public void handleRollback(NodeStateContext nodeStateContext)
/*     */   {
/* 140 */     if (PersistenceContextType.EXTENDED.equals(nodeStateContext.getPersistenceCache().getPersistenceContextType()))
/*     */     {
/* 142 */       moveNodeToNextState(nodeStateContext, new ManagedState());
/*     */     }
/*     */     else {
/* 145 */       if (!(PersistenceContextType.TRANSACTION.equals(nodeStateContext.getPersistenceCache().getPersistenceContextType()))) {
/*     */         return;
/*     */       }
/* 148 */       nodeStateContext.detach();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handleGetReference(NodeStateContext nodeStateContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void handleContains(NodeStateContext nodeStateContext)
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.lifecycle.states.RemovedState
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
