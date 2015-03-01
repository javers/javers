package org.javers.spring.auditable;

import org.aspectj.lang.JoinPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class AspectUtil {
    public static Iterable<Object> collectArguments(JoinPoint jp){
        List<Object> result = new ArrayList<>();

        for (Object arg: jp.getArgs()) {
            if (arg instanceof Collection) {
                result.addAll((Collection)arg);
            } else {
                result.add(arg);
            }
        }
        return result;
    }
}
