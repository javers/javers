package pl.edu.icm.sedno.common.dao;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super (message);
    }
}
