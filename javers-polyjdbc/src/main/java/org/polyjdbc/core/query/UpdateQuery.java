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
package org.polyjdbc.core.query;

import org.polyjdbc.core.util.StringBuilderUtil;

/**
 * Builds update query, use {@link QueryFactory#update() } to create new instance.
 *
 * <pre>
 * QueryFactory.update("test")
 *      .value("columnA", "A")
 *      .value("columnB", 2)
 *      .value("columnC", BigDecimal.valueOf(3.1415))
 *      .where("columnA > :maxId").withArgument("maxId", 10000);
 * </pre>
 *
 * @author Adam Dubiel
 */
public class UpdateQuery {

    private static final int VALUES_LENGTH = 50;

    private Query query;

    private StringBuilder values = new StringBuilder(VALUES_LENGTH);

    private StringBuilder where = new StringBuilder(VALUES_LENGTH);

    UpdateQuery(String what) {
        this.query = new Query();
        this.query.append("UPDATE ").append(what).append(" SET ");
    }

    Query build() {
        StringBuilderUtil.deleteLastCharacters(values, 2);
        query.append(values.toString()).append(" ").append(where.toString());
        query.compile();

        return query;
    }

    /**
     * Set column to update. Object is automatically translated
     * onto matching JDBC type.
     *
     * @see org.polyjdbc.core.type.ColumnType
     */
    public UpdateQuery set(String fieldName, Object value) {
        String updatedFieldName = "update_" + fieldName;
        values.append(fieldName).append(" = :").append(updatedFieldName).append(", ");
        query.setArgument(updatedFieldName, value);
        return this;
    }

    /**
     * Adds <b>WHERE</b> clause with given conditions. Words prefixed with
     * <b>:</b> are replaced into value placeholders, which can be populated
     * using {@link #withArgument(java.lang.String, java.lang.Object) } method.
     * <pre>
     * .where("value > :value").withArgument("value", 10);
     * </pre>
     */
    public UpdateQuery where(String conditions) {
        where.append("WHERE ").append(conditions);
        return this;
    }

    /**
     * Sets value for placeholder defined in query. Placeholder name should
     * not start with <b>:</b>, it is stripped off. Based on passed object type,
     * appropriate JDBC type is chosen.
     *
     * @see org.polyjdbc.core.type.ColumnType
     *
     */
    public UpdateQuery withArgument(String argumentName, Object object) {
        query.setArgument(argumentName, object);
        return this;
    }
}
