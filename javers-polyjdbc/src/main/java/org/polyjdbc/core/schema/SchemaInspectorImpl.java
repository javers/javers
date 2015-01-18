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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import org.polyjdbc.core.exception.SchemaInspectionException;
import org.polyjdbc.core.transaction.Transaction;

class SchemaInspectorImpl implements SchemaInspector {

    private Locale locale;

    private final Transaction transaction;

    private DatabaseMetaData metadata;

    private String catalog;

    private String schema;

    SchemaInspectorImpl(Transaction transaction) {
        this(transaction, Locale.ENGLISH);
        extractMetadata(transaction);
    }

    SchemaInspectorImpl(Transaction transaction, Locale locale) {
        this.transaction = transaction;
        this.locale = locale;
    }

    private void extractMetadata(Transaction transaction) {
        try {
            Connection connection = transaction.getConnection();
            metadata = connection.getMetaData();
            catalog = connection.getCatalog();
        } catch (SQLException exception) {
            throw new SchemaInspectionException("METADATA_EXTRACTION_ERROR", "Failed to obtain metadata from connection.", exception);
        }
    }

    @Override
    public boolean relationExists(String name) {
        try {
            ResultSet resultSet = metadata.getTables(catalog, schema, convertCase(name), new String[]{"TABLE"});
            transaction.registerCursor(resultSet);

            return resultSet.next();
        } catch (SQLException exception) {
            throw new SchemaInspectionException("RELATION_LOOKUP_ERROR", "Failed to obtain relation list when looking for relation " + name, exception);
        }
    }

    @Override
    public boolean indexExists(String relationName, String indexName) {
        try {
            ResultSet resultSet = metadata.getIndexInfo(catalog, schema, convertCase(relationName), false, true);
            transaction.registerCursor(resultSet);

            String normalizedIndexName = convertCase(indexName);
            while(resultSet.next()) {
                if(resultSet.getString("INDEX_NAME").equals(normalizedIndexName)) {
                    return true;
                }
            }
            
            return false;
        } catch (SQLException exception) {
            throw new SchemaInspectionException("INDEX_LOOKUP_ERROR", "Failed to obtain relation index list when looking for indexes for relation " + relationName, exception);
        }
    }

    private String convertCase(String identifier) throws SQLException {
        if(metadata.storesLowerCaseIdentifiers()) {
            return identifier.toLowerCase(locale);
        }
        if(metadata.storesUpperCaseIdentifiers()) {
            return identifier.toUpperCase(locale);
        }

        return identifier;
    }

    @Override
    public void close() {
        transaction.commit();
        transaction.close();
    }
}
