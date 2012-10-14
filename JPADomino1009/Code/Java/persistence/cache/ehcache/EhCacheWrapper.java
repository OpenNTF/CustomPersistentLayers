package persistence.cache.ehcache;

/*     */import com.ibm.commons.util.NotImplementedException;

import net.sf.ehcache.Element;

/*     */public class EhCacheWrapper
/*     */implements persistence.cache.Cache
/*     */{
	/*     */private net.sf.ehcache.Cache ehcache;

	/*     */
	/*     */public EhCacheWrapper(net.sf.ehcache.Cache ehcache)
	/*     */{
		/* 43 */this.ehcache = ehcache;
		/*     */}

	/*     */
	/*     */public Object get(Object key)
	/*     */{
		/* 55 */Element element = this.ehcache.get(key);
		/* 56 */return ((element == null) ? null : element.getObjectValue());
		/*     */}

	/*     */
	/*     */public void put(Object key, Object value)
	/*     */{
		/* 72 */this.ehcache.put(new Element(key, value));
		/*     */}

	/*     */
	/*     */public int size()
	/*     */{
		/* 84 */return this.ehcache.getSize();
		/*     */}

	/*     */
	/*     */public boolean contains(Class arg0, Object arg1)
	/*     */{
		/* 95 */return (this.ehcache.get(arg1) != null);
		/*     */}

	/*     */
	/*     */public void evict(Class arg0)
	/*     */{
		/* 107 */throw new NotImplementedException("TODO");
		/*     */}

	/*     */
	/*     */public void evict(Class arg0, Object arg1)
	/*     */{
		/* 119 */this.ehcache.remove(arg1);
		/*     */}

	/*     */
	/*     */public void evictAll()
	/*     */{
		/* 130 */this.ehcache.removeAll();
		/*     */}
	/*     */
}

/*
 * Location: C:\Users\SWECWI\Desktop\SECRET
 * WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name: com.impetus.kundera.cache.ehcache.EhCacheWrapper Java Class
 * Version: 6 (50.0) JD-Core Version: 0.5.3
 */
