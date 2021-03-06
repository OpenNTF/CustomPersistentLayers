package persistence.proxy;

import java.util.Map;
import java.util.Set;

public abstract interface EntityEnhancerFactory
{
  public abstract EnhancedEntity getProxy(Object paramObject, String paramString, Map<String, Set<String>> paramMap);
}
