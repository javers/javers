package org.javers.core.examples

import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class RefactoringExample extends Specification {

    def '''should allow Entity class name change
           when both old and new class use @TypeName annotation'''()
    {
        given:
        def javers = JaversBuilder.javers().build()
        javers.commit('author', new Person(1, 'Bob'))

        when: '''Refactoring happens here, Person.class is removed,
                 new PersonRefactored.class appears'''
        javers.commit('author', new PersonRefactored(1, 'Uncle Bob', 'London'))

        def changes =
            javers.findChanges( QueryBuilder.byInstanceId(1,PersonRefactored).build() )

        then: 'one ValueChange is expected'
        changes.size() == 1
        with(changes[0]){
            left == 'Bob'
            right == 'Uncle Bob'
            affectedGlobalId.value() == 'Person/1'
        }
        println changes[0]
    }

    def '''should allow Entity class name change
           when old class forgot to use @TypeName annotation'''()
    {
      given:
      def javers = JaversBuilder.javers().build()
      javers.commit('author', new PersonSimple(1,'Bob'))

      when:
      javers.commit('author', new PersonRetrofitted(1,'Uncle Bob'))

      def changes =
          javers.findChanges( QueryBuilder.byInstanceId(1,PersonRetrofitted).build() )

      then: 'one ValueChange is expected'
      changes.size() == 1
      with(changes[0]){
          left == 'Bob'
          right == 'Uncle Bob'
          affectedGlobalId.value() == 'org.javers.core.examples.PersonSimple/1'
      }
      println changes[0]
    }

    def "should be very relaxed about ValueObject types"(){
      given:
      def javers = JaversBuilder.javers().build()
      javers.commit('author', new Person(1,new EmailAddress('me@example.com',   false)))
      javers.commit('author', new Person(1,new HomeAddress ('London','Green 50',true)))
      javers.commit('author', new Person(1,new HomeAddress ('London','Green 55',true)))

      when:
      def changes =
          javers.findChanges( QueryBuilder.byValueObjectId(1, Person, 'address').build() )

      then: 'three ValueChanges are expected'
      changes.size() == 3
      changes.count{
          it.propertyName == 'email' &&
          it.left == 'me@example.com'
          it.right == null
      } // == 1
      changes.count{
          it.propertyName == 'verified' &&
          it.left == false &&
          it.right == true
      }
      changes.count{
          it.propertyName == 'street' &&
          it.left == 'Green 50' &&
          it.right == 'Green 55'
      }
      changes.each { println it }
    }
}
