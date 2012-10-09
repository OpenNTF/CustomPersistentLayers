package persistence.event;

/*    */ import persistence.exception.KunderaException;
/*    */ 
/*    */ public class EventListenerException extends KunderaException
/*    */ {
/*    */   public EventListenerException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public EventListenerException(String arg0)
/*    */   {
/* 40 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public EventListenerException(Throwable arg0)
/*    */   {
/* 48 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public EventListenerException(String arg0, Throwable arg1)
/*    */   {
/* 57 */     super(arg0, arg1);
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.persistence.event.EventListenerException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
