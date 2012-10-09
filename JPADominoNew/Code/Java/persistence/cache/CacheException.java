package persistence.cache;

/*    */import persistence.exception.KunderaException;

/*    */
/*    */public class CacheException extends KunderaException
/*    */{
	/*    */private static final long serialVersionUID = 1L;

	/*    */
	/*    */public CacheException(String s)
	/*    */{
		/* 40 */super(s);
		/*    */}

	/*    */
	/*    */public CacheException(String s, Throwable e)
	/*    */{
		/* 53 */super(s, e);
		/*    */}

	/*    */
	/*    */public CacheException(Throwable e)
	/*    */{
		/* 64 */super(e);
		/*    */}
	/*    */
}

/*
 * Location: C:\Users\SWECWI\Desktop\SECRET
 * WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name: com.impetus.kundera.cache.CacheException Java Class Version:
 * 6 (50.0) JD-Core Version: 0.5.3
 */
