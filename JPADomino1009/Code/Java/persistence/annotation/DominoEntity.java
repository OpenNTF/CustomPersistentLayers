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

	/**
	 * used to inform runtime that entity bean is going to be intercepted , all
	 * intercept rules are defined in JavaBeanInterceptor, by default, its used
	 * to intercept getter/setter method. Other interceptor can be registerd in
	 * JavaBeanProxyFactory to deliver customized interception for specify
	 * entity beans.
	 */
	public boolean intercept() default false;

	public String DBName() default "";
}
