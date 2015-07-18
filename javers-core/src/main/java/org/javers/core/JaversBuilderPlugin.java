package org.javers.core;

/**
 * @author bartosz.walacik
 */
public interface JaversBuilderPlugin {
    void beforeAssemble(JaversBuilder javersBuilder);
}
