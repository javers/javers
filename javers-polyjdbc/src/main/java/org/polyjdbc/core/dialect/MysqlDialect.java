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
public class MysqlDialect extends AbstractDialect {

    private MysqlDialectTypes types = new MysqlDialectTypes();

    private MysqlDialectConstraints constraints = new MysqlDialectConstraints();

    public String getCode() {
        return "MYSQL";
    }

    @Override
    public boolean supportsSequences() {
        return false;
    }

    public String nextFromSequence(String sequenceName) {
        throw new UnsupportedOperationException("MySQL dialect does not support sequences, check Dialce.supportsSequence() before trying to reach sequence!");
    }

    @Override
    public boolean supportsAttributeModifier(String modifier) {
        if (modifier.equals("AUTO_INCREMENT")) {
            return true;
        }
        return false;
    }

    @Override
    public DialectTypes types() {
        return types;
    }

    @Override
    public DialectConstraints constraints() {
        return constraints;
    }

    @Override
    public String createRelationDefaultOptions() {
        return "ENGINE = InnoDB";
    }
}
