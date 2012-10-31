package persistence.lifecycle.states;


/*    */ import persistence.graph.Node;
/*    */ import persistence.lifecycle.NodeStateContext;
/*    */ import persistence.context.CacheBase;
/*    */ import persistence.context.PersistenceCache;
import util.CloneUtil;
/*    */ 
/*    */ public class TransientState extends NodeState
/*    */ {
/*    */   public void initialize(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handlePersist(NodeStateContext nodeStateContext)
/*    */   {
/* 38 */     moveNodeToNextState(nodeStateContext, new ManagedState());
/*    */ 
/* 41 */     nodeStateContext.setDirty(true);
/*    */ 
/* 44 */     nodeStateContext.getPersistenceCache().getMainCache().addNodeToCache((Node)nodeStateContext);
/*    */ 
/* 48 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.PERSIST);
/*    */   }
/*    */ 
/*    */   public void handleRemove(NodeStateContext nodeStateContext)
/*    */   {
/* 58 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.REMOVE);
/*    */   }
/*    */ 
/*    */   public void handleRefresh(NodeStateContext nodeStateContext)
/*    */   {
/* 68 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.REFRESH);
/*    */   }
/*    */ 
/*    */   public void handleMerge(NodeStateContext nodeStateContext)
/*    */   {
/* 76 */     Object copiedNodeData = CloneUtil.cloneDominoEntity(nodeStateContext.getData());
/* 77 */     nodeStateContext.setData(copiedNodeData);
/*    */   }
/*    */ 
/*    */   public void handleFind(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleClose(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleClear(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleFlush(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleLock(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleDetach(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleCommit(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleRollback(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleGetReference(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handleContains(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.lifecycle.states.TransientState
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */