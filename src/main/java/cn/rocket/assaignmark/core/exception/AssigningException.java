package cn.rocket.assaignmark.core.exception;

/**
 * 赋分异常，在出现已定义的异常时抛出。会自动关闭<code>Notifier</code>的线程池
 *
 * @author Rocket
 * @version 0.9.8
 * @see cn.rocket.assaignmark.core.event.Notifier
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
