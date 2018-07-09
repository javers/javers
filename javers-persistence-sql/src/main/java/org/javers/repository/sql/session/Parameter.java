package org.javers.repository.sql.session;

import org.javers.common.validation.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Parameter {
    private final String name;
    private final Object value;

    Parameter(String name, Object value) {
        Validate.argumentIsNotNull(name);
        this.name = name;
        this.value = value;
    }

    String getName() {
        return name;
    }

    Object getValue() {
        return value;
    }

    static class ParametersBuilder {
        private List<Parameter> list = new ArrayList<>();

        static ParametersBuilder parameters() {
            return new ParametersBuilder();
        }

        ParametersBuilder add(String name, Object value) {
            list.add(new Parameter(name, value));
            return this;
        }

        List<Parameter> build() {
            return Collections.unmodifiableList(list);
        }
    }

}
