package org.javers.core;

import static org.fest.assertions.api.Assertions.assertThat;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.DiffFactory;
import org.javers.core.pico.JaversContainerFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Test;
import org.picocontainer.PicoContainer;

/**
 * @author bartosz walacik
 */
public class JaversContainerFactoryTest {

    @Test
    public void shouldCreateMultipleJaversContainers() {
        JaversBuilder builder1 = JaversBuilder.javers();
        JaversBuilder builder2 = JaversBuilder.javers();

        //when
        builder1.build();
        builder2.build();

        //then
          assertThat(builder1.getContainerComponent(EntityManager.class))
        .isNotSameAs(builder2.getContainerComponent(EntityManager.class));
    }

    @Test
    public void shouldContainRequiredJaversBeans(){
        //given
        JaversBuilder builder = JaversBuilder.javers();

        //when
        builder.build();

        //then
        assertThat(builder.getContainerComponent(Javers.class)).isNotNull();
        assertThat(builder.getContainerComponent(EntityManager.class)).isNotNull();
        assertThat(builder.getContainerComponent(TypeMapper.class)).isNotNull();
        assertThat(builder.getContainerComponent(DiffFactory.class)).isNotNull();
    }

    @Test
    public void shouldContainSingletons() {
        //given
        JaversBuilder builder = JaversBuilder.javers();
        builder.build();

        //when
        Javers javers =          builder.getContainerComponent(Javers.class);
        Javers javersSecondRef = builder.getContainerComponent(Javers.class);

        //then
        assertThat(javers).isSameAs(javersSecondRef);
    }
}
