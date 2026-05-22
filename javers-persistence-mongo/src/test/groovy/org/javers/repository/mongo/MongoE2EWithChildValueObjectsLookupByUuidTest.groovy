package org.javers.repository.mongo

import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import jakarta.persistence.Id
import org.bson.UuidRepresentation
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared

import static org.javers.repository.jql.QueryBuilder.byInstance

@Testcontainers
class MongoE2EWithChildValueObjectsLookupByUuidTest extends JaversMongoRepositoryE2ETest {

    @Shared
    static DockerizedMongoContainer dockerizedMongoContainer = new DockerizedMongoContainer(MongoClientSettings
            .builder()
            .uuidRepresentation(UuidRepresentation.STANDARD))

    @Shared
    static MongoClient mongoClient = dockerizedMongoContainer.mongoClient

    @Override
    protected MongoDatabase getMongoDb() {
        mongoClient.getDatabase("test")
    }

    class Parent {
        @Id
        UUID id;
        List<Child> children;
    }

    class Child {
        List<GrandChild> grandChildren;
    }

    class GrandChild {
        String value;
    }

    def "withChildValueObjects() should work with UUID as entity id"() {
        given:
        GrandChild grandChild1 = new GrandChild(value: "value1")
        GrandChild grandChild2 = new GrandChild(value: "value2")
        GrandChild grandChild3 = new GrandChild(value: "value3")
        GrandChild grandChild4 = new GrandChild(value: "value4")
        GrandChild grandChild5 = new GrandChild(value: "value5")
        GrandChild grandChild6 = new GrandChild(value: "value6")

        Child child1 = new Child(grandChildren: [grandChild1, grandChild2])
        Child child2 = new Child(grandChildren: [grandChild3, grandChild4, grandChild5])
        Child child3 = new Child(grandChildren: [grandChild6])

        Parent parent = new Parent(id: UUID.randomUUID(), children: [child1, child2, child3])

        javers.commit("author", parent)

        when:
        def query = byInstance(parent).withChildValueObjects().build()
        def snapshots = javers.findSnapshots(query)

        then:
        assert snapshots.size() == 10 // 1x Parent, 3x Child, 6x GrandChild
    }
}
