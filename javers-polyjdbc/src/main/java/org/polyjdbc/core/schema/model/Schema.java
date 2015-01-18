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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Adam Dubiel
 */
public final class Schema {

    private final Dialect dialect;

    private final List<SchemaEntity> entities = new ArrayList<SchemaEntity>();

    private final List<Sequence> sequences = new ArrayList<Sequence>();

    public Schema(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public List<SchemaEntity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public List<Sequence> getSequences() {
        return Collections.unmodifiableList(sequences);
    }

    void add(SchemaEntity entity) {
        entities.add(entity);
    }

    void add(Sequence sequence) {
        sequences.add(sequence);
    }

    public RelationBuilder addRelation(String name) {
        return RelationBuilder.relation(this, name);
    }

    public IndexBuilder addIndex(String name) {
        return IndexBuilder.index(this, name);
    }

    public SequenceBuilder addSequence(String name) {
        return SequenceBuilder.sequence(this, name);
    }
}
