package org.javers.core.pico;

import static org.fest.assertions.api.Assertions.assertThat;

import org.javers.core.Javers;
import org.javers.core.JaversConfiguration;
import org.javers.core.MappingStyle;
import org.javers.core.diff.DiffFactory;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.PropertyScanner;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Test;
import org.picocontainer.PicoContainer;

/**
 * @author bartosz walacik
 */
public class JaversContainerFactoryTest {

    @Test
    public void JaversContainerShouldLoadDefaultPropertiesFile() {
        //when
        PicoContainer javersContainer = JaversContainerFactory.create();

        //then
        assertThat(javersContainer.getComponent(PropertyScanner.class)).isInstanceOf(BeanBasedPropertyScanner.class);
    }

    @Test
    public void shouldCreateMultipleJaversContainers() {
        //when
        PicoContainer container1 = JaversContainerFactory.create();
        PicoContainer container2 = JaversContainerFactory.create();

        //then
        assertThat(container1).isNotSameAs(container2);
        assertThat(container1.getComponent(EntityManager.class))
      .isNotSameAs(container2.getComponent(EntityManager.class));
    }

    @Test
    public void shouldContainRequiredJaversBeans(){
        //when
        PicoContainer container = JaversContainerFactory.create();

        //then
        assertThat(container.getComponent(Javers.class)).isNotNull();
        assertThat(container.getComponent(EntityManager.class)).isNotNull();
        assertThat(container.getComponent(TypeMapper.class)).isNotNull();
        assertThat(container.getComponent(DiffFactory.class)).isNotNull();
    }

    @Test
    public void shouldContainSingletons() {
        //given
        PicoContainer container = JaversContainerFactory.create();

        //when
        Javers javers = container.getComponent(Javers.class);
        Javers javersSecondRef = container.getComponent(Javers.class);

        //then
        assertThat(javers).isSameAs(javersSecondRef);
    }

    @Test
    public void shouldContainFieldBasedPropertyScannerWhenFieldStyle(){
        //when
        PicoContainer container = JaversContainerFactory.create(new JaversConfiguration().withMappingStyle(MappingStyle.FIELD));

        //then
        assertThat(container.getComponent(PropertyScanner.class)).isInstanceOf(FieldBasedPropertyScanner.class);
    }

    @Test
    public void shouldContainBeanBasedPropertyScannerWhenBeanStyle(){
        //when
        PicoContainer container = JaversContainerFactory.create(new JaversConfiguration().withMappingStyle(MappingStyle.BEAN));

        //then
        assertThat(container.getComponent(PropertyScanner.class)).isInstanceOf(BeanBasedPropertyScanner.class);
    }

    @Test
    public void shouldNotContainFieldBasedPropertyScannerWhenBeanStyle() {
        //when
        PicoContainer container = JaversContainerFactory.create(new JaversConfiguration().withMappingStyle(MappingStyle.BEAN));

        //then
        assertThat(container.getComponent(FieldBasedPropertyScanner.class)).isNull();
    }

    @Test
    public void shouldNotContainBeanBasedPropertyScannerWhenFieldStyle(){
        //when
        PicoContainer container = JaversContainerFactory.create(new JaversConfiguration().withMappingStyle(MappingStyle.FIELD));

        //then
        assertThat(container.getComponent(BeanBasedPropertyScanner.class)).isNull();
    }
}
