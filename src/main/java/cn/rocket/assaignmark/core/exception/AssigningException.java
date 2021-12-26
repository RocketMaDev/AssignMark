package cn.rocket.assaignmark.core.exception;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class AssigningException extends Exception {
    public AssigningException() {
    }

    public AssigningException(String message) {
        super(message);
    }

    public AssigningException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssigningException(Throwable cause) {
        super(cause);
    }
}
