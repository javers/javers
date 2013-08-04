package org.javers.model.mapping;

import org.fest.assertions.api.Assertions;
import org.javers.core.model.DummyUser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.javers.model.mapping.MetaType.*;

/**
 * @author bartosz walacik
 */
@Test
public class EntityFromBeanConstructionTest {
    private EntityFactory entityFactory;

    @BeforeMethod
    public void setUp() {
        entityFactory = new EntityFactory();
    }

    public void shouldHoldReferenceToSourceClass() {
        //when
        Entity entity = entityFactory.createFromBean(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasSourceClass(DummyUser.class);
    }

    public void shouldScanSimpleProperties() {
        //when
        Entity entity = entityFactory.createFromBean(DummyUser.class);

        //then
        Assertions.assertThat(entity.getProperties()).hasSize(5);
    }

    public void shouldScanIntProperty() {
        //when
        Entity entity = entityFactory.createFromBean(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("age")
                                       .hasType(PRIMITIVE);
    }

    public void shouldScanIntegerProperty() {
        //when
        Entity entity = entityFactory.createFromBean(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("largeInt")
                                       .hasType(PRIMITIVE_BOX);
    }

    public void shouldScanBooleanProperty() {
        //when
        Entity entity = entityFactory.createFromBean(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("flag")
                                       .hasType(PRIMITIVE);
    }

    public void shouldScanBigBooleanProperty() {
        //when
        Entity entity = entityFactory.createFromBean(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("bigFlag")
                .hasType(PRIMITIVE_BOX);
    }

    public void shouldScanStringProperty() {
        //when
        Entity entity = entityFactory.createFromBean(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("name")
                .hasType(PRIMITIVE_BOX);
    }
}
