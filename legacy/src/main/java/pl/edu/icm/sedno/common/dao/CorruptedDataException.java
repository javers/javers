package pl.edu.icm.sedno.common.dao;

/**
 * @author bart
 */
public class CorruptedDataException extends RuntimeException {
    public CorruptedDataException(String message) {
        super (message);
    }
}
