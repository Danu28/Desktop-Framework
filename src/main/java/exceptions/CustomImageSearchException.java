package exceptions;

public class CustomImageSearchException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new CustomImageSearchException with the specified detail
	 * message.
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                getMessage() method).
	 */
	public CustomImageSearchException(String message) {
		super(message);
	}

	/**
	 * Constructs a new CustomImageSearchException with the specified detail message
	 * and cause.
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                getMessage() method).
	 * @param cause   The cause (which is saved for later retrieval by the
	 *                getCause() method).
	 */
	public CustomImageSearchException(String message, Throwable cause) {
		super(message, cause);
	}

}
