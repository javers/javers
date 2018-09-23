package org.javers.repository.sql.session;

import org.javers.repository.sql.session.Parameter.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParametersBuilder {
    private List<Parameter> list = new ArrayList<>();

    public static ParametersBuilder parameters() {
        return new ParametersBuilder();
    }

    public ParametersBuilder add(String name, String value) {
        list.add(new StringParameter(name, value));
        return this;
    }

    public ParametersBuilder add(String name, Integer value) {
        list.add(new IntParameter(name, value));
        return this;
    }

    public ParametersBuilder add(String name, Long value) {
        list.add(new LongParameter(name, value));
        return this;
    }

    public List<Parameter> build() {
        return Collections.unmodifiableList(list);
    }
}
