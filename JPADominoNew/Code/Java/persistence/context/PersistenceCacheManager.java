package persistence.context;

import persistence.graph.Node;

public class PersistenceCacheManager {
	private PersistenceCache persistenceCache;

	public PersistenceCacheManager(PersistenceCache pc) {
		this.persistenceCache = pc;
	}

	public void clearPersistenceCache() {
		this.persistenceCache.clean();
	}

	private void cleanIndividualCache(CacheBase cache) {
		for (Node node : cache.getAllNodes()) {
			node.clear();
		}
	}

	public void markAllNodesNotTraversed() {
		for (Node node : this.persistenceCache.getMainCache().getAllNodes()) {
			node.setTraversed(false);
		}

	}
}
