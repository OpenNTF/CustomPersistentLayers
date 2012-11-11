package persistence.annotation;
/**
 * @author weihang chen
 */
import java.lang.annotation.*;

import javax.persistence.CascadeType;

import persistence.annotation.resource.FetchType;

/**
 * Follow references to other documents when loading and updating this
 * collection based on the result of a view query. The parameter
 * <code>view</code> defines the name of a view located in the design document
 * of the class or in the design document specified by <code>designDoc</code>.
 * The parameter <code>fetch</code> controls when reference loading is
 * performed. This annotation only has meaning on collection class members.
 * 
 * @author ragnar rova
 * 
 */
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentReferences {

	/**
	 * Controls when referenced documents are loaded. Default is
	 * <code>LAZY</code> and implies that references should be loaded when a
	 * method on the collection is accessed which needs the documents.
	 * <code>EAGER</code> means that all references of arbitrary depth will be
	 * followed directly at load time.
	 */
	public FetchType fetch() default FetchType.LAZY;

	/**
	 * descending , ascending
	 */
	public boolean descendingSortOrder() default false;

	/**
	 * collection order field
	 */
	public String orderBy() default "";

	/**
	 * Set the type if cascade behaviour this collection should have.
	 * 
	 * @return
	 */
	public CascadeType[] cascade() default { CascadeType.ALL };

	/**
	 * unid from owner class as foreignkey
	 */
	public String foreignKey();

	public String viewName();

}
