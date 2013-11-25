package org.javers.core;

import org.javers.core.pico.JaversContainerFactory;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.PropertyScanner;
import org.junit.Test;
import org.picocontainer.PicoContainer;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.javers.core.JaversBuilder.*;

/**
 * @author bartosz walacik
 */
public class JaversBuilderPropertyScannerTest {
    @Test
    public void shouldContainFieldBasedPropertyScannerWhenFieldStyle(){
        //given
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD);

        //when
        javersBuilder.build();

        //then
        assertThat(javersBuilder.getContainerComponent(PropertyScanner.class)).isInstanceOf(FieldBasedPropertyScanner.class);
    }

    @Test
    public void shouldContainBeanBasedPropertyScannerWhenBeanStyle(){
        //given
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN);

        //when
        javersBuilder.build();

        //then
        assertThat(javersBuilder.getContainerComponent(PropertyScanner.class)).isInstanceOf(BeanBasedPropertyScanner.class);
    }

    @Test
    public void shouldNotContainFieldBasedPropertyScannerWhenBeanStyle() {
        //given
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN);

        //when
        javersBuilder.build();

        //then
        assertThat(javersBuilder.getContainerComponent(FieldBasedPropertyScanner.class)).isNull();
    }

    @Test
    public void shouldNotContainBeanBasedPropertyScannerWhenFieldStyle(){
        //given
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD);

        //when
        javersBuilder.build();

        //then
        assertThat(javersBuilder.getContainerComponent(BeanBasedPropertyScanner.class)).isNull();
    }
}
