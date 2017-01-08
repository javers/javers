package org.javers.core.json.typeadapter.util;

import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.JaversBuilder;

import java.net.URI;
import java.net.URL;
import java.util.Currency;

/**
 * @author bartosz.walacik
 */
public class UtilTypeAdapters extends ConditionalTypesPlugin {

    @Override
    public boolean shouldBeActivated() {
        return true;
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        javersBuilder.registerValueTypeAdapter(new UUIDTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new FileTypeAdapter());
        javersBuilder.registerValue(Currency.class);
        javersBuilder.registerValue(URI.class);
        javersBuilder.registerValue(URL.class);
    }
}
