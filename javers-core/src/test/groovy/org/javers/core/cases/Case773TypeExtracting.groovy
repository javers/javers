package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.object.SnapshotType
import org.javers.core.model.BooleanValueExample
import org.javers.repository.inmemory.InMemoryRepository
import spock.lang.Specification

/**
 * see https://github.com/javers/javers/issues/723
 */
class Case773TypeExtracting extends Specification {

  def "test"(){
    given:
    def repo = new InMemoryRepository()
    def javers = JaversBuilder.javers().registerJaversRepository(repo).build()

    when:
    def commit = javers.commit("author", new BooleanValueExample())

    then:
    commit.snapshots.size() == 1
    commit.snapshots.get(0).type == SnapshotType.INITIAL
  }

}
