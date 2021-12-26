package cn.rocket.assaignmark.core.exception;

public class EmptyMarkTableException extends AssigningException {
    public EmptyMarkTableException() {
    }

    public EmptyMarkTableException(String message) {
        super(message);
    }

    public EmptyMarkTableException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyMarkTableException(Throwable cause) {
        super(cause);
    }
}
