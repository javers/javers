package org.javers.spring;

import org.javers.common.collections.Lists;
import org.javers.core.Javers;
import org.javers.spring.aspect.DeleteAspect;
import org.javers.spring.aspect.SaveAspect;
import org.javers.spring.aspect.UpdateAscpect;

import java.util.List;

public class AspectFactory {

    private List<Object> aspects;

    public AspectFactory(Javers javers) {
        aspects = Lists.immutableListOf(new SaveAspect(javers), new DeleteAspect(javers), new UpdateAscpect(javers));
    }

    public Iterable<? extends Object> create() {
        return aspects;
    }
}
