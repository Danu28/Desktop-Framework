package exceptions;

public class CustomLocationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new CustomLocationException with the specified detail message.
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                getMessage() method).
	 */
	public CustomLocationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new CustomLocationException with the specified detail message
	 * and cause.
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                getMessage() method).
	 * @param cause   The cause (which is saved for later retrieval by the
	 *                getCause() method).
	 */
	public CustomLocationException(String message, Throwable cause) {
		super(message, cause);
	}

}
