package persistence.client;

/*    */ import persistence.exception.KunderaException;
/*    */ 
/*    */ public class ClientDatastoreOperationException extends KunderaException
/*    */ {
/*    */   private static final long serialVersionUID = 2884125874947278021L;
/*    */ 
/*    */   public ClientDatastoreOperationException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ClientDatastoreOperationException(String arg0)
/*    */   {
/* 47 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public ClientDatastoreOperationException(Throwable arg0)
/*    */   {
/* 56 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public ClientDatastoreOperationException(String arg0, Throwable arg1)
/*    */   {
/* 66 */     super(arg0, arg1);
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.client.ClientDatastoreOperationException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
