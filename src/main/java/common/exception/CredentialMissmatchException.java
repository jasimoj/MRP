package common.exception;

public class CredentialMissmatchException extends RuntimeException{
    public CredentialMissmatchException() {
    }

    public CredentialMissmatchException(String message) {
        super(message);
    }

    public CredentialMissmatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialMissmatchException(Throwable cause) {
        super(cause);
    }

    public CredentialMissmatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
