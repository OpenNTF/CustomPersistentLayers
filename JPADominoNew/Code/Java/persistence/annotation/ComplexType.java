package persistence.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
// @Target(ElementType.FIELD)
public @interface ComplexType {
	public abstract String foreignKey();

	public abstract String primaryKey();

	public abstract boolean storeForeignKeys();
}
