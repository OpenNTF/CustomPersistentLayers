package persistence.cache.ehcache;


/*     */ import persistence.cache.CacheProvider;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;

import javax.persistence.PersistenceException;

/*     */ import net.sf.ehcache.CacheManager;
/*     */ import net.sf.ehcache.Ehcache;
/*     */ import net.sf.ehcache.event.CacheEventListener;
/*     */ import net.sf.ehcache.event.RegisteredEventListeners;
/*     */ import net.sf.ehcache.util.ClassLoaderUtil;
/*     */ import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class EhCacheProvider
/*     */   implements CacheProvider
/*     */ {
/*  45 */   private static final Log log = LogFactory.getLog(EhCacheProvider.class);
/*     */   private CacheManager manager;
/*     */   private javax.persistence.Cache cache;
/*     */   private static final String NET_SF_EHCACHE_CONFIGURATION_RESOURCE_NAME = "net.sf.ehcache.configurationResourceName";
/*     */   private boolean initializing;
/*     */   private List<CacheEventListener> listeners;
/*     */ 
/*     */   public EhCacheProvider()
/*     */   {
/*  60 */     this.listeners = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void init(String cacheResourceName)
/*     */   {
/*  70 */     if (this.manager != null)
/*     */     {
/*  72 */       log.warn("Attempt to restart an already started CacheFactory. Using previously created EhCacheFactory.");
/*  73 */       return;
/*     */     }
/*  75 */     this.initializing = true;
/*     */     try
/*     */     {
/*  78 */       String configurationResourceName = cacheResourceName;
/*  79 */       if ((configurationResourceName == null) || (configurationResourceName.length() == 0))
/*     */       {
/*  81 */         this.manager = new CacheManager();
/*     */       }
/*     */       else
/*     */       {
/*  85 */         if (!(configurationResourceName.startsWith("/")))
/*     */         {
/*  87 */           configurationResourceName = "/" + configurationResourceName;
/*  88 */           log.info("prepending / to " + configurationResourceName + ". It should be placed in the root" + "of the classpath rather than in a package.");
/*     */         }
/*     */ 
/*  91 */         URL url = loadResource(configurationResourceName);
/*  92 */         this.manager = new CacheManager(url);
/*     */       }
/*     */     }
/*     */     catch (net.sf.ehcache.CacheException e)
/*     */     {
/*  97 */       if (e.getMessage().startsWith("Cannot parseConfiguration CacheManager. Attempt to create a new instance of CacheManager using the diskStorePath"));
/* 105 */       throw e;
/*     */     }
/*     */     finally
/*     */     {
/* 110 */       this.initializing = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void init(Map<?, ?> properties)
/*     */   {
/* 124 */     if (this.manager != null)
/*     */     {
/* 126 */       log.warn("Attempt to restart an already started CacheFactory. Using previously created EhCacheFactory.");
/* 127 */       return;
/*     */     }
/* 129 */     this.initializing = true;
/*     */     try
/*     */     {
/* 132 */       String configurationResourceName = null;
/* 133 */       if (properties != null)
/*     */       {
/* 135 */         configurationResourceName = (String)properties.get("net.sf.ehcache.configurationResourceName");
/*     */       }
/* 137 */       if ((configurationResourceName == null) || (configurationResourceName.length() == 0))
/*     */       {
/* 139 */         this.manager = new CacheManager();
/*     */       }
/*     */       else
/*     */       {
/* 143 */         if (!(configurationResourceName.startsWith("/")))
/*     */         {
/* 145 */           configurationResourceName = "/" + configurationResourceName;
/* 146 */           log.info("prepending / to " + configurationResourceName + ". It should be placed in the root" + "of the classpath rather than in a package.");
/*     */         }
/*     */ 
/* 149 */         URL url = loadResource(configurationResourceName);
/* 150 */         this.manager = new CacheManager(url);
/*     */       }
/*     */     }
/*     */     catch (net.sf.ehcache.CacheException e)
/*     */     {
/* 155 */       if (e.getMessage().startsWith("Cannot parseConfiguration CacheManager. Attempt to create a new instance of CacheManager using the diskStorePath"));
/* 163 */       throw new PersistenceException(e);
/*     */     }
/*     */     finally
/*     */     {
/* 168 */       this.initializing = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private URL loadResource(String configurationResourceName)
/*     */   {
/* 182 */     ClassLoader standardClassloader = ClassLoaderUtil.getStandardClassLoader();
/* 183 */     URL url = null;
/* 184 */     if (standardClassloader != null)
/*     */     {
/* 186 */       url = standardClassloader.getResource(configurationResourceName);
/*     */     }
/* 188 */     if (url == null)
/*     */     {
/* 190 */       url = super.getClass().getResource(configurationResourceName);
/*     */     }
/* 192 */     log.info("Creating EhCacheFactory from a specified resource: " + configurationResourceName + " Resolved to URL: " + url);
/*     */ 
/* 195 */     if (url == null)
/*     */     {
/* 197 */       log.warn("A configurationResourceName was set to " + configurationResourceName + " but the resource could not be loaded from the classpath." + "Ehcache will configure itself using defaults.");
/*     */     }
/*     */ 
/* 201 */     return url;
/*     */   }
/*     */ 
/*     */   public javax.persistence.Cache createCache(String name)
/*     */   {
/* 207 */     if (this.manager == null)
/*     */     {
/* 209 */       throw new PersistenceException("CacheFactory was not initialized. Call init() before creating a cache.");
/*     */     }
/*     */     try
/*     */     {
/* 213 */       net.sf.ehcache.Cache cache = this.manager.getCache(name);
/* 214 */       if (cache == null)
/*     */       {
/* 216 */         log.warn("Could not find a specific ehcache configuration for cache named [" + name + "]; using defaults.");
/*     */ 
/* 218 */         this.manager.addCache(name);
/* 219 */         cache = this.manager.getCache(name);
/*     */       }
/* 221 */       Ehcache backingCache = cache;
/* 222 */       if ((!(backingCache.getCacheEventNotificationService().hasCacheEventListeners())) && 
/* 224 */         (this.listeners.size() > 0))
/*     */       {
/* 226 */         for (CacheEventListener listener : this.listeners)
/*     */         {
/* 228 */           if (!(backingCache.getCacheEventNotificationService().getCacheEventListeners().contains(listener)))
/*     */           {
/* 231 */             backingCache.getCacheEventNotificationService().registerListener(listener);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 240 */       this.cache = new EhCacheWrapper(cache);
/* 241 */       return this.cache;
/*     */     }
/*     */     catch (net.sf.ehcache.CacheException e)
/*     */     {
/* 245 */       throw new PersistenceException("Could not create cache: " + name, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public javax.persistence.Cache getCache(String cacheName)
/*     */     throws PersistenceException
/*     */   {
/* 258 */     if (this.cache == null)
/*     */     {
/* 260 */       this.cache = createCache(cacheName);
/*     */     }
/*     */ 
/* 263 */     return this.cache;
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 275 */     if (this.manager == null)
/*     */       return;
/* 277 */     this.manager.shutdown();
/* 278 */     this.manager = null;
/*     */   }
/*     */ 
/*     */   public void clearAll()
/*     */   {
/* 287 */     this.manager.clearAll();
/*     */   }
/*     */ 
/*     */   public CacheManager getCacheManager()
/*     */   {
/* 297 */     return this.manager;
/*     */   }
/*     */ 
/*     */   public void addDefaultListener(CacheEventListener cacheEventListener)
/*     */   {
/* 308 */     this.listeners.add(cacheEventListener);
/*     */   }
/*     */ }

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.cache.ehcache.EhCacheProvider
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
