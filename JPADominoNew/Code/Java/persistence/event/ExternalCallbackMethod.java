package persistence.event;


/*    */ import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.PersistenceException;
/*    */ 
/*    */ public final class ExternalCallbackMethod
/*    */   implements CallbackMethod
/*    */ {
/*    */   private Class<?> clazz;
/*    */   private Method method;
/*    */ 
/*    */   public ExternalCallbackMethod(Class<?> clazz, Method method)
/*    */   {
/* 45 */     this.clazz = clazz;
/* 46 */     this.method = method;
/*    */   }
/*    */ 
/*    */   public void invoke(Object entity) throws PersistenceException
/*    */   {
/* 51 */     if (!(this.method.isAccessible()))
/* 52 */       this.method.setAccessible(true);
/*    */     try
/*    */     {
/* 55 */       this.method.invoke(this.clazz.newInstance(), new Object[] { entity });
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 59 */       throw new PersistenceException(e);
/*    */     }
/*    */     catch (IllegalAccessException e)
/*    */     {
/* 63 */       throw new PersistenceException(e);
/*    */     }
/*    */     catch (InvocationTargetException e)
/*    */     {
/* 67 */       throw new PersistenceException(e);
/*    */     }
/*    */     catch (InstantiationException e)
/*    */     {
/* 71 */       throw new PersistenceException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 78 */     StringBuilder builder = new StringBuilder();
/* 79 */     builder.append(this.clazz.getName() + "." + this.method.getName());
/* 80 */     return builder.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.persistence.event.ExternalCallbackMethod
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
