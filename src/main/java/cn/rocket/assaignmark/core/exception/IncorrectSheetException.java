package cn.rocket.assaignmark.core.exception;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class IncorrectSheetException extends Exception {
    public IncorrectSheetException() {
    }

    public IncorrectSheetException(String message) {
        super(message);
    }

    public IncorrectSheetException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectSheetException(Throwable cause) {
        super(cause);
    }
}
