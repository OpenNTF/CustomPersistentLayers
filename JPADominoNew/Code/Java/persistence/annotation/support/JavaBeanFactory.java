package persistence.annotation.support;

import java.lang.reflect.Constructor;

import net.sf.cglib.proxy.Enhancer;
import model.notes.ModelBase;

/**
 * 
 * @author weihang chen
 */

public class JavaBeanFactory {

	public static <T> T getProxy(Class<T> clazz) {
		return getInstance(clazz, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> clazz, Object dominoDoc) {
		try {
			Constructor con = clazz
					.getConstructor(new Class[] { Object.class });
			T object1 = (T) con.newInstance(new Object[] { dominoDoc });
			if (!(object1 instanceof ModelBase))
				return null;

			Object result = null;
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(clazz);
			JavaBeanInterceptor interceptor = new JavaBeanInterceptor();
			interceptor.setTarget(object1);
			enhancer.setCallback(interceptor);

			// !!IMPORTANT CGLIB WILL CREATE A SHADOW INSTANCE
			// AND UNID GETS RECALCULATED, need to reset the id after
			// creating an interceptor
			String originalUNID = ((ModelBase) object1).getUnid();
			result = enhancer.create();
			((ModelBase) result).setUnid(originalUNID);
			return (T) result;

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
