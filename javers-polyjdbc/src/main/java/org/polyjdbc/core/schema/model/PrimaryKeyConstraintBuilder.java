/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.polyjdbc.core.schema.model;

import org.polyjdbc.core.dialect.Dialect;

/**
 *
 * @author Adam Dubiel
 */
public final class PrimaryKeyConstraintBuilder {

    private RelationBuilder parent;

    private PrimaryKeyConstraint constraint;

    private PrimaryKeyConstraintBuilder(Dialect dialect, String name, RelationBuilder parent) {
        this.constraint = new PrimaryKeyConstraint(dialect, name);
        this.parent = parent;
    }

    public static PrimaryKeyConstraintBuilder primaryKey(Dialect dialect, String name, RelationBuilder parent) {
        return new PrimaryKeyConstraintBuilder(dialect, name, parent);
    }

    public RelationBuilder and() {
        parent.with(constraint);
        return parent;
    }

    public PrimaryKeyConstraintBuilder using(String... attributes) {
        constraint.withTargetAttributes(attributes);
        return this;
    }
}
