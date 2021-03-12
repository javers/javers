package org.javers.core;

import org.javers.core.json.JsonAdvancedTypeAdapter;
import org.javers.core.json.JsonTypeAdapter;

import java.util.Collections;
import java.util.List;

public class RegisterTypeAdaptersPlugin implements JaversBuilderPlugin {
    private final List<JsonTypeAdapter<?>> typeAdapters;
    private final List<JsonAdvancedTypeAdapter<?>> advancedTypeAdapters;

    public RegisterTypeAdaptersPlugin(List<JsonTypeAdapter<?>> typeAdapters,
                                      List<JsonAdvancedTypeAdapter<?>> advancedTypeAdapters) {
        this.typeAdapters = typeAdapters == null ? Collections.emptyList() : typeAdapters;
        this.advancedTypeAdapters = advancedTypeAdapters == null ? Collections.emptyList() : advancedTypeAdapters;
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        typeAdapters.forEach(javersBuilder::registerValueTypeAdapter);
        advancedTypeAdapters.forEach(javersBuilder::registerJsonAdvancedTypeAdapter);
    }
}
