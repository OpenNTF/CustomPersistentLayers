package persistence.configure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Configurator {
	private static Log logger = LogFactory.getLog(Configurator.class);
	private List<Configuration> configurer = new ArrayList<Configuration>();
	private static Configurator instance;

	/**
	 * all three configurator will be updated later, use the temporary
	 * configurator instead, this configurator do not use persistence.xml, hard
	 * code all the functionality
	 */
	public static Configurator getInstance(String[] persistenceUnits) {
		if (instance == null) 
			instance = new Configurator(persistenceUnits);
		return instance;
	}

	private Configurator() {

	}

	private Configurator(String[] persistenceUnits) {
		// this.configurer.add(new
		// PersistenceUnitConfiguration(persistenceUnits));
		// this.configurer.add(new MetamodelConfiguration(persistenceUnits));
		// this.configurer.add(new SchemaConfiguration(persistenceUnits));
		// use the hard code one for now
		this.configurer.add(new MetamodelConfigurationTemp());
	}

	public void configure() {
		for (Configuration conf : this.configurer) {
			logger.debug("Loading configuration for :"
					+ conf.getClass().getSimpleName());
			conf.configure();
		}
	}
}
