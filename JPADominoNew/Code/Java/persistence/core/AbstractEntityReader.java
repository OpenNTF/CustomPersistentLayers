package persistence.core;

import persistence.annotation.DocumentReferences;
import persistence.annotation.resource.CascadeType;
import persistence.annotation.resource.FetchType;
import persistence.annotation.support.CollectionLazyLoader;
import persistence.annotation.support.ConstructibleAnnotatedCollection;
import persistence.client.Client;
import persistence.client.EnhanceEntity;
import persistence.metadata.KunderaMetadataManager; //import persistence.metadata.MetadataUtils;
import persistence.metadata.model.EntityMetadata;
import persistence.metadata.model.Relation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet; /*      */
import java.util.Iterator; /*      */
import java.util.List; /*      */
import java.util.Map; /*      */
import java.util.Set; /*      */
import javax.persistence.JoinColumn; /*      */
import javax.persistence.PersistenceException;
import persistence.utils.PropertyAccessorHelper;/*      */
import util.Assert;
import util.CommonUtil;
import util.JSFUtil;
import util.Predicate;
import util.ReflectionUtils;

import lotus.domino.NotesException;
import model.Theme;
import model.notes.Key;
import model.notes.ModelBase;
import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.logging.Log; /*      */
import org.apache.commons.logging.LogFactory;

import com.ibm.commons.util.StringUtil;

import exception.notes.ViewNotFoundException;

public class AbstractEntityReader {
	private static Log log = LogFactory.getLog(AbstractEntityReader.class);

