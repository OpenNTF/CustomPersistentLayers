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
	public String formName();

	/**
	 * not use
	 */
	public String viewName() default "";

	/**
	 * 
	 * name to be data published as resolved variable, not used
	 */
	public String resolvedVarName() default "";

	/**
	 * used to inform runtime that entity bean is going to be intercepted , all
	 * intercept rules are defined in JavaBeanInterceptor, by default, its used
	 * to intercept getter/setter method. Other interceptor can be registerd in
	 * JavaBeanProxyFactory to deliver customized interception for specify
	 * entity beans.
	 */
	public boolean intercept() default false;
}
