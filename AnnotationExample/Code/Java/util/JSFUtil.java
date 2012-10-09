package util;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import persistence.annotation.support.JavaBeanFactory;

import dao.DaoBase;

public class JSFUtil {

	public static Object getBindingValue(String ref) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		return application.createValueBinding(ref).getValue(context);
	}

	public static DaoBase getDaoBase() {
		DaoBase daoBase = (DaoBase) JSFUtil.getBindingValue("#{DaoBase}");
		return daoBase;
	}

	public static JavaBeanFactory getBeanProxyFactory() {
		JavaBeanFactory beanProxyFactory = (JavaBeanFactory) JSFUtil
				.getBindingValue("#{JavaBeanFactory}");
		return beanProxyFactory;
	}

}
