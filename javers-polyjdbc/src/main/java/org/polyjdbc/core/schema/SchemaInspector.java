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

/**
 * Inspects database metadata. Should use one transaction per instance.
 *
 * @author Adam Dubiel
 */
public interface SchemaInspector extends Closeable {

    /**
     * Return if relation of given name exists in database.
     */
    boolean relationExists(String name);
    
    /**
     * Return if index of given name exists for given relation database.
     */
    boolean indexExists(String relationName, String indexName);

    /**
     * Close underlying transaction.
     */
    @Override
    void close();
}
