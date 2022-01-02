package cn.rocket.assaignmark.core.exception;

/**
 * 在检查分数表/赋分表时，如果表格不符合规范时抛出的异常
 *
 * @author Rocket
 * @version 0.9.8
 */
public class IncorrectSheetException extends AssigningException {
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
