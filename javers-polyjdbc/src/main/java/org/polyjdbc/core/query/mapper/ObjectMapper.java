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
package org.polyjdbc.core.query.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps contents of result set onto Java object.
 * For simple usages consider using {@link StringMapper} or {@link EmptyMapper}.
 *
 * @author Adam Dubiel
 */
public interface ObjectMapper<T> {

    /**
     * Create bean using contents of result set and return it.
     */
    T createObject(ResultSet resultSet) throws SQLException;

}
