package exception.annotation;

import exception.notes.BaseException;

public class CollectionNotSupportedException extends BaseException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7735155903989140966L;

	public CollectionNotSupportedException(String msg) {
		super(msg);
	}
}
