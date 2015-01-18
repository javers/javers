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

import java.util.Arrays;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.util.StringUtils;

/**
 *
 * @author Adam Dubiel
 */
public class Index implements SchemaEntity {

    private static final int DDL_LENGTH = 50;

    private Dialect dialect;

    private String name;

    private String targetRelation;

    private String[] targetAttributes;

    Index(Dialect dialect, String name) {
        this.dialect = dialect;
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ddl();
    }

    @Override
    public String ddl() {
        StringBuilder builder = new StringBuilder(DDL_LENGTH);
        builder.append("CREATE INDEX ").append(name).append(" ON ").append(targetRelation).append("(")
                .append(StringUtils.concatenate(",", (Object[]) targetAttributes)).append(")");
        return builder.toString();
    }

    @Override
    public String dropDDL() {
        return dialect.constraints().dropIndex(name, targetRelation);
    }

    public String getTargetRelation() {
        return targetRelation;
    }

    void withTargetRelation(String targetRelation) {
        this.targetRelation = targetRelation;
    }

    public String[] getTargetAttributes() {
        return Arrays.copyOf(targetAttributes, targetAttributes.length);
    }

    void withTargetAttributes(String[] targetAttributes) {
        this.targetAttributes = Arrays.copyOf(targetAttributes, targetAttributes.length);
    }
}
