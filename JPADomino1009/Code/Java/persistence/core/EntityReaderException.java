package persistence.core;

import persistence.exception.KunderaException;

public class EntityReaderException extends KunderaException {
	public EntityReaderException() {
	}

	public EntityReaderException(String arg0) {
		super(arg0);
	}

	public EntityReaderException(Throwable arg0) {
		super(arg0);
	}

	public EntityReaderException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
