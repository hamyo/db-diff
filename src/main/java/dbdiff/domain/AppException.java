package dbdiff.domain;

public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
