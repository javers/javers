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
package org.polyjdbc.core.type;

import org.polyjdbc.core.exception.UnknownColumnTypeException;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;

/**
 *
 * @author Adam Dubiel
 */
public enum ColumnType {

    STRING(String.class, Types.VARCHAR),
    TEXT(String.class, Types.VARCHAR),
    INT(Integer.class, Types.INTEGER),
    LONG(Long.class, Types.BIGINT),
    FLOAT(Float.class, Types.FLOAT),
    CHAR(Character.class, Types.CHAR),
    BOOLEAN(Boolean.class, Types.BOOLEAN),
    DATE(Date.class, Types.DATE),
    NUMBER(BigDecimal.class, Types.NUMERIC),
    SQLTIMESTAMP(java.sql.Timestamp.class, Types.DATE),
    TIMESTAMP(Timestamp.class, Types.TIMESTAMP);

    private int sqlType;

    private Class<?> matchingClass;

    private ColumnType(Class<?> matchingClass, int sqlType) {
        this.matchingClass = matchingClass;
        this.sqlType = sqlType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public static ColumnType forClass(Class<?> objectClass) {
        for(ColumnType type : ColumnType.values()) {
            if(type.matchingClass.isAssignableFrom(objectClass)) {
                return type;
            }
        }
        throw new UnknownColumnTypeException("Could not find column type matching class " + objectClass.getCanonicalName() + ". "
                + "Make sure class is assignable from any of supported classes." );
    }

    public static ColumnType forObject(Object object) {
        return ColumnType.forClass(object.getClass());
    }
}
