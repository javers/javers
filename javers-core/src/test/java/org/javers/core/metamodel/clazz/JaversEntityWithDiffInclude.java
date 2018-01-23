package org.javers.core.metamodel.clazz;

import org.javers.core.metamodel.annotation.DiffInclude;

@DiffInclude(value = {"includedField", "id"})
public class JaversEntityWithDiffInclude extends JaversEntity {
    private String includedField;
    private String ignoredField;
}
