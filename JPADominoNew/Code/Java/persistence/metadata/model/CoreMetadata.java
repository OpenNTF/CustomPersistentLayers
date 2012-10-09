package persistence.metadata.model;

/**
 * @author weihang chen
 */
import persistence.annotation.support.JavaBeanFactory;
import persistence.proxy.EntityEnhancerFactory;

public class CoreMetadata {
	private EntityEnhancerFactory enhancedProxyFactory;
	private JavaBeanFactory javaBeanFactory;

	public EntityEnhancerFactory getEnhancedProxyFactory() {
		return this.enhancedProxyFactory;
	}

	public void setEnhancedProxyFactory(
			EntityEnhancerFactory enhancedProxyFactory) {
		this.enhancedProxyFactory = enhancedProxyFactory;
	}

	public JavaBeanFactory getJavaBeanFactory() {
		return javaBeanFactory;
	}

	public void setJavaBeanFactory(JavaBeanFactory javaBeanFactory) {
		this.javaBeanFactory = javaBeanFactory;
	}

}
