/*
 * Copyright 2014 Adam Dubiel.
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
import org.polyjdbc.core.type.ColumnType;

/**
 *
 * @author Adam Dubiel
 */
public class NumberAttribute extends Attribute {

    private int integerPrecision;

    private int decimalPrecision;

    public NumberAttribute(Dialect dialect, String name) {
        super(dialect, name);
    }

    @Override
    public ColumnType getType() {
        return ColumnType.NUMBER;
    }

    @Override
    protected String getTypeDefinition() {
        return dialect().types().number(integerPrecision, decimalPrecision);
    }

    void withIntegerPrecision(int integerPrecision) {
        this.integerPrecision = integerPrecision;
    }

    void withDecimalPrecision(int decimalPrecision) {
        this.decimalPrecision = decimalPrecision;
    }

    public static class NumberAttributeBuilder extends AttributeBuilder<NumberAttributeBuilder, NumberAttribute> {

        private NumberAttributeBuilder(Dialect dialect, String name, RelationBuilder parent) {
            super(new NumberAttribute(dialect, name), parent);
        }

        public static NumberAttributeBuilder number(Dialect dialect, String name, RelationBuilder parent) {
            return new NumberAttributeBuilder(dialect, name, parent);
        }

        @Override
        protected NumberAttributeBuilder self() {
            return this;
        }

        public NumberAttributeBuilder withIntegerPrecision(int integerPrecision) {
            attribute().withIntegerPrecision(integerPrecision);
            return self();
        }
        
        public NumberAttributeBuilder withDecimalPrecision(int decimalPrecision) {
            attribute().withDecimalPrecision(decimalPrecision);
            return self();
        }
    }
}
