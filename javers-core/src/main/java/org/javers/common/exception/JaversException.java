package org.javers.common.exception;

import static org.javers.common.exception.JaversExceptionCode.RUNTIME_EXCEPTION;

/**
 *  @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
@SuppressWarnings("serial")
public class JaversException extends RuntimeException {
    public static final String BOOTSTRAP_ERROR = "JaVers bootstrap error - ";
    public static final String RUNTIME_ERROR = "JaVers runtime error - ";

    private final JaversExceptionCode code;

    public JaversException(Throwable throwable) {
        super(RUNTIME_EXCEPTION.getMessage(), throwable);
        this.code = RUNTIME_EXCEPTION;
    }

    public JaversException(JaversExceptionCode code, Object... arguments) {
        super(code.name() + " " + String.format(code.getMessage(), arguments));
        this.code = code;
    }

    public JaversExceptionCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+": "+ getMessage();
    }
}
