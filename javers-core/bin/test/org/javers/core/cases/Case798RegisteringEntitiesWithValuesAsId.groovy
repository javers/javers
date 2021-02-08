package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.ValueType
import spock.lang.Specification

import javax.persistence.Id

class Case798RegisteringEntitiesWithValuesAsId extends Specification {
    
    class Person {
        @DiffIgnore
        @Id
        private Integer id 
        public Passport passport

        public Person(Passport passport) {
            this.passport = passport
        }
    }
    
    class Passport {
        @DiffIgnore
        @Id
        private Integer id 
        private String passportId
        
        Passport(String passportId) {
            this.passportId = passportId
        }

        @Override
        String toString() {
            return "Passport{" +
                    "passportId='" + passportId + '\'' +
                    '}';
        }
    }
    
    class Person2 {
        @DiffIgnore
        @Id
        private Integer id 
        public Passport passport
        
        public Person2(Passport passport) {
            this.passport = passport
        }
    }

    def "should successfully generate ID when registering entities and referred value in mixed order"(){
        given:
        Passport sebastiansPassport = new Passport("ID-398")
        Person sebastian = new Person(sebastiansPassport)

        when:
        JaversBuilder javersBuilder = JaversBuilder.javers()

        javersBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)

        // Order changed
        javersBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        javersBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        javersBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        Javers javers = javersBuilder.build()

        println javers.getTypeMapping(Person.class).prettyPrint()
        println javers.getTypeMapping(Person2.class).prettyPrint()
        println javers.getTypeMapping(Passport.class).prettyPrint()

        EntityType entityType = (EntityType) javers.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)
        
        then:
        javers.getTypeMapping(Person.class).idProperty.type instanceof ValueType
        javers.getTypeMapping(Person2.class).idProperty.type instanceof ValueType
        sebastiansId.value().endsWith("Person/ID-398")
    }

    def "should successfully generate ID when registering entities before referred value"(){
        given:
        Passport sebastiansPassport = new Passport("ID-398")
        Person sebastian = new Person(sebastiansPassport)

        when:
        JaversBuilder javersBuilder = JaversBuilder.javers()

        javersBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
        javersBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        javersBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        javersBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        Javers javers = javersBuilder.build()

        println javers.getTypeMapping(Person.class).prettyPrint()
        println javers.getTypeMapping(Person2.class).prettyPrint()
        println javers.getTypeMapping(Passport.class).prettyPrint()

        EntityType entityType = (EntityType) javers.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)

        then:
        javers.getTypeMapping(Person.class).idProperty.type instanceof ValueType
        javers.getTypeMapping(Person2.class).idProperty.type instanceof ValueType
        sebastiansId.value().endsWith("Person/ID-398")
    }
}
