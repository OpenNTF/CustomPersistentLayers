package persistence.loader;


public abstract interface ClientLifeCycleManager
{
  public abstract void initialize();

  public abstract boolean isThreadSafe();

  public abstract void destroy();
}

/* Location:           C:\Users\SWECWI\Desktop\SECRET WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name:     com.impetus.kundera.loader.ClientLifeCycleManager
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */