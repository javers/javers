package org.javers.core.cases

import org.javers.core.Changes
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * see https://stackoverflow.com/questions/51634751/javersexception-property-not-found-property-in-derived-class-not-found-in-abstr
 */
class CaseWithAbstractValueObjectPath extends Specification {

    @Entity
    class InputForm {
        @Id
        String id
        List<InputFormGroup> inputFormGroups
    }

    abstract class InputFormGroup {
        String id
        String name
    }

    class StaticInputFormGroup extends InputFormGroup {
        InputControl inputControl
    }

    class DynamicInputFormGroup extends InputFormGroup {
        List<InputControl> inputControlList
    }

    class InputControl {
        String value

        InputControl(String value) {
            this.value = value
        }
    }

    def "should manage query for Value Object by concrete path"(){
      given:
      def repo = new InMemoryRepository()
      def javers = JaversBuilder.javers().registerJaversRepository(repo) .build()
      def staticInputFormGroup =
              new StaticInputFormGroup(id: "100", inputControl: new InputControl("static Input"))

      def dynamicInputFormGroup =
              new DynamicInputFormGroup(id: "200", inputControlList: [new InputControl("dynamic Input")])

      def inputForm = new InputForm(id:"inputFormId", inputFormGroups: [staticInputFormGroup, dynamicInputFormGroup])

      when:
      javers.commit("author", inputForm)

      //Change the value
      dynamicInputFormGroup.inputControlList[0].value = "New Value"

      javers.commit("author", inputForm)

      //Change the value again
      dynamicInputFormGroup.inputControlList[0].value = "New Value 2"

      javers.commit("author", inputForm)

      Changes changes = javers.findChanges(QueryBuilder.byClass(InputForm).withChildValueObjects().build())
      println "all " + changes.prettyPrint()


      def path = changes.find {it instanceof ValueChange}.affectedGlobalId.fragment
      println "query path: " + path

      // new javers instance - fresh TypeMapper state
      javers = JaversBuilder.javers().registerJaversRepository(repo) .build()

      then:
      // This has thrown
      // JaversException: PROPERTY_NOT_FOUND: Property 'inputControlList' not found in class 'com.example.javerspolymorphismissue.model.InputFormGroup'. If the name is correct - check annotations. Properties with @DiffIgnore or @Transient are not visible for JaVers.
      Changes valueChanges = javers.findChanges(QueryBuilder.byValueObject(InputForm, path).build())
      println valueChanges.prettyPrint()

      valueChanges.size() == 2
    }
}
