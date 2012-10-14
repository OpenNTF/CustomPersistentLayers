package persistence.cache;

public abstract interface Cache extends javax.persistence.Cache {
	public abstract int size();

	public abstract Object get(Object paramObject);

	public abstract void put(Object paramObject1, Object paramObject2);
}
