package persistence.cache;


/*    */ public class NonOperationalCache
/*    */   implements Cache, javax.persistence.Cache
/*    */ {
/*    */   public int size()
/*    */   {
/* 36 */     return 0;
/*    */   }
/*    */ 
/*    */   public void put(Object key, Object value)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Object get(Object key)
/*    */   {
/* 63 */     return null;
/*    */   }
/*    */ 
/*    */   public boolean contains(Class paramClass, Object paramObject)
/*    */   {
/* 74 */     return false;
/*    */   }
/*    */ 
/*    */   public void evict(Class paramClass, Object paramObject)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void evict(Class paramClass)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void evictAll()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.cache.NonOperationalCache
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
