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
import org.polyjdbc.core.type.ColumnType;
import org.polyjdbc.core.util.TypeUtil;

import java.util.Arrays;

/**
 *
 * @author Adam Dubiel
 */
public abstract class Attribute implements SchemaPart {

    static final int TO_STRING_LENGTH = 100;

    private Dialect dialect;

    private String name;

    private boolean unique;

    private boolean notNull;

    private Object defaultValue;

    private String[] additionalModifiers;

    Attribute(Dialect dialect, String name) {
        this.dialect = dialect;
        this.name = name;
    }

    public abstract ColumnType getType();

    protected abstract String getTypeDefinition();

    protected Dialect dialect() {
        return dialect;
    }

    @Override
    public String toString() {
        return ddl();
    }

    @Override
    public String ddl() {
        StringBuilder builder = new StringBuilder(TO_STRING_LENGTH);
        builder.append(name).append(" ").append(getTypeDefinition()).append(" ");
        if (unique) {
            builder.append("UNIQUE ");
        }
        if (notNull) {
            builder.append("NOT NULL ");
        }
        if (defaultValue != null) {
            appendDefaultValue(builder);
        }
        if (additionalModifiers != null) {
            for (String additionalModifier : additionalModifiers) {
                if (dialect.supportsAttributeModifier(additionalModifier)) {
                    builder.append(additionalModifier).append(" ");
                }
            }
        }
        return builder.toString().trim();
    }

    private void appendDefaultValue(StringBuilder builder) {
        boolean primitive = TypeUtil.isNonQuotablePrimitive(defaultValue);

        builder.append("DEFAULT ");
        if (!primitive) {
            builder.append("'");
        }
        builder.append(defaultValue);
        if (!primitive) {
            builder.append("'");
        }
        builder.append(" ");
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isUnique() {
        return unique;
    }

    void unique() {
        this.unique = true;
    }

    public boolean isNotNull() {
        return notNull;
    }

    void notNull() {
        this.notNull = true;
    }

    public String[] getAdditionalModifiers() {
        return Arrays.copyOf(additionalModifiers, additionalModifiers.length);
    }

    void withAdditionalModifiers(String... additionalModifiers) {
        this.additionalModifiers = Arrays.copyOf(additionalModifiers, additionalModifiers.length);
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    void withDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
