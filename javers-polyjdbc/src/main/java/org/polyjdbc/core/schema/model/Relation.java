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
import org.polyjdbc.core.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author Adam Dubiel
 */
public class Relation implements SchemaEntity {

    private static final int TO_STRING_LENGTH_BASE = 30;

    private String name;

    private Dialect dialect;

    private Heading heading;

    private Collection<Constraint> constraints = new LinkedList<Constraint>();

    Relation(Dialect dialect, String name) {
        this.dialect = dialect;
        this.name = name;
    }

    @Override
    public String toString() {
        return ddl();
    }

    @Override
    public String ddl() {
        String headingDDL = heading.toString();
        String constraintsDDL = StringUtils.concatenate(",\n", constraints.toArray());
        StringBuilder builder = new StringBuilder(TO_STRING_LENGTH_BASE + headingDDL.length() + constraintsDDL.length());

        builder.append("CREATE TABLE ").append(name).append(" (\n")
                .append(headingDDL);
        if (constraintsDDL.length() > 0) {
            builder.append(",\n");
        }
        builder.append(constraintsDDL).append("\n) ")
                .append(dialect.createRelationDefaultOptions());

        return builder.toString();
    }

    @Override
    public String dropDDL() {
        return "DROP TABLE " + name;
    }

    public Dialect getDialect() {
        return dialect;
    }

    @Override
    public String getName() {
        return name;
    }

    public Heading getHeading() {
        return heading;
    }

    void withHeading(Heading heading) {
        this.heading = heading;
    }

    public Collection<Constraint> getConstraints() {
        return Collections.unmodifiableCollection(constraints);
    }

    void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }
}
