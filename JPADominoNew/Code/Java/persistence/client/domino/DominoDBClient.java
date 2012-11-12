package persistence.client.domino;

import persistence.annotation.support.DominoEntityHelper;
import persistence.annotation.support.JavaBeanFactory;
import persistence.client.Client;
import persistence.graph.Node;
import persistence.core.EntityReader;
import persistence.context.jointable.JoinTableData;
import util.Assert;
import util.JSFUtil;
import util.ResourceUtil;

import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.PersistenceException;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;
import model.notes.Key;
import model.notes.ModelBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.commons.util.NotImplementedException;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.domino.DominoUtils;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

/**
 * 
 * @author weihang chen
 * 
 */

/**
 * class responsibility: 1. direct interaction with database, wrapping document
 * inside of java object 2. java object are created by JavaBeanFactory, and get
 * intercepted
 */

public class DominoDBClient implements Client {
	// public class DominoDBClient extends ClientBase {

	private Database dominoDb;
	private String persistenceUnit;
	private EntityReader reader;
	private static Log log = LogFactory.getLog(DominoDBClient.class);

	public DominoDBClient(Object dominoDb, EntityReader reader,
			String persistenceUnit) {
		this.dominoDb = ((Database) dominoDb);
		this.reader = reader;
		this.persistenceUnit = persistenceUnit;
	}

	public void persist(Node node) {
		// indexNode(node, entityMetadata, getIndexManager());
		System.out.println("dominodbclient persiste(node) starts");
		Object obj1 = node.getData();
		if (obj1 instanceof ModelBase) {
			((ModelBase) obj1).persist();
		}
	}

	public void persistJoinTable(JoinTableData joinTableData) {
		throw new NotImplementedException(" Not implemented");
	}

	/**
	 * 
	 * @param entityClass
	 * @param key
	 * @return
	 * @throws EmptyKeyException
	 * @throws ViewNotFoundException
	 * @throws NotesException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws CreateException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Object find(Class entityClass, Key key) throws PersistenceException,
			PersistenceException, NotesException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (!(key instanceof Key) || key.getEntries().size() == 0)
			throw new PersistenceException("empty key");
		String viewName = DominoEntityMetaDataUtil.getViewName(entityClass);
		View lup = ResourceUtil.getViewByName1(dominoDb, viewName);
		Document doc = lup.getDocumentByKey(key.getEntries(), true);
		Assert.notNull(doc, "Business Object with ID " + key + " not found!");
		String dbName = dominoDb.getFileName();
		// if dbName is same as current dbname, no need to use it
		String currentDbName = DominoUtils.getCurrentDatabase().getFileName();
		Object obj = null;
		if (StringUtil.equals(currentDbName, dbName)) {
			obj = documentToJava("", doc, entityClass);
		} else {
			obj = documentToJava(dbName, doc, entityClass);
		}
		return obj;
	}

	public List findAll(Class entityClass, Key key)
			throws PersistenceException, NotesException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {

		String viewName = DominoEntityMetaDataUtil.getViewName(entityClass);
		View lup = ResourceUtil.getViewByName1(dominoDb, viewName);
		// if key is null, return all, else get by key
		if (key == null) {
			ViewEntryCollection vc = lup.getAllEntries();
			if (vc == null)
				return null;
			return viewEntryCollectionToJava(vc, entityClass);
		} else if (key instanceof Key) {
			DocumentCollection dc = lup.getAllDocumentsByKey(key.getEntries(),
					true);
			return documentCollectionToJava(dc, entityClass);
		}
		return null;

	}

	/**
	 * 
	 * @param entity
	 * @param key
	 * @throws ViewNotFoundException
	 * @throws NotesException
	 */
	public void delete(Object entity, String docUNID)
			throws PersistenceException, NotesException {
		String viewName = DominoEntityMetaDataUtil.getViewName(JSFUtil
				.getRealClass(entity.getClass()));
		View lup = ResourceUtil.getViewByName(viewName);
		System.out.println("do I get a view " + lup);
		if (lup != null) {
			Document doc = DominoUtils.getCurrentDatabase().getDocumentByUNID(
					docUNID);

			System.out.println("do I get a doc " + doc);
			Assert.notNull(doc, "Business Object with ID " + docUNID
					+ " not found!");
			boolean b = doc.remove(true);
			Assert.isTrue(b, "Business Object with ID " + docUNID
					+ " can not be deleted");
		}
	}

	public String getPersistenceUnit() {
		return this.persistenceUnit;
	}

	public EntityReader getReader() {
		return this.reader;
	}

	private String[] getString(Object[] pKeys) {
		if (pKeys != null) {
			String[] arr = new String[pKeys.length];
			int counter = 0;
			for (Object o : pKeys) {
				arr[(counter++)] = o.toString();
			}
			return arr;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Vector viewEntryCollectionToJava(ViewEntryCollection vc, Class clazz)
			throws NotesException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Vector<Object> col = new Vector<Object>();
		ViewEntry ve = vc.getFirstEntry();
		ViewEntry tmp = null;
		String dbName = DominoEntityHelper.getDBName(clazz);
		while (ve != null) {
			Document doc = ve.getDocument();
			DominoDocument dominoDoc = DominoDocument.wrap(dbName, doc, "both",
					"force", true, "", "");

			Object obj = JavaBeanFactory.getInstance(clazz, dominoDoc);
			col.add(obj);
			tmp = vc.getNextEntry();
			ve.recycle();
			ve = tmp;
		}
		return col;
	}

	@SuppressWarnings("unchecked")
	private Vector documentCollectionToJava(DocumentCollection dc, Class clazz)
			throws NotesException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Vector<Object> col = new Vector<Object>();
		Document doc = dc.getFirstDocument();
		String dbName = DominoEntityHelper.getDBName(clazz);
		while (doc != null) {
			DominoDocument dominoDoc = DominoDocument.wrap(dbName, doc, "both",
					"force", true, "", "");
			// 0716
			Object obj = JavaBeanFactory.getInstance(clazz, dominoDoc);

			// Constructor con = clazz
			// .getConstructor(new Class[] { Object.class });
			// Object obj = con.newInstance(new Object[] { dominoDoc });
			col.add(obj);
			Document tmp = dc.getNextDocument();
			doc.recycle();
			doc = tmp;
		}
		return col;
	}

	@SuppressWarnings("unchecked")
	private Object documentToJava(String dbName, Document doc, Class clazz)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Object obj = null;
		DominoDocument dominoDoc = DominoDocument.wrap(dbName, doc, "both",
				"force", true, "", "");
		obj = JavaBeanFactory.getInstance(clazz, dominoDoc);
		Assert
				.notNull(
						obj,
						"document fails to be converted to Java object: documentToJava(Document doc, Class clazz)");
		return obj;
	}

	public void deleteByColumn(String paramString1, String paramString2,
			Object paramObject) {
		// TODO Auto-generated method stub

	}

	public Object find(Class paramClass, Object paramObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object> findByRelation(String paramString1,
			String paramString2, Class paramClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] findIdsByColumn(String paramString1, String paramString2,
			String paramString3, Object paramObject, Class paramClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public List find(Class paramClass, Map paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getColumnsById(String paramString1, String paramString2,
			String paramString3, String paramString4) {
		// TODO Auto-generated method stub
		return null;
	}

	public Class getQueryImplementor() {
		// TODO Auto-generated method stub
		return null;
	}

}
