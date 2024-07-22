package org.javers.spring.boot.redis;

import java.util.Optional;

import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.graph.ObjectAccessProxy;

public class RedisObjectAccessHook <T> implements ObjectAccessHook<T> {

  @Override
  public Optional<ObjectAccessProxy<T>> createAccessor(final T entity) {
    return Optional.empty();
  }

}
