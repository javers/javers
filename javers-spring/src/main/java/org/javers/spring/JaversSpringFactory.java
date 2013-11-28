package org.javers.spring;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.beans.factory.FactoryBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pawel Cierpiatka
 */
public class JaversSpringFactory implements FactoryBean<Javers> {

    private List<Class> entityClasses = new ArrayList<>();

    private List<Class> valueObject = new ArrayList<>();

    private Map<Class, String> describedEntityClasses = new HashMap<>();

    @Override
    public Javers getObject() throws Exception {

        JaversBuilder javersBuilder = JaversBuilder.javers();

        /*
        WAITING FOR JAVERS CREW :)
        for (Map.Entry<Class, String> entrie : customsId.entrySet()) {
            javersBuilder.registerEntityWithCustomId(entrie.getKey(), entrie.getValue());
        }*/

        for(Class clazz : entityClasses) {
            javersBuilder.registerEntity(clazz);
        }

        for(Class clazz : valueObject) {
            javersBuilder.registerValueObject(clazz);
        }

        return javersBuilder.build();
    }


    @Override
    public Class<?> getObjectType() {
        return Javers.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setEntityClasses(List<Class> entityClasses) {
        this.entityClasses = entityClasses;
    }

    public void setValueObject(List<Class> valueObject) {
        this.valueObject = valueObject;
    }

    public void setDescribedEntityClasses(Map<Class, String> describedEntityClasses) {
        this.describedEntityClasses = describedEntityClasses;
    }
}
