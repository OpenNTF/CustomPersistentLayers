package persistence.lifecycle.states;


/*    */ import persistence.lifecycle.NodeStateContext;
/*    */ 
/*    */ public class DetachedState extends NodeState
/*    */ {
/*    */   public void initialize(NodeStateContext nodeStateContext)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handlePersist(NodeStateContext nodeStateContext)
/*    */   {
/* 35 */     throw new IllegalArgumentException("Persist operation not allowed in Detached state");
/*    */   }
/*    */ 
/*    */   public void handleRemove(NodeStateContext nodeStateContext)
/*    */   {
/* 41 */     throw new IllegalArgumentException("Remove operation not allowed in Detached state. Possible reason: You may have closed entity manager before calling remove. A solution is to call merge before remove.");
/*    */   }
/*    */ 
/*    */   public void handleRefresh(NodeStateContext nodeStateContext)
/*    */   {
/* 49 */     throw new IllegalArgumentException("Refresh operation not allowed in Detached state");
/*    */   }
/*    */ 
/*    */   public void handleMerge(NodeStateContext nodeStateContext)
/*    */   {
/* 56 */     moveNodeToNextState(nodeStateContext, new ManagedState());
/*    */ 
/* 64 */     recursivelyPerformOperation(nodeStateContext, NodeState.OPERATION.MERGE);
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
 * Qualified Name:     com.impetus.kundera.lifecycle.states.DetachedState
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */