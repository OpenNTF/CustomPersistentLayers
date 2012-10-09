package util;

/**
 * @author weihang chen
 */

public class CommonUtil {

	public static String firstCharToUpperCase(String name) {
		return Character.toString(Character.toUpperCase(name.charAt(0)))
				+ name.substring(1);
	}

}
