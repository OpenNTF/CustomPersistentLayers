package persistence.annotation;
/**
 * @author weihang chen
 */
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DominoProperty {

	public String itemName();
}
