package util;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.servlet.http.HttpServletRequest;

import net.sf.cglib.proxy.Enhancer;


import com.ibm.commons.util.NotImplementedException;
import com.ibm.xsp.domino.context.DominoFacesContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.model.domino.wrapped.DominoDocument.FieldValueHolder;
import com.ibm.xsp.util.DataPublisher;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import model.Location;
import model.Tool;
import model.notes.Key;
import model.notes.ModelBase;

import persistence.annotation.support.JavaBeanFactory;
import persistence.annotation.support.JavaBeanProxyFactory;
import persistence.configure.Configurator;
import persistence.context.CacheBase;
import persistence.core.EntityManagerFactoryImpl;
import persistence.core.EntityManagerImpl;
import persistence.graph.Node;
import persistence.metadata.MetadataManager;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.model.KunderaMetadata;
import persistence.metadata.model.Relation;
import persistence.metadata.model.Relation.ForeignKey;

import persistence.core.DominoPersistenceProvider;
import dao.DaoBase;

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

	public static void test() {

		EntityManagerFactory emf = DominoPersistenceProvider
				.getEntityManagerFactory();
		EntityManagerImpl entityManager = (EntityManagerImpl) emf
				.createEntityManager();
		entityManager.begin();
		try {
			// Theme theme = entityManager.find(Theme.class,
			// "954A9B5E0C30C8C5C1257A7300813F4C");
			// System.out.println("FINAL RESULT GENERATE AT JSFUTIL:  " +
			// theme);
			// System.out.println("list1" + theme.getCSSList1());
			// System.out.println("list2" + theme.getCSSList2());
			// Theme theme1 = entityManager.find(Theme.class,
			// "954A9B5E0C30C8C5C1257A7300813F4C");
			// theme1.setThemeName("jingjingzaiji");
			//
			// // entityManager.clear();
			// // entityManager.close();
			// List<CSS> list1 = theme1.getCSSList2();
			// CSS css = list1.get(0);
			//
			//			
			// entityManager.persist(theme1);
			// System.out
			// .println("try to find the theme and check if its the same");
			// Theme theme2 = entityManager.find(Theme.class, id);
			// if (theme2 == t1) {
			// System.out
			// .println("COOOOOOOOLLLLLLLLLLLLLLLLLL THE NEWLY PERSISTED ENTITY GOT MANAGED , AND STAY IN THE FIRST LEVEL CACHE");
			// }
			// EntityManagerImpl entityManager1 = (EntityManagerImpl) emf
			// .createEntityManager();
			// entityManager1.begin();
			// entityManager1.persist(theme1);

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
		// entityManager.persist(theme1);
		// entityManager.flush();
		// entityManager.rollback();
		// entityManager.commit();
		// entityManager.clear();
		// entityManager.close();

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

	// push data to requestscope map, used in our project to show cache objects
	// and graph
	public static void pushData(Object obj, String name) {
		DataPublisher dataPublisher = ((DominoFacesContext) FacesContext
				.getCurrentInstance()).getDataPublisher();
		dataPublisher.pushObject(dataPublisher.createShadowedList(), name, obj);
	}

	public static void p(String title, Object content) {
		System.out.println(title + " : " + content.toString());
	}

}
