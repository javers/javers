package pl.edu.icm.crmanager.exception;

/**
 * @author bart
 */
public class CrmRuntimeException extends RuntimeException{
    public CrmRuntimeException(String message, Exception exception) {
        super (message, exception);
    }
    
    public CrmRuntimeException(String message) {
        super (message);
    }
}
