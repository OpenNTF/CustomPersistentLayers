package persistence.utils;

/*     */import persistence.metadata.model.EntityMetadata; /*     */
import persistence.proxy.EnhancedEntity; /*     */
import persistence.utils.ReflectUtils; /*     */
import java.lang.reflect.Field; /*     */
import java.lang.reflect.InvocationTargetException; /*     */
import java.lang.reflect.Method; /*     */
import java.lang.reflect.Type; /*     */
import java.util.ArrayList; /*     */
import java.util.Collection; /*     */
import java.util.HashSet; /*     */
import java.util.List; /*     */
import java.util.Set;

/*     */
/*     */public class PropertyAccessorHelper
/*     */{
	/*     */public static void set(Object target, Field field, byte[] bytes)
	/*     */{
		// /* 56 */ PropertyAccessor accessor =
		// PropertyAccessorFactory.getPropertyAccessor(field);
		// /* 57 */ Object value = accessor.fromBytes(field.getType(), bytes);
		// /* 58 */ set(target, field, value);

		/*     */}

	/*     */
	/*     */public static void set(Object target, Field field, String fieldVal)
	/*     */{
		// /* 77 */ PropertyAccessor accessor =
		// PropertyAccessorFactory.getPropertyAccessor(field);
		// /* 78 */ Object value = accessor.fromString(target.getClass(),
		// fieldVal);
		// /* 79 */ set(target, field, value);
		/*     */}

	/*     */
	/*     */public static void set(Object target, Field field, Object value)
	/*     */{
		/* 98 */if (!(field.isAccessible()))
		/*     */{
			/* 100 */field.setAccessible(true);
			/*     */}
		/*     */try
		/*     */{
			/* 104 */field.set(target, value);
			/*     */}
		/*     */catch (IllegalArgumentException iarg)
		/*     */{
			/* 108 */throw new RuntimeException(iarg);
			/*     */}
		/*     */catch (IllegalAccessException iacc)
		/*     */{
			/* 112 */throw new RuntimeException(iacc);
			/*     */}
		/*     */}

	/*     */
	/*     */public static Object getObject(Object from, Field field)
	/*     */{
		/* 132 */if (!(field.isAccessible()))
		/*     */{
			/* 134 */field.setAccessible(true);
			/*     */}
		/*     */
		/*     */try
		/*     */{
			/* 139 */return field.get(from);
			/*     */}
		/*     */catch (IllegalArgumentException iarg)
		/*     */{
			/* 143 */throw new RuntimeException(iarg);
			/*     */}
		/*     */catch (IllegalAccessException iacc)
		/*     */{
			/* 147 */throw new RuntimeException(iacc);
			/*     */}
		/*     */}

	/*     */
	/*     */public static String getString(Object from, Field field)
	/*     */{
		/* 167 */// PropertyAccessor accessor =
					// PropertyAccessorFactory.getPropertyAccessor(field);
		/* 168 */Object object = getObject(from, field);
		/* 169 */// return ((object != null) ? accessor.toString(object) :
					// null);
		return "this is a string";
		/*     */}

	/*     */

	/*     */
	/*     */public static String getId(Object entity, EntityMetadata metadata)
	/*     */{
		/* 211 */if (entity instanceof EnhancedEntity)
		/*     */{
			/* 214 */return ((EnhancedEntity) entity).getId();
			/*     */}
		/*     */
		/* 219 */// return getString(entity, metadata.getIdColumn().getField());
		return "this is a id";
		/*     */}

	/*     */
	/*     */public static void setId(Object entity, EntityMetadata metadata,
			String rowKey)
	/*     */{
		/*     */try
		/*     */{
			// /* 238 */ Field idField = metadata.getIdColumn().getField();
			// /* 239 */ PropertyAccessor accessor =
			// PropertyAccessorFactory.getPropertyAccessor(idField);
			// /* 240 */ Object obj = accessor.fromString(idField.getClass(),
			// rowKey);

			Object obj = null;
			/*     */
			/* 242 */metadata.getWriteIdentifierMethod().invoke(entity,
					new Object[] { obj });
			/*     */}
		/*     */catch (IllegalArgumentException iarg)
		/*     */{
			/* 246 */throw new RuntimeException(iarg);
			/*     */}
		/*     */catch (IllegalAccessException iacc)
		/*     */{
			/* 250 */throw new RuntimeException(iacc);
			/*     */}
		/*     */catch (InvocationTargetException ite)
		/*     */{
			/* 254 */throw new RuntimeException(ite);
			/*     */}
		/*     */}

	/*     */
	/*     */public static final Object getObject(Object obj, String fieldName)
	/*     */{
		
		/*     */try
		/*     */{
			/* 276 */Field embeddedField = obj.getClass().getDeclaredField(
					fieldName);
			/* 277 */if (embeddedField != null)
			/*     */{
				/* 279 */if (!(embeddedField.isAccessible()))
				/*     */{
					/* 281 */embeddedField.setAccessible(true);
					/*     */}
				/* 283 */Object embededObject = embeddedField.get(obj);
				/* 284 */if (embededObject == null)
				/*     */{
					/* 286 */Class embeddedObjectClass = embeddedField
							.getType();
					/* 287 */if (Collection.class
							.isAssignableFrom(embeddedObjectClass))
					/*     */{
						/* 289 */if (embeddedObjectClass.equals(List.class))
						/*     */{
							/* 291 */return new ArrayList();
							/*     */}
						/* 293 */if (embeddedObjectClass.equals(Set.class))
						/*     */{
							/* 295 */return new HashSet();
							/*     */}
						/*     */}
					/*     */else
					/*     */{
						/* 300 */embededObject = embeddedField.getType()
								.newInstance();
						/* 301 */embeddedField.set(obj, embededObject);
						/*     */}
					/*     */}
				/*     */
				/* 305 */return embededObject;
				/*     */}
			/*     */
			/* 309 */throw new RuntimeException("Embedded object not found: "
					+ fieldName);
			/*     */}
		/*     */catch (Exception e)
		/*     */{
			/* 315 */e.printStackTrace();
			/*     */}
		return null;
		/*     */}

	/*     */
	/*     */public static Class<?> getGenericClass(Field collectionField)
	/*     */{
		/* 344 */Class genericClass = null;
		/* 345 */if (collectionField == null)
		/*     */{
			/* 347 */return genericClass;
			/*     */}
		/* 349 */if (isCollection(collectionField.getType()))
		/*     */{
			/* 352 */Type[] parameters = ReflectUtils
					.getTypeArguments(collectionField);
			/* 353 */if (parameters != null)
			/*     */{
				/* 355 */if (parameters.length == 1)
				/*     */{
					/* 357 */genericClass = (Class) parameters[0];
					/*     */}
				/*     */else
				/*     */{
					/* 361 */throw new RuntimeException(
							"Can't determine generic class from a field that has two parameters.");
					/*     */}
				/*     */}
			/*     */}
		/*     */
		/* 366 */return ((genericClass != null) ? genericClass
				: collectionField.getType());
		/*     */}

	/*     */
	/*     */public static Field[] getDeclaredFields(Field relationalField)
	/*     */{
		/*     */Field[] fields;
		/* 379 */if (isCollection(relationalField.getType()))
		/*     */{
			/* 381 */fields = getGenericClass(relationalField)
					.getDeclaredFields();
			/*     */}
		/*     */else
		/*     */{
			/* 385 */fields = relationalField.getType().getDeclaredFields();
			/*     */}
		/* 387 */return fields;
		/*     */}

	/*     */
	/*     */public static final boolean isCollection(Class<?> clazz)
	/*     */{
		/* 399 */return Collection.class.isAssignableFrom(clazz);
		/*     */}
	/*     */
}

/*
 * Location: C:\Users\SWECWI\Desktop\SECRET
 * WEAPON\Kundera\kundera-mongo\kundera-mongo-2.0.6-jar-with-dependencies.jar
 * Qualified Name: com.impetus.kundera.property.PropertyAccessorHelper Java
 * Class Version: 6 (50.0) JD-Core Version: 0.5.3
 */
