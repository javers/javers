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
package org.polyjdbc.core.schema;

import java.io.Closeable;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.schema.model.SchemaEntity;

/**
 * Performs schema alteration. Should use one transaction per instance.
 *
 * @author Adam Dubiel
 */
public interface SchemaManager extends Closeable {

    /**
     * Create schema matching provided schema model.
     */
    void create(Schema schema);

    /**
     * Create single entity (relation, sequence, index).
     */
    void create(SchemaEntity entity);

    /**
     * Drop any entities defined inside schema.
     */
    void drop(Schema schema);

    /**
     * Drop single entity.
     */
    void drop(SchemaEntity entity);

    /**
     * Run custom DDL query.
     */
    void ddl(DDLQuery ddlQuery);

    /**
     * Close underlying transaction.
     */
    void close();
}
