package persistence.cache;


/*    */ import java.util.Map;
import javax.persistence.Cache;
import javax.persistence.PersistenceException;
/*    */ 
/*    */ public class NonOperationalCacheProvider
/*    */   implements CacheProvider
/*    */ {
/* 31 */   private Cache cache = new NonOperationalCache();
/*    */ 
/*    */   public void init(Map<?, ?> properties)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Cache createCache(String name)
/*    */   {
/* 64 */     return this.cache;
/*    */   }
/*    */ 
/*    */   public Cache getCache(String name)
/*    */     throws PersistenceException
/*    */   {
/* 75 */     return null;
/*    */   }
/*    */ 
/*    */   public void shutdown()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void init(String cacheResourceName)
/*    */     throws PersistenceException
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.cache.NonOperationalCacheProvider
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
