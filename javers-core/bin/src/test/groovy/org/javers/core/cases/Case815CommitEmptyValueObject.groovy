package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.repository.inmemory.InMemoryRepository
import spock.lang.Specification

class Case815CommitEmptyValueObject extends Specification {

    def "should not throw UnsupportedOperationException when committing empty ValueObject atop non-empty ValueObject"() {
        when:
        // Use a simple in-memory repository.
        InMemoryRepository repo = new InMemoryRepository()
        Javers javers = JaversBuilder.javers().registerJaversRepository(repo).build()

        // Create a RangeHolder with a Range that has some values.
        def versionOne = new RangeHolder()
        versionOne.setId("id")
        def populatedRange = new Range()
        populatedRange.setMin(0)
        populatedRange.setMax(100)
        versionOne.setRange(populatedRange)

        // Commit the first version.
        javers.commit("tester", versionOne)

        // Create a RangeHolder with an empty Range (all fields are null).
        def versionTwo = new RangeHolder()
        versionTwo.setId("id")
        def emptyRange = new Range()
        versionTwo.setRange(emptyRange)

        // Commit the second version. Should not throw an UnsupportedOperationException.
        javers.commit("tester", versionTwo)

        then:
        notThrown(UnsupportedOperationException)
    }
}

@Entity
class RangeHolder {
    @Id
    private String id
    private Range range

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

    Range getRange() {
        return range
    }

    void setRange(Range range) {
        this.range = range
    }
}

@ValueObject
class Range {
    private Integer min
    private Integer max

    Integer getMin() {
        return min
    }

    void setMin(Integer min) {
        this.min = min;
    }

    Integer getMax() {
        return max
    }

    void setMax(Integer max) {
        this.max = max
    }
}
