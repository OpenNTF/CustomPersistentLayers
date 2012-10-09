package persistence.core;

import persistence.cache.CacheException;
import persistence.cache.CacheProvider;
import persistence.cache.NonOperationalCacheProvider;
import persistence.loader.ClientFactory;
import persistence.loader.ClientLifeCycleManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;

//create instance of entitymanagerfactory in application listern do not know how to refer to this single instance in Xpages, use singleton instead
public class EntityManagerFactoryImpl implements EntityManagerFactory {
	private static Log logger = LogFactory
			.getLog(EntityManagerFactoryImpl.class);
	private boolean closed;
	private Map<String, Object> properties;
	private CacheProvider cacheProvider;
	private static EntityManagerFactoryImpl entityManagerFactory;
	String[] persistenceUnits;
	PersistenceUnitTransactionType transactionType;

	public static synchronized EntityManagerFactoryImpl getInstance(
			String persistenceUnit, Map<String, Object> properties) {
		if (entityManagerFactory == null)
			entityManagerFactory = new EntityManagerFactoryImpl(
					persistenceUnit, properties);
		return entityManagerFactory;
	}

	private EntityManagerFactoryImpl(PersistenceUnitInfo persistenceUnitInfo,
			Map props) {
		this((persistenceUnitInfo != null) ? persistenceUnitInfo
				.getPersistenceUnitName() : null, props);
	}

	private EntityManagerFactoryImpl() {
	}

	private EntityManagerFactoryImpl(String persistenceUnit,
			Map<String, Object> properties) {
		this.closed = false;

		if (properties == null) {
			properties = new HashMap();
		}
		properties.put("persistenceUnitName", persistenceUnit);
		this.properties = properties;
		this.persistenceUnits = persistenceUnit.split(",");

		this.cacheProvider = initSecondLevelCache();
		this.cacheProvider.createCache("Kundera");

		logger.info("Loading Client(s) For Persistence Unit(s) "
				+ persistenceUnit);
		// Set txTypes = new HashSet();
		// for (String pu : this.persistenceUnits) {
		// PersistenceUnitTransactionType txType = KunderaMetadataManager
		// .getPersistenceUnitMetadata(pu).getTransactionType();
		// txTypes.add(txType);
		// ClientResolver.getClientFactory(pu).load(pu);
		// }
		//		
		// if (txTypes.size() != 1) {
		// throw new IllegalArgumentException(
		// "For polyglot persistence, it is mandatory for all persistence units to have same Transction type.");
		// }

		// this.transactionType = ((PersistenceUnitTransactionType) txTypes
		// .iterator().next());
		//		
		this.transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
		logger.info("EntityManagerFactory created for persistence unit : "
				+ persistenceUnit);
	}

	public final void close() {
		this.closed = true;

		if (this.cacheProvider != null) {
			this.cacheProvider.shutdown();
		}
		// for (String pu : this.persistenceUnits) {
		// ((ClientLifeCycleManager) ClientResolver.getClientFactory(pu))
		// .destroy();
		// }
	}

	public final EntityManager createEntityManager() {
		return new EntityManagerImpl(this, this.transactionType,
				PersistenceContextType.EXTENDED);
	}

	public final EntityManager createEntityManager(Map map) {
		return new EntityManagerImpl(this, map, this.transactionType,
				PersistenceContextType.EXTENDED);
	}

	public final boolean isOpen() {
		return (!(this.closed));
	}

	public CriteriaBuilder getCriteriaBuilder() {
		throw new NotImplementedException("TODO");
	}

	public Metamodel getMetamodel() {
		// return KunderaMetadataManager.getMetamodel(getPersistenceUnits());
		return null;
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public Cache getCache() {
		return this.cacheProvider.getCache("Kundera");
	}

	public PersistenceUnitUtil getPersistenceUnitUtil() {
		throw new NotImplementedException("TODO");
	}

	public PersistenceUnitTransactionType getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(
			PersistenceUnitTransactionType transactionType) {
		this.transactionType = transactionType;
	}

	private CacheProvider initSecondLevelCache() {
		String classResourceName = (String) getProperties().get(
				"kundera.cache.config.resource");
		String cacheProviderClassName = (String) getProperties().get(
				"kundera.cache.provider.class");

		CacheProvider cacheProvider = null;
		if (cacheProviderClassName != null) {
			try {
				Class cacheProviderClass = Class
						.forName(cacheProviderClassName);
				cacheProvider = (CacheProvider) cacheProviderClass
						.newInstance();
				cacheProvider.init(classResourceName);
			} catch (ClassNotFoundException e) {
				throw new CacheException(
						"Could not find class "
								+ cacheProviderClassName
								+ ". Check whether you spelled it correctly in persistence.xml",
						e);
			} catch (InstantiationException e) {
				throw new CacheException("Could not instantiate "
						+ cacheProviderClassName, e);
			} catch (IllegalAccessException e) {
				throw new CacheException(e);
			}
		}
		if (cacheProvider == null) {
			cacheProvider = new NonOperationalCacheProvider();
		}
		return cacheProvider;
	}

	private String[] getPersistenceUnits() {
		return this.persistenceUnits;
	}
}

/*
 * Location: C:\Users\SWECWI\Desktop\SECRET
 * WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name: com.impetus.kundera.persistence.EntityManagerFactoryImpl Java
 * Class Version: 6 (50.0) JD-Core Version: 0.5.3
 */
