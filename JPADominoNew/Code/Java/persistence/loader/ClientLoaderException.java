package persistence.loader;

/*    */ import persistence.exception.KunderaException;
/*    */ 
/*    */ public class ClientLoaderException extends KunderaException
/*    */ {
/*    */   private static final long serialVersionUID = -2780499169457052865L;
/*    */ 
/*    */   public ClientLoaderException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ClientLoaderException(String arg0)
/*    */   {
/* 44 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public ClientLoaderException(Throwable arg0)
/*    */   {
/* 53 */     super(arg0);
/*    */   }
/*    */ 
/*    */   public ClientLoaderException(String arg0, Throwable arg1)
/*    */   {
/* 63 */     super(arg0, arg1);
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.loader.ClientLoaderException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */