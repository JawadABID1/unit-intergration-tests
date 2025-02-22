package ma.abid.customer_service.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message){
        super(message);
    }
}
