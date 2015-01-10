package org.javers.spring.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.javers.core.Javers;

/**
 * @author Pawel Szymczyk
 */
public class JaversAdvice implements MethodInterceptor {

    private final Javers javers;
    private final String author;

    public JaversAdvice(Javers javers, String author) {
        this.javers = javers;
        this.author = author;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object retVal = invocation.proceed();

        for (Object arg: invocation.getArguments()) {
            if (arg instanceof Iterable) {
                commitAllEntities((Iterable) arg);
            } else {
                javers.commit(author, arg);
            }
        }

        return retVal;
    }

    private void commitAllEntities(Iterable iterable) {
        for (Object entity: iterable) {
            javers.commit(author, entity);
        }
    }
}
