package org.javers.spring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;

@Aspect
public class SaveAspect {

    private Javers javers;

    public SaveAspect(Javers javers) {
        this.javers = javers;
    }

    @Around("execution(* *.save(..))")
    public void update(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();

        for (Object arg: joinPoint.getArgs()) {
          javers.commit("author", arg);
        }
    }
}
