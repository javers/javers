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
import org.polyjdbc.core.schema.model.FloatAttribute.FloatAttributeBuilder;
import org.polyjdbc.core.schema.model.NumberAttribute.NumberAttributeBuilder;

/**
 *
 * @author Adam Dubiel
 */
public final class RelationBuilder {

    private Relation relation;

    private Schema schema;

    private Heading heading;

    private Dialect dialect;

    private RelationBuilder(Dialect dialect, String name) {
        this.dialect = dialect;
        relation = new Relation(dialect, name);
        heading = new Heading(dialect);
    }

    private RelationBuilder(Schema schema, String name) {
        this(schema.getDialect(), name);
        this.schema = schema;
    }

    public static RelationBuilder relation(Dialect dialect, String name) {
        return new RelationBuilder(dialect, name);
    }

    public static RelationBuilder relation(Schema schema, String name) {
        return new RelationBuilder(schema, name);
    }

    public Relation build() {
        relation.withHeading(heading);
        if (schema != null) {
            schema.add(relation);
        }
        return relation;
    }

    RelationBuilder with(Attribute attribute) {
        heading.addAttribute(attribute);
        return this;
    }

    RelationBuilder with(Constraint constraint) {
        relation.addConstraint(constraint);
        return this;
    }

    public RelationBuilder withAttribute() {
        return this;
    }

    public LongAttributeBuilder longAttr(String name) {
        return LongAttributeBuilder.longAttr(dialect, name, this);
    }

    public IntegerAttributeBuilder integer(String name) {
        return IntegerAttributeBuilder.integer(dialect, name, this);
    }
    
    public FloatAttributeBuilder floatAttr(String name) {
        return FloatAttributeBuilder.floatAttr(dialect, name, this);
    }
    
    public NumberAttributeBuilder number(String name) {
        return NumberAttributeBuilder.number(dialect, name, this);
    }

    public StringAttributeBuilder string(String name) {
        return StringAttributeBuilder.string(dialect, name, this);
    }

    public TextAttributeBuilder text(String name) {
        return TextAttributeBuilder.text(dialect, name, this);
    }

    public CharAttributeBuilder character(String name) {
        return CharAttributeBuilder.character(dialect, name, this);
    }

    public BooleanAttributeBuilder booleanAttr(String name) {
        return BooleanAttributeBuilder.booleanAttr(dialect, name, this);
    }

    public DateAttributeBuilder date(String name) {
        return DateAttributeBuilder.date(dialect, name, this);
    }

    public TimestampAttributeBuilder timestamp(String name) {
        return TimestampAttributeBuilder.timestamp(dialect, name, this);
    }

    public RelationBuilder constrainedBy() {
        return this;
    }

    public PrimaryKeyConstraintBuilder primaryKey(String name) {
        return PrimaryKeyConstraintBuilder.primaryKey(dialect, name, this);
    }

    public ForeignKeyConstraintBuilder foreignKey(String name) {
        return ForeignKeyConstraintBuilder.foreignKey(dialect, name, this);
    }
}
