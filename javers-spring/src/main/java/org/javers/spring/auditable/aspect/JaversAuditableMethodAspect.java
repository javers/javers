package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;

/**
 * @author bartosz walacik
 */
@Aspect
public class JaversAuditableMethodAspect {

    private JaversCommitAdvice javersCommitAdvice;

    public JaversAuditableMethodAspect(Javers javers, AuthorProvider authorProvider) {
        this.javersCommitAdvice = new JaversCommitAdvice(javers, authorProvider);
    }

    @After("@annotation(org.javers.spring.annotation.JaversAuditable)")
    public void myAdvice(JoinPoint pjp) throws Throwable{
        javersCommitAdvice.commitMethodArguments(pjp);
    }
}
