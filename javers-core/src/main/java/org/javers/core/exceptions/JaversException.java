package org.javers.core.exceptions;

/**
 * Basic javers exception.
 *
 *  @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
@SuppressWarnings("serial")
public class JaversException extends RuntimeException {
    public static final String BOOTSTRAP_ERROR = "JaVers bootstrap error - ";
    public static final String RUNTIME_ERROR = "JaVers runtime error - ";

    private JaversExceptionCode code;

    public JaversException(JaversExceptionCode code, Object... arguments) {
        super(code.name() + " " + String.format(code.getMessage(), arguments));
        this.code = code;
    }

    public JaversExceptionCode getCode() {
        return code;
    }
}
