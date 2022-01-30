package cn.rocket.assaignmark.core.exception;

import cn.rocket.assaignmark.core.MarkTable;

/**
 * 当分数表中没有可赋分的工作表时抛出的异常
 *
 * @author Rocket
 * @version 1.0.8
 * @see MarkTable#checkAndLoad()
 * @since 0.9.8
 */
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
