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

import org.polyjdbc.core.exception.SchemaManagerException;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.schema.model.SchemaEntity;
import org.polyjdbc.core.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Adam Dubiel
 */
class SchemaManagerImpl implements SchemaManager {

    private static final Logger logger = LoggerFactory.getLogger(SchemaManagerImpl.class);

    private final Transaction transaction;

    SchemaManagerImpl(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void create(Schema schema) {
        List<SchemaEntity> entitiesToCreate = new ArrayList<SchemaEntity>(schema.getEntities());
        if (schema.getDialect().supportsSequences()) {
            entitiesToCreate.addAll(schema.getSequences());
        }

        String ddlText;
        for (SchemaEntity entity : entitiesToCreate) {
            ddlText = entity.ddl();
            logger.info("creating entity with name {} using ddl:\n{}", entity.getName(), ddlText);
            ddl(DDLQuery.ddl(ddlText));
        }
    }

    @Override
    public void create(SchemaEntity entity) {
        ddl(DDLQuery.ddl(entity.ddl()));
    }

    @Override
    public void drop(Schema schema) {
        List<SchemaEntity> entitiesToDrop = new ArrayList<SchemaEntity>(schema.getEntities());
        Collections.reverse(entitiesToDrop);

        if (schema.getDialect().supportsSequences()) {
            entitiesToDrop.addAll(schema.getSequences());
        }

        String ddlText;
        for (SchemaEntity entity : entitiesToDrop) {
            ddlText = entity.dropDDL();
            logger.info("dropping entity with name {} using ddl:\n{}", entity.getName(), ddlText);
            ddl(DDLQuery.ddl(ddlText));
        }
    }

    @Override
    public void drop(SchemaEntity entity) {
        ddl(DDLQuery.ddl(entity.dropDDL()));
    }

    @Override
    public void ddl(DDLQuery ddlQuery) {
        String textQuery = ddlQuery.build();
        try {
            Statement statement = transaction.createStatement();
            statement.execute(textQuery);
        } catch (SQLException exception) {
            transaction.rollback();
            throw new SchemaManagerException("DDL_ERROR", String.format("Failed to run DDL:%n%s", textQuery), exception);
        }
    }

    @Override
    public void close() {
        transaction.commit();
        transaction.close();
    }
}
