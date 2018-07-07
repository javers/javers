package org.javers.repository.sql.session;

import org.javers.common.validation.Validate;

public class Query {

    public static class Parameter {
        private final String name;
        private final Object value;

        public Parameter(String name, Object value) {
            Validate.argumentIsNotNull(name);
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }
}
