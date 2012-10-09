package util;

import com.ibm.commons.util.NotImplementedException;

/**
 * @author weihang chen
 */

public class CommonUtil {

	public static String firstCharToUpperCase(String name) {
		return Character.toString(Character.toUpperCase(name.charAt(0)))
				+ name.substring(1);
	}

	public static String getMethodName(String className) {
		final StackTraceElement[] stacktrace = Thread.currentThread()
				.getStackTrace();
		StackTraceElement e = stacktrace[3];
		String methodName = e.getMethodName();
		String str = methodName + " from " + className;
		return str;
	}

}
