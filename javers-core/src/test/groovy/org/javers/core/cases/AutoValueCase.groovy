package org.javers.core.cases

import groovy.transform.PackageScope
import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification
import spock.lang.Unroll

class AutoValueCase extends Specification {

  abstract class AbstractEntity {
    @Id
    abstract int getId()

    abstract int getValue()
  }

  @PackageScope
  class ConcreteEntity extends AbstractEntity {
    private int value
    private int id

    @Override
    int getId() {
      id
    }

    @Override
    int getValue() {
      value
    }
  }

  @Unroll
  def "#label should support abstract idGetter"() {
    given:
    def a = new ConcreteEntity(id:1, value: 1)
    def b = new ConcreteEntity(id:1, value: 2)
    def diff = javers.compare(a, b)

    expect:
    diff.changes.size() == 1
    diff.changes[0].left == 1
    diff.changes[0].right == 2

    where:
    label << ['basic javers', 'javers with bean mapping', 'javers with bean mapping and entity registered']
    javers << [
            JaversBuilder.javers().build(),
            JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).build(),
            JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).registerEntity(AbstractEntity.class).build()
    ]
  }
}