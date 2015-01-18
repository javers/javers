/*
 * Copyright 2013 Adam Dubiel.
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
import org.polyjdbc.core.type.ColumnType;

/**
 *
 * @author Adam Dubiel
 */
public class TextAttribute extends Attribute {

    public TextAttribute(Dialect dialect, String name) {
        super(dialect, name);
    }

    @Override
    public ColumnType getType() {
        return ColumnType.TEXT;
    }

    @Override
    protected String getTypeDefinition() {
        return dialect().types().text();
    }
}
