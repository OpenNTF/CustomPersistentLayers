package persistence.client;

import persistence.graph.Node; //import persistence.EntityReader;
import persistence.context.jointable.JoinTableData;
import persistence.core.EntityReader;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;

import exception.notes.CreateException;
import exception.notes.EmptyKeyException;
import exception.notes.ViewNotFoundException;

import lotus.domino.NotesException;
import model.notes.Key;

public abstract interface Client<Q extends Query> {
	public abstract Object find(Class paramClass, Key paramObject)
			throws EmptyKeyException, ViewNotFoundException, NotesException,
			SecurityException, IllegalArgumentException, CreateException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException;

	public abstract <E> List<E> findAll(Class<E> paramClass, Key key)
			throws ViewNotFoundException, NotesException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException;

	public abstract <E> List<E> find(Class<E> paramClass,
			Map<String, String> paramMap);

	public abstract void close();

	public abstract void delete(Object paramObject1, Object paramObject2);

	public abstract String getPersistenceUnit();

	public abstract void persist(Node paramNode);

	public abstract void persistJoinTable(JoinTableData paramJoinTableData);

	public abstract <E> List<E> getColumnsById(String paramString1,
			String paramString2, String paramString3, String paramString4);

	public abstract Object[] findIdsByColumn(String paramString1,
			String paramString2, String paramString3, Object paramObject,
			Class paramClass);

	public abstract void deleteByColumn(String paramString1,
			String paramString2, Object paramObject);

	public abstract List<Object> findByRelation(String paramString1,
			String paramString2, Class paramClass);

	public abstract EntityReader getReader();

	public abstract Class<Q> getQueryImplementor();
}
