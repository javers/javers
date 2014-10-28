package org.javers.spring;

import org.javers.common.collections.Lists;
import org.javers.core.Javers;
import org.javers.spring.aspect.DeleteAspect;
import org.javers.spring.aspect.SaveAspect;
import org.javers.spring.aspect.UpdateAscpect;

public class AspectFactory {

    private Javers javers;

    public AspectFactory(Javers javers) {
        this.javers = javers;
    }

    public Iterable<? extends Object> create() {
        return Lists.immutableListOf(new SaveAspect(javers), new DeleteAspect(javers), new UpdateAscpect(javers));
    }
}
