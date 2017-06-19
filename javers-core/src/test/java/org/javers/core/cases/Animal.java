/*
* Copyright (c) 2017 Brevan Howard Limited. All rights reserved.
*/
package org.javers.core.cases;

import com.google.auto.value.AutoValue;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jfreedman
 */
@TypeName("Animal")
@AutoValue
public abstract class Animal {
    @Id
    public abstract String name();

    public abstract int numberOfLegs();

    public static Builder builder() {
        return new AutoValue_Animal.Builder();
    }

    @AutoValue.Builder
    public abstract  static class Builder {
        public abstract Builder setName(final String name);

        public abstract Builder setNumberOfLegs(final int numberOfLegs);

        public abstract Animal build();
    }
}