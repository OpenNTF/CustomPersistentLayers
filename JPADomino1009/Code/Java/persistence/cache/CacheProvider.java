package persistence.cache;


import java.util.Map;
import javax.persistence.Cache;

public abstract interface CacheProvider
{
  public abstract void init(Map<?, ?> paramMap);

  public abstract void init(String paramString);

  public abstract Cache createCache(String paramString);

  public abstract Cache getCache(String paramString);

  public abstract void shutdown();
}

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.cache.CacheProvider
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
