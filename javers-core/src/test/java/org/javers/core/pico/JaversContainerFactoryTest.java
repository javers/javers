package org.javers.core.pico;

import org.javers.core.Javers;
import org.javers.core.MappingStyle;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.PropertyScanner;
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
        PicoContainer container1 = JaversContainerFactory.create(MappingStyle.BEAN);
        PicoContainer container2 = JaversContainerFactory.create(MappingStyle.BEAN);

        //then
        assertThat(container1).isNotSameAs(container2);
        assertThat(container1.getComponent(EntityManager.class))
      .isNotSameAs(container2.getComponent(EntityManager.class));
    }

    @Test
    public void shouldContainRequiredJaversBeans(){
        //given
        PicoContainer container = JaversContainerFactory.create(MappingStyle.BEAN);

        //then
        assertThat(container.getComponent(Javers.class)).isNotNull();
        assertThat(container.getComponent(EntityManager.class)).isNotNull();
        assertThat(container.getComponent(TypeMapper.class)).isNotNull();
    }

    @Test
    public void shouldContainSingletons() {
        //given
        PicoContainer container = JaversContainerFactory.create(MappingStyle.BEAN);

        //when
        Javers javers = container.getComponent(Javers.class);
        Javers javersSecondRef = container.getComponent(Javers.class);

        //then
        assertThat(javers).isSameAs(javersSecondRef);
    }

    @Test
    public void shouldContainFieldBasedPropertyScannerWhenFieldStyle(){
        //given
        PicoContainer container = JaversContainerFactory.create(MappingStyle.FIELD);

        //then
        assertThat(container.getComponent(PropertyScanner.class)).isInstanceOf(FieldBasedPropertyScanner.class);
    }

    @Test
    public void shouldContainBeanBasedPropertyScannerWhenBeanStyle(){
        //given
        PicoContainer container = JaversContainerFactory.create(MappingStyle.BEAN);

        //then
        assertThat(container.getComponent(PropertyScanner.class)).isInstanceOf(BeanBasedPropertyScanner.class);
    }


    @Test
    public void shouldNotContainFieldBasedPropertyScannerWhenBeanStyle(){
        //given
        PicoContainer container = JaversContainerFactory.create(MappingStyle.BEAN);

        //then
        assertThat(container.getComponent(FieldBasedPropertyScanner.class)).isNull();
    }

    @Test
    public void shouldNotContainBeanBasedPropertyScannerWhenFieldStyle(){
        //given
        PicoContainer container = JaversContainerFactory.create(MappingStyle.FIELD);

        //then
        assertThat(container.getComponent(BeanBasedPropertyScanner.class)).isNull();
    }
}
