package ma.abid.customer_service.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
