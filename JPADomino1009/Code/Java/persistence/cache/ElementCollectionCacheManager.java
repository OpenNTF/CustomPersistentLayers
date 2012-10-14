package persistence.cache;


/*     */ import persistence.utils.DeepEquals;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.persistence.PersistenceException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class ElementCollectionCacheManager
/*     */ {
/*  42 */   private static Log log = LogFactory.getLog(ElementCollectionCacheManager.class);
/*     */   private static ElementCollectionCacheManager instance;
/*     */   private static Map<String, Map<Object, String>> elementCollectionCache;
/*     */ 
/*     */   public static synchronized ElementCollectionCacheManager getInstance()
/*     */   {
/*  63 */     if (instance == null)
/*     */     {
/*  65 */       instance = new ElementCollectionCacheManager();
/*     */     }
/*  67 */     return instance;
/*     */   }
/*     */ 
/*     */   public Map<String, Map<Object, String>> getElementCollectionCache()
/*     */   {
/*  83 */     if (elementCollectionCache == null)
/*     */     {
/*  85 */       elementCollectionCache = new HashMap();
/*     */     }
/*  87 */     return elementCollectionCache;
/*     */   }
/*     */ 
/*     */   public boolean isCacheEmpty()
/*     */   {
/*  97 */     return ((elementCollectionCache == null) || (elementCollectionCache.isEmpty()));
/*     */   }
/*     */ 
/*     */   public void addElementCollectionCacheMapping(String rowKey, Object elementCollectionObject, String elementCollObjectName)
/*     */   {
/* 113 */     Map embeddedObjectMap = new HashMap();
/* 114 */     if (getElementCollectionCache().get(rowKey) == null)
/*     */     {
/* 116 */       embeddedObjectMap.put(elementCollectionObject, elementCollObjectName);
/* 117 */       getElementCollectionCache().put(rowKey, embeddedObjectMap);
/*     */     }
/*     */     else
/*     */     {
/* 121 */       ((Map)getElementCollectionCache().get(rowKey)).put(elementCollectionObject, elementCollObjectName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getElementCollectionObjectName(String rowKey, Object elementCollectionObject)
/*     */   {
/* 136 */     if (getElementCollectionCache().get(rowKey) == null)
/*     */     {
/* 138 */       log.debug("No element collection object map found in cache for Row key " + rowKey);
/* 139 */       return null;
/*     */     }
/*     */ 
/* 143 */     Map elementCollectionObjectMap = (Map)getElementCollectionCache().get(rowKey);
/* 144 */     String elementCollectionObjectName = (String)elementCollectionObjectMap.get(elementCollectionObject);
/*     */     Iterator i$;
/* 145 */     if (elementCollectionObjectName == null)
/*     */     {
/* 147 */       for (i$ = elementCollectionObjectMap.keySet().iterator(); i$.hasNext(); ) { Object obj = i$.next();
/*     */ 
/* 149 */         if (DeepEquals.deepEquals(elementCollectionObject, obj))
/*     */         {
/* 151 */           elementCollectionObjectName = (String)elementCollectionObjectMap.get(obj);
/* 152 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 157 */     if (elementCollectionObjectName == null)
/*     */     {
/* 159 */       log.debug("No element collection object name found in cache for object:" + elementCollectionObject);
/* 160 */       return null;
/*     */     }
/*     */ 
/* 164 */     return elementCollectionObjectName;
/*     */   }
/*     */ 
/*     */   public int getLastElementCollectionObjectCount(String rowKey)
/*     */   {
/* 178 */     if (getElementCollectionCache().get(rowKey) == null)
/*     */     {
/* 180 */       log.debug("No element collection object map found in cache for Row key " + rowKey);
/* 181 */       return -1;
/*     */     }
/*     */ 
/* 185 */     Map elementCollectionMap = (Map)getElementCollectionCache().get(rowKey);
/* 186 */     Collection<String> elementCollectionObjectNames = elementCollectionMap.values();
/* 187 */     int max = 0;
/*     */ 
/* 189 */     for (String s : elementCollectionObjectNames)
/*     */     {
/* 191 */       String elementCollectionCountStr = s.substring(s.indexOf("#") + 1);
/* 192 */       int elementCollectionCount = 0;
/*     */       try
/*     */       {
/* 195 */         elementCollectionCount = Integer.parseInt(elementCollectionCountStr);
/*     */       }
/*     */       catch (NumberFormatException e)
/*     */       {
/* 199 */         log.error("Invalid element collection Object name " + s);
/* 200 */         throw new PersistenceException("Invalid element collection Object name " + s);
/*     */       }
/* 202 */       if (elementCollectionCount > max)
/*     */       {
/* 204 */         max = elementCollectionCount;
/*     */       }
/*     */     }
/* 207 */     return max;
/*     */   }
/*     */ 
/*     */   public void clearCache()
/*     */   {
/* 216 */     elementCollectionCache = null;
/*     */     try
/*     */     {
/* 219 */       super.finalize();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 223 */       log.warn("Unable to reclaim memory while clearing ElementCollection cache. Nothing to worry, will be taken care of by GC");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.cache.ElementCollectionCacheManager
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
