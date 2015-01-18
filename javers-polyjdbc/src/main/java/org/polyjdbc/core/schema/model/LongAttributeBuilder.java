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
public final class LongAttributeBuilder extends AttributeBuilder<LongAttributeBuilder, LongAttribute> {

    private LongAttributeBuilder(Dialect dialect, String name, RelationBuilder parent) {
        super(new LongAttribute(dialect, name), parent);
    }

    public static LongAttributeBuilder longAttr(Dialect dialect, String name, RelationBuilder parent) {
        return new LongAttributeBuilder(dialect, name, parent);
    }

    @Override
    protected LongAttributeBuilder self() {
        return this;
    }

    public LongAttributeBuilder withIntegerPrecision(int integerPrecision) {
        attribute().withIntegerPrecision(integerPrecision);
        return self();
    }
}
