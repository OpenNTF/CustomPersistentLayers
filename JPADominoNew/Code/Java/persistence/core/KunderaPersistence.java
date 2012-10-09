package persistence.core;

import persistence.configure.Configurator;
import persistence.core.EntityManagerFactoryImpl;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;

public class KunderaPersistence implements PersistenceProvider {
	private final String DOMINOPERSISTENUNIT = "DOMINOJPATEST";
	private static EntityManagerFactory emf;

	public synchronized static EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	public KunderaPersistence() {
	}

	public final EntityManagerFactory createContainerEntityManagerFactory(
			PersistenceUnitInfo info, Map map) {
		// return createEntityManagerFactory(info.getPersistenceUnitName(),
		// map);
		return createEntityManagerFactory(DOMINOPERSISTENUNIT, map);
	}

	public final synchronized EntityManagerFactory createEntityManagerFactory(
			String persistenceUnit, Map map) {
		synchronized (persistenceUnit) {
			// load persistence.xml, luncence.xml, cache global resources, kick
			// start jpa runtime
			initializeKundera(persistenceUnit);
			emf = EntityManagerFactoryImpl.getInstance(persistenceUnit, map);
			return emf;
		}
	}

	private void initializeKundera(String persistenceUnit) {
		String[] persistenceUnits = persistenceUnit.split(",");
		Configurator.getInstance(persistenceUnits).configure();
	}

	public ProviderUtil getProviderUtil() {
		throw new NotImplementedException("TODO");
	}
}
