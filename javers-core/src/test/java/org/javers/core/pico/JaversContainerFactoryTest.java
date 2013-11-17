package org.javers.core.pico;

import com.google.common.collect.ImmutableList;
import org.javers.core.Javers;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.MappingStyle;
import org.javers.model.mapping.*;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Test;
import org.picocontainer.PicoContainer;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author bartosz walacik
 */
public class JaversContainerFactoryTest {

    @Test
    public void shouldCreateMultipleJaversContainers() {
        //when
        PicoContainer container1 = JaversContainerFactory.createDefaultCore();
        PicoContainer container2 = JaversContainerFactory.createDefaultCore();

        //then
        assertThat(container1).isNotSameAs(container2);
        assertThat(container1.getComponent(EntityManager.class))
      .isNotSameAs(container2.getComponent(EntityManager.class));
    }

    @Test
    public void shouldContainRequiredJaversBeans(){
        //when
        PicoContainer container = JaversContainerFactory.createDefaultCore();

        //then
        assertThat(container.getComponent(Javers.class)).isNotNull();
        assertThat(container.getComponent(EntityManager.class)).isNotNull();
        assertThat(container.getComponent(TypeMapper.class)).isNotNull();
    }

    @Test
    public void shouldContainSingletons() {
        //given
        PicoContainer container = JaversContainerFactory.createDefaultCore();

        //when
        Javers javers = container.getComponent(Javers.class);
        Javers javersSecondRef = container.getComponent(Javers.class);

        //then
        assertThat(javers).isSameAs(javersSecondRef);
    }
}
