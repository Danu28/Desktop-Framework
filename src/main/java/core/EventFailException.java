package core;

/**
 * Custom exception class for representing an event failure.
 */
@SuppressWarnings("serial")
public class EventFailException extends Exception {

	/**
	 * Constructs a new EventFailException with the specified error message.
	 *
	 * @param errorMessage The error message that describes the event failure.
	 */
	public EventFailException(String errorMessage) {
		super(errorMessage);
	}
}
