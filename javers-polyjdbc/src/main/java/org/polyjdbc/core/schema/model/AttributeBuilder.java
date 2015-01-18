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

/**
 *
 * @author Adam Dubiel
 */
public abstract class AttributeBuilder<T, A extends Attribute> {

    private A attribute;

    private RelationBuilder parent;

    protected AttributeBuilder(A attribute) {
        this.attribute = attribute;
    }

    protected AttributeBuilder(A attribute, RelationBuilder parent) {
        this.attribute = attribute;
        this.parent = parent;
    }

    protected A attribute() {
        return attribute;
    }

    protected abstract T self();

    public RelationBuilder and() {
        parent.with(attribute);
        return parent;
    }

    public T unique() {
        attribute.unique();
        return self();
    }

    public T notNull() {
        attribute.notNull();
        return self();
    }

    public T withAdditionalModifiers(String... additionalModifiers) {
        attribute.withAdditionalModifiers(additionalModifiers);
        return self();
    }

    public T withDefaultValue(Object defaultValue) {
        attribute.withDefaultValue(defaultValue);
        return self();
    }
}
