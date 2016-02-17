package org.javers.core.json.typeadapter.util;

import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;

import java.net.URI;
import java.net.URL;
import java.util.Currency;

/**
 * @author bartosz.walacik
 */
public class UtilTypeAdapters implements JaversBuilderPlugin {

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        javersBuilder.registerValueTypeAdapter(new UUIDTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new FileTypeAdapter());
        javersBuilder.registerValue(Currency.class);
        javersBuilder.registerValue(URI.class);
        javersBuilder.registerValue(URL.class);
    }
}
