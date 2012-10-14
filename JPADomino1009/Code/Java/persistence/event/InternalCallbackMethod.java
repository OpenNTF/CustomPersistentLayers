package persistence.event;


/*    */ import persistence.metadata.model.EntityMetadata;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public final class InternalCallbackMethod
/*    */   implements CallbackMethod
/*    */ {
/*    */   private final EntityMetadata entityMetadata;
/*    */   private Method method;
/*    */ 
/*    */   public InternalCallbackMethod(EntityMetadata entityMetadata, Method method)
/*    */   {
/* 47 */     this.entityMetadata = entityMetadata;
/* 48 */     this.method = method;
/*    */   }
/*    */ 
/*    */   public void invoke(Object entity)
/*    */     throws EventListenerException
/*    */   {
/* 63 */     if (!(this.method.isAccessible()))
/* 64 */       this.method.setAccessible(true);
/*    */     try
/*    */     {
/* 67 */       this.method.invoke(entity, new Object[0]);
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 71 */       throw new EventListenerException(e);
/*    */     }
/*    */     catch (IllegalAccessException e)
/*    */     {
/* 75 */       throw new EventListenerException(e);
/*    */     }
/*    */     catch (InvocationTargetException e)
/*    */     {
/* 79 */       throw new EventListenerException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 86 */     StringBuilder builder = new StringBuilder();
/* 87 */     builder.append(this.entityMetadata.getEntityClazz().getName() + "." + this.method.getName());
/* 88 */     return builder.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.persistence.event.InternalCallbackMethod
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
