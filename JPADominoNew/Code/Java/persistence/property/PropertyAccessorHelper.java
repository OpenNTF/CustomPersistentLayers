package persistence.property;

import persistence.utils.ReflectUtils;
import util.CommonUtil;
import util.JSFUtil;
import util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.jsse2.util.f;

import model.Theme;

public class PropertyAccessorHelper {

	public static void set(Object target, Field field, Object value) {
		if (!(field.isAccessible())) {
			field.setAccessible(true);
		}
		try {
			field.set(target, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getGenericClass(Field collectionField) {
		Class genericClass = null;
		if (collectionField == null) {
			return genericClass;
		}
		if (isCollection(collectionField.getType())) {
			Type[] parameters = ReflectUtils.getTypeArguments(collectionField);
			if (parameters != null) {
				if (parameters.length == 1) {
					genericClass = (Class) parameters[0];
				} else {
					throw new RuntimeException(
							"Can't determine generic class from a field that has two parameters.");
				}
			}
		}

		return ((genericClass != null) ? genericClass : collectionField
				.getType());
	}

	public static Field[] getDeclaredFields(Field relationalField) {
		Field[] fields;
		if (isCollection(relationalField.getType())) {
			fields = getGenericClass(relationalField).getDeclaredFields();
		} else {
			fields = relationalField.getType().getDeclaredFields();
		}
		return fields;
	}

	public static final boolean isCollection(Class<?> clazz) {
		return Collection.class.isAssignableFrom(clazz);
	}
}
