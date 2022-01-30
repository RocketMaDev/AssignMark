package cn.rocket.assaignmark.core.exception;

/**
 * 在检查赋分表时，检查到赋分表非由本程序提取的赋分表时抛出的异常
 *
 * @author Rocket
 * @version 1.0.8
 * @see cn.rocket.assaignmark.core.AMFactory#extractTable(String)
 * @since 0.9.8
 */
public class InvalidTableException extends AssigningException {

    public InvalidTableException() {
    }

    public InvalidTableException(String message) {
        super(message);
    }

    public InvalidTableException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTableException(Throwable cause) {
        super(cause);
    }
}
