package util;

import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.servlet.http.HttpServletRequest;

import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.lang3.StringUtils;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import model.Theme;
import model.notes.Key;

import persistence.annotation.support.JavaBeanFactory;
import persistence.configure.Configurator;
import persistence.core.EntityManagerFactoryImpl;
import persistence.core.EntityManagerImpl;
import persistence.core.KunderaPersistence;
import persistence.metadata.KunderaMetadataManager;
import persistence.metadata.model.KunderaMetadata;

import dao.DaoBase;
import exception.notes.InvalidStateException;

public class JSFUtil {
	public static Object getVariableValue(String varName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getVariableResolver().resolveVariable(
				context, varName);
	}

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

	/**
	 * use SignerWithFullAccess to open restricted database
	 * 
	 * @param dbName
	 * @return
	 */
	public static Database doOpenDatabase(String dbName) {
		Session strongSession = ExtLibUtil
				.getCurrentSessionAsSignerWithFullAccess();
		try {
			return strongSession.getDatabase(DominoUtils.getCurrentDatabase()
					.getServer(), dbName);
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void bark1(Session session) {
		// Database d=null;
		// try {
		// d =
		// session.getDatabase(DominoUtils.getCurrentDatabase().getServer(),"names.nsf");
		// } catch (NotesException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//		
		Database d = doOpenDatabase("names.nsf");
		System.out.println("KKK" + d);
		// System.out.println("1111"+d);
	}

	public static void test() {

		EntityManagerFactory emf = KunderaPersistence.getEntityManagerFactory();
		EntityManagerImpl entityManager = (EntityManagerImpl) emf
				.createEntityManager();
		entityManager.begin();
		try {
			Theme theme = entityManager.find(Theme.class,
					"954A9B5E0C30C8C5C1257A7300813F4C");
			System.out.println("FINAL RESULT GENERATE AT JSFUTIL:  " + theme);
			System.out.println("list1"+theme.getCSSList1());
			System.out.println("list2"+theme.getCSSList2());			
			Theme theme1 = entityManager.find(Theme.class,
					"954A9B5E0C30C8C5C1257A7300813F4C");

			// System.out.println(theme.getThemeName());
			// System.out.println(theme.getThemeType());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// Theme theme=entityManager.find(Theme.class,
		// "04BCDBDF26CBD4ACC1257A3E004C1B0E");
		// System.out.println("theme is found "+theme);
		// Theme theme = JavaBeanFactory.getProxy(Theme.class);
		// theme.setThemeName("newthemeHAHAHAH");
		// theme.setThemeType(4);

//		entityManager.flush();
//		entityManager.rollback();
//		entityManager.commit();
//		entityManager.clear();
//		entityManager.close();

	}

	public static void test1() {
		DaoBase base = new DaoBase();
		Key key = new Key();
		key.appendEntry("04BCDBDF26CBD4ACC1257A3E004C1B0E");
		try {
			Theme theme = (Theme) base.findOneByKey(key, null, "Theme",
					Theme.class);
			Theme weihang = org.apache.commons.lang3.SerializationUtils
					.clone(theme);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//
	public static Class getRealClass(Class clazz) {
		if (Enhancer.isEnhanced(clazz))
			clazz = clazz.getSuperclass();
		return clazz;
	}

	public static String getRelativeDBPath(Database db) {
		String currentPath = "";
		try {
			if (db == null)
				currentPath = DominoUtils.getCurrentDatabase().getFilePath();
			else
				db.getFilePath();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentPath;
	}

	public static String getPath1() {
		String currentPath = "";
		Session session = (Session) JSFUtil.getVariableValue("session");
		try {
			System.out.println("----break-----");
			Object o = FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			HttpServletRequest request = (HttpServletRequest) o;
			currentPath = request.getServerName() + "/"
					+ session.getCurrentDatabase().getFilePath();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentPath;
	}

}
