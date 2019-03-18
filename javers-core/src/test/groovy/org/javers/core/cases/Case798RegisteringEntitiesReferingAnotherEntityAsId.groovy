package org.javers.core.cases

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Map

import javax.persistence.Id
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.clazz.ClientsClassDefinition
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.type.EntityType
import spock.lang.Specification

class Case798RegisteringEntitiesReferingAnotherEntityAsId extends Specification {
    
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
        
        public Passport(String passportId) {
            this.passportId = passportId
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

    public void makeOrderOfClassDefinitionsDeterministic(JaversBuilder javersBuilder) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Map<Class, ClientsClassDefinition> sortedClassDefinitions = new LinkedHashMap<>() 
        Field field = JaversBuilder.class.getDeclaredField( "clientsClassDefinitions" )
        field.setAccessible( true )

        Field modifiersField = Field.class.getDeclaredField( "modifiers" )
        modifiersField.setAccessible( true )
        modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL )
        field.set(javersBuilder, sortedClassDefinitions)
    //    javersBuilder.clientsClassDefinitions = new LinkedHashMap<>()
    }

    def "should successfully generate ID when registering entities and referred value in mixed order"(){
        given:
        Passport sebastiansPassport = new Passport("ID-398")
        Person sebastian = new Person(sebastiansPassport)

        when:
        JaversBuilder javersBuilder = JaversBuilder.javers()
        
        // We found indeterministic behavior, depending on the internal order of the classes
        // within the Class Definitions stored in a map of the JaversBuilder
        makeOrderOfClassDefinitionsDeterministic(javersBuilder)
        javersBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)

        // Order changed
        javersBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        javersBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        javersBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        Javers javers = javersBuilder.build()

        EntityType entityType = (EntityType) javers.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)
        
        then:
        sebastiansId.value().endsWith("Person/ID-398")
    }

    def "should successfully generate ID when registering entities before referred value"(){
        given:
        Passport sebastiansPassport = new Passport("ID-398")
        Person sebastian = new Person(sebastiansPassport)

        when:
        JaversBuilder javersBuilder = JaversBuilder.javers()
        
        // We found indeterministic behavior, depending on the internal order of the classes
        // within the Class Definitions stored in a map of the JaversBuilder
        makeOrderOfClassDefinitionsDeterministic(javersBuilder)
        javersBuilder.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
        javersBuilder.registerEntity(new EntityDefinition(Person2.class, "passport", Arrays.asList("id")))
        javersBuilder.registerEntity(new EntityDefinition(Person.class, "passport", Arrays.asList("id")))
        javersBuilder.registerValueWithCustomToString(Passport.class, {passport -> passport.passportId})
        Javers javers = javersBuilder.build()

        EntityType entityType = (EntityType) javers.getTypeMapping(Person.class)
        InstanceId sebastiansId = entityType.createIdFromInstance(sebastian)

        then:
        sebastiansId.value().endsWith("Person/ID-398")
    }
}
