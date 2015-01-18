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
package org.polyjdbc.core.dialect;

/**
 *
 * @author Adam Dubiel
 */
public interface DialectConstraints {

    String primaryKey(String name, String[] targetAttributes);

    String foreignKey(String name, String targetAttribute, String targetRelation, String targetRelationAttribute);

    String dropIndex(String name, String targetRelationName);
}
