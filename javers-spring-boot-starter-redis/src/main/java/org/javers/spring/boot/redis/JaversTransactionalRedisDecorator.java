package org.javers.spring.boot.redis;

import org.javers.core.Javers;
import org.javers.spring.transactions.JaversTransactionalDecorator;

public class JaversTransactionalRedisDecorator extends JaversTransactionalDecorator {

    public JaversTransactionalRedisDecorator(final Javers delegate) {
        super(delegate);
    }

}
