package org.javers.core.model;

import org.javers.common.pico.JaversModule;
import org.javers.core.model.DummyJaversBean;

import java.util.Arrays;
import java.util.Collection;

/**
* @author bartosz walacik
*/
public class DummyJaversModule implements JaversModule {

    @Override
    public Collection<Class> getModuleComponents() {
        return (Collection) Arrays.asList(DummyJaversBean.class);
    }
}
