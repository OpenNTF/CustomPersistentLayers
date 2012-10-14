package persistence.metadata;

/**
 * 
 * @author weihang chen 
 */
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.processor.CacheableAnnotationProcessor;
import persistence.metadata.processor.DominoEntityProcessor;
import persistence.metadata.processor.EntityListenersProcessor;
import persistence.metadata.processor.IndexProcessor; //import persistence.metadata.processor.TableProcessor; // import persistence.metadata.validator.EntityValidator;
import persistence.metadata.processor.TableProcessor;
//import persistence.metadata.validator.EntityValidatorImpl;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;

public class MetadataBuilder {

	private List<MetadataProcessor> metadataProcessors;
	// private EntityValidator validator;
	// private boolean instantiated = false;
	private String persistenceUnit;
	private String client;

	/**
	 * 
	 * @param puName
	 * @param client
	 */

	public MetadataBuilder(String puName, String client) {
		this.persistenceUnit = puName;
		this.client = client;
		// this.validator = new EntityValidatorImpl();
		this.metadataProcessors = new ArrayList<MetadataProcessor>();
		// this.metadataProcessors.add(new TableProcessor());
		// this.metadataProcessors.add(new CacheableAnnotationProcessor());
		// this.metadataProcessors.add(new IndexProcessor());
		// this.metadataProcessors.add(new EntityListenersProcessor());
		this.metadataProcessors.add(new DominoEntityProcessor());
		this.metadataProcessors.add(new TableProcessor());

	}

	/**
	 * add entity validator back when persistence.xml is in use
	 */
	public final void validate(Class<?> clazz) throws PersistenceException {

		// this.validator.validate(clazz);
	}

	/**
	 * set value in EntityMetadata instance via MetadataProcessors
	 * 
	 * @param clazz
	 * @return
	 */
	public EntityMetadata buildEntityMetadata(Class<?> clazz) {
		EntityMetadata metadata = new EntityMetadata(clazz);
		validate(clazz);
		for (MetadataProcessor processor : this.metadataProcessors) {
			processor.process(clazz, metadata);
		}
		return metadata;
	}

}
