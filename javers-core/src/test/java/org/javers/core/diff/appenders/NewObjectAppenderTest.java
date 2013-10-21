package org.javers.core.diff.appenders;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

import java.util.Set;

import org.javers.common.collections.Sets;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.domain.Change;
import org.javers.model.domain.Diff;
import org.javers.model.domain.changeType.NewObject;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;
import org.javers.model.object.graph.ObjectNode;
import org.javers.test.builder.DummyUserBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class NewObjectAppenderTest {

    private NewObjectAppender newObjectAppender = new NewObjectAppender();
    private ObjectGraphBuilder objectGraphBuilder;

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        EntityManager entityManager = new EntityManager(new BeanBasedEntityFactory(mapper));
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUserDetails.class);
        entityManager.buildManagedClasses();
        objectGraphBuilder = new ObjectGraphBuilder(entityManager);
    }

    public void shouldAppendNewObjectToDiff() {
        // given:
        Diff diff = new Diff("userId");

        Set<ObjectNode> previousGraph = Sets.asSet(createObjectNodeWithId("1"));
        Set<ObjectNode> currentGraph = Sets.asSet(createObjectNodeWithId("1"), createObjectNodeWithId("2"));

        // when:
        newObjectAppender.append(diff, previousGraph, currentGraph);

        // then:
        // TODO: custom assert
        Assert.assertEquals(diff.getChanges().size(), 1);
        Change change = diff.getChanges().get(0);
        Assert.assertTrue(change instanceof NewObject);
        Assert.assertEquals(change.getGlobalCdoId().getCdoId(), "2");
        Assert.assertEquals(change.getGlobalCdoId().getEntity().getSourceClass(), DummyUser.class);
        Assert.assertEquals(change.getParent(), diff);
    }

    private ObjectNode createObjectNodeWithId(String id) {
        return objectGraphBuilder.build(DummyUserBuilder.dummyUser().withName(id).build());
    }
}
