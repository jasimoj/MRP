package common.exception;

public class MissingRequiredFieldsException extends RuntimeException {
    public MissingRequiredFieldsException() {
        super();
    }
    public MissingRequiredFieldsException(String message) {
        super(message);
    }

    public MissingRequiredFieldsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingRequiredFieldsException(Throwable cause) {
        super(cause);
    }

    public MissingRequiredFieldsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
