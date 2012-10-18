package persistence.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @author weihang chen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DominoEntity {
	// Form
	public String formName();

	// from which view this document can be found
	public String viewName();

	public String DBName() default "";
}
