package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Vector;

import model.notes.Key;
import model.notes.ModelBase;
import net.sf.cglib.proxy.Enhancer;
import dao.DaoBase;

import persistence.annotation.DocumentReferences;
import persistence.annotation.resource.FetchType;
import persistence.annotation.support.CollectionLazyLoader;
import persistence.annotation.support.ConstructibleAnnotatedCollection;
import persistence.client.domino.DominoDBClient;
import persistence.core.PersistenceDelegator;
import persistence.graph.Node;

public class AnnotationUtil {

	public static boolean isEager(Field field) {

		if (ReflectionUtils.hasAnnotation(field, DocumentReferences.class)) {
			DocumentReferences ref = field
					.getAnnotation(DocumentReferences.class);
			if (FetchType.LAZY == ref.fetch())
				return false;
		}
		return true;
	}

	


}
