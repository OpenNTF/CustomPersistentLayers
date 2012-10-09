package persistence.metadata.model;

public class KunderaMetadata {
	private CoreMetadata coreMetadata;
	private ApplicationMetadata applicationMetadata;

	private static KunderaMetadata INSTANCE = new KunderaMetadata();

	private KunderaMetadata() {
		// update should be made later, use Configurator to set values in the
		// KunderarMetadata
		// init the configurator in application listener
		getApplicationMetadata();
		getCoreMetadata();
		
	}

	public static synchronized KunderaMetadata getInstance() {
		return INSTANCE;
	}

	public ApplicationMetadata getApplicationMetadata() {
		if (this.applicationMetadata == null) {
			this.applicationMetadata = new ApplicationMetadata();
		}
		return this.applicationMetadata;
	}

	public void setApplicationMetadata(ApplicationMetadata applicationMetadata) {
		this.applicationMetadata = applicationMetadata;
	}

	public CoreMetadata getCoreMetadata() {
		if (this.coreMetadata == null) {
			this.coreMetadata = new CoreMetadata();
		}
		return this.coreMetadata;
	}

	public void setCoreMetadata(CoreMetadata coreMetadata) {
		this.coreMetadata = coreMetadata;
	}

}
