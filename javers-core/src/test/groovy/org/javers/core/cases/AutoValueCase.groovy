package org.javers.core.cases

import groovy.transform.PackageScope
import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.type.EntityType
import spock.lang.Specification
import spock.lang.Unroll

class AutoValueCase extends Specification {

  abstract class AbstractEntity {
    @Id
    abstract int getId()

    abstract int getValue()
  }

  @PackageScope
  final class ConcreteEntity extends AbstractEntity {
    private final int id
    private final int value

    ConcreteEntity(final int id, final int value) {
      this.id = id
      this.value = value
    }

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
    def a = new ConcreteEntity(1, 1)
    def b = new ConcreteEntity(1, 2)
    def diff = javers.compare(a, b)
    def first = javers.commit('Alice', a)
    def second = javers.commit('Bob', b)

    expect:
    diff.changes.size() == 1
    diff.changes[0].propertyName == 'value'
    diff.changes[0].left == 1
    diff.changes[0].right == 2

    first.author == 'Alice'
    second.author == 'Bob'
    second.changes.size() == 1
    second.changes[0].propertyName == 'value'
    second.changes[0].left == 1
    second.changes[0].right == 2
    second.snapshots[0].managedType.managedClass.looksLikeId.size() == looksLikeId

    where:
    label << ['basic javers', 'javers with bean mapping', 'javers with bean mapping and entity registered']
    javers << [
            JaversBuilder.javers().build(),
            JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).build(),
            JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).registerEntity(AbstractEntity.class).build()
    ]
    looksLikeId << [0, 1, 1]
  }

  @Unroll
  def "should map #entity.simpleName with abstract @IdGetter as EntityType"(){
    given:
    def javers = JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).build()

    expect:
    javers.getTypeMapping(entity) instanceof EntityType

    where:
    entity << [AbstractEntity, ConcreteEntity]
  }
}