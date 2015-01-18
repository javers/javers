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
import org.polyjdbc.core.util.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Adam Dubiel
 */
public class Heading {

    private Dialect dialect;

    private List<Attribute> attributes = new LinkedList<Attribute>();

    Heading(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect getDialect() {
        return dialect;
    }

    @Override
    public String toString() {
        return StringUtils.concatenate(",\n", attributes.toArray());
    }

    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }
}
