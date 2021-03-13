package org.javers.spring;

import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.javers.core.json.JsonAdvancedTypeAdapter;
import org.javers.core.json.JsonTypeAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RegisterJsonTypeAdaptersPlugin implements JaversBuilderPlugin {
    private final List<JsonTypeAdapter<?>> typeAdapters;
    private final List<JsonAdvancedTypeAdapter<?>> advancedTypeAdapters;

    public RegisterJsonTypeAdaptersPlugin(Optional<List<JsonTypeAdapter<?>>> typeAdapters,
                                          Optional<List<JsonAdvancedTypeAdapter<?>>> advancedTypeAdapters) {
        this.typeAdapters = typeAdapters.orElse(Collections.emptyList());
        this.advancedTypeAdapters = advancedTypeAdapters.orElse(Collections.emptyList());
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        typeAdapters.forEach(javersBuilder::registerValueTypeAdapter);
        advancedTypeAdapters.forEach(javersBuilder::registerJsonAdvancedTypeAdapter);
    }
}