	public Object recursivelyFindEntities(EnhanceEntity enhanceEntity,
			Client client, EntityMetadata m,
			PersistenceDelegator persistenceDelegator) {
		// this variable is used to check if duplicated relations is already
		// defined within the same class
		// for example @DocumentReferences(fetch = FetchType.LAZY, foreignKey =
		// "unid", viewName = "CSS")
		// @DocumentReferences(fetch = FetchType.EAGER, foreignKey = "unid",
		// viewName = "CSS", cascade = { CascadeType.SAVE_UPDATE })
		// shares the same collection, so maybe its good not to retrieve it from
		// database twice
		// not implemented yet, which is good, can test lazy/eager fetch first,
		// see if they lazy fetch get node from cache
		Map<String, Collection> relationValuesMap = new HashMap<String, Collection>();

		Client childClient = null;
		Class childClass = null;
		Object parentObj = enhanceEntity.getEntity();
		EntityMetadata childMetadata = null;
		for (Relation relation : m.getRelations()) {
			Relation.ForeignKey multiplicity = relation.getType();
			// lets assume one_to_many is documentsreferences
			// if foreignkey + viewname is the same, children collections is the
			// same
			if (multiplicity.equals(Relation.ForeignKey.ONE_TO_MANY)) {
				childClass = relation.getTargetEntity();
				childMetadata = KunderaMetadataManager
						.getEntityMetadata(childClass);
				Field collectionField = relation.getProperty();
				childClient = persistenceDelegator.getClient(childMetadata);
				// relationValue ex. document uniqueid is the relationalValue
				String relationSignature = relation
						.getDominoRelationSignature();

				if ((relationSignature != null)
						&& (!(relationValuesMap.containsKey(relationSignature
								+ childClass.getName())))) {
					// two situations here, eager, then fetch the collection and
					// assign it to the field
					// if its lazy, send in the required parameters so lazy
					// loader knows how to initialize the collection and merging
					// with the object graph
					System.out
							.println("RELATIONVALUEMAP DOES NOT CONTAIN THE COLLECTION, INIT COLLECTION ISSUED");
					Class<?> collectionClass = collectionField.getType();
					Method collectionSetterMethod = ReflectionUtils
							.getFieldSetterMethod(collectionField);
					Method collectionGetterMethod = ReflectionUtils
							.getFieldGetterMethod(collectionField);
					// ex. ArrayList<CSS> list=new ArrayList<CSS>(); CSS is the
					// returnType
					Class<?> returnType = ReflectionUtils
							.resolveReturnType(collectionGetterMethod);

					Constructor<Collection<Object>> constructor = null;
					Collection concreteCollection = null;
					try {
						constructor = ReflectionUtils.findCtor(collectionField);
						concreteCollection = constructor.newInstance();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					FetchType fetchType = relation.getFetchType();
					if (fetchType == FetchType.EAGER) {
						System.out.println("EAGER COLLECTION INIT");
						List childs = null;
						String foreignKey = relation.getDominoForeignKey();
						Method foreignKeyGetter = ReflectionUtils
								.findMethod(
										parentObj.getClass(),
										"get"
												+ CommonUtil
														.firstCharToUpperCase(foreignKey));
						Object foreignKeyFieldValue = ReflectionUtils
								.invokeGetterMethod(parentObj, foreignKeyGetter);

						if (foreignKeyFieldValue == null)
							continue;
						Key key = new Key();
						key.appendEntry(foreignKeyFieldValue.toString());

						try {
							childs = childClient.findAll(childClass, key);

							System.out
									.println("WEEEEEEEEEEEEEEEEEE: children collection "
											+ childs);

							if ((childs != null) && (!(childs.isEmpty()))) {

								for (Iterator i = childs.iterator(); i
										.hasNext();) {
									Object child = i.next();
									Object o = (child instanceof EnhanceEntity) ? ((EnhanceEntity) child)
											.getEntity()
											: child;

									if (!(o.getClass().equals(enhanceEntity
											.getEntity().getClass()))) {
										recursivelyFindEntities(
												new EnhanceEntity(
														o,
														PropertyAccessorHelper
																.getId(o,
																		childMetadata),
														null), childClient,
												childMetadata,
												persistenceDelegator);
									}
									concreteCollection.add(o);
								}
								relationValuesMap.put(relationSignature
										+ childClass.getName(),
										concreteCollection);
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						// CGLIB lazyloader
						System.out.println("LAZY COLLECTION INIT");
						ConstructibleAnnotatedCollection constructibleField = new ConstructibleAnnotatedCollection(
								collectionField, constructor, returnType);
						try {
							CollectionLazyLoader collectionLazyLoader = new CollectionLazyLoader(
									(ModelBase) parentObj, relation,
									constructibleField, persistenceDelegator,
									childMetadata);
							Object lazyCollection = Enhancer.create(
									collectionClass, collectionLazyLoader);
							collectionSetterMethod.invoke(parentObj,
									lazyCollection);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				
				ReflectionUtils.setFieldObject(parentObj, collectionField,
						relationValuesMap.get(relationSignature
								+ childClass.getName()));

				//Object o1 = enhanceEntity.getEntity();
//				System.out
//						.println("PERFORM CHECK SEE IF BOTH CSSLIST ARE POPULATED"
//								+ o1);
//				if (o1 instanceof Theme) {
//					System.out.println(((Theme) o1).getCSSList1());
//					System.out.println(((Theme) o1).getCSSList2());
//				}
//				System.out
//						.println("END PERFORM CHECK SEE IF BOTH CSSLIST ARE POPULATED"
//								+ o1);
			}
		}
		return enhanceEntity.getEntity();

	}

	protected EnhanceEntity findById(Key key, EntityMetadata entityMetadata,
			List<String> relationNames, Client client) {
		try {
			System.out.println("use client to find object: " + client
					+ " with key " + key + "/entity class: "
					+ entityMetadata.getEntityClazz());
			Object entity = client.find(entityMetadata.getEntityClazz(), key);
			if (entity == null) {
				return null;
			}
			return new EnhanceEntity(entity, getId(entity, entityMetadata),
					null);
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	private Set<?> onReflect(Object entity, Field ownerField, List<?> childs)
			throws RuntimeException {
		Set chids = new HashSet();
		if (childs != null) {
			chids = new HashSet(childs);

			PropertyAccessorHelper
					.set(entity, ownerField,
							(PropertyAccessorHelper.isCollection(ownerField
									.getType())) ? getFieldInstance(childs,
									ownerField) : childs.get(0));
		}
		return chids;
	}

	private Object getFieldInstance(List chids, Field f) {
		if (Set.class.isAssignableFrom(f.getType())) {
			Set col = new HashSet(chids);
			return col;
		}
		return chids;
	}

	protected String getId(Object entity, EntityMetadata metadata) {
		if (entity instanceof ModelBase) {
			return ((ModelBase) entity).getUnid();
		}
		try {
			return PropertyAccessorHelper.getId(entity, metadata);
		} catch (RuntimeException e) {
			log.error("Error while Getting ID. Details:" + e.getMessage());
			throw new EntityReaderException(
					"Error while Getting ID for entity " + entity, e);
		}
	}

}
