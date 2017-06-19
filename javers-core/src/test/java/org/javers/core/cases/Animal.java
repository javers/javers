/*
* Copyright (c) 2017 Brevan Howard Limited. All rights reserved.
*/
package org.javers.core.cases;

import com.google.auto.value.AutoValue;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

/**
 * @author jfreedman
 */
@TypeName("Animal")
@AutoValue
public abstract class Animal {
    public abstract String getName();

    @Id
    public String getJaversId() {
        return getName();
    }

    public abstract int getNumberOfLegs();

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