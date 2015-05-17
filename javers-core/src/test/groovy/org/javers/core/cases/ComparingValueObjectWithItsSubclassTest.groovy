package org.javers.core.cases

import org.javers.core.JaversBuilder
import spock.lang.Specification

import static org.javers.core.cases.ComparingValueObjectWithItsSubclassClasses.*
import static org.javers.core.cases.ComparingValueObjectWithItsSubclassClasses.Bicycle
import static org.javers.core.cases.ComparingValueObjectWithItsSubclassClasses.Mountenbike

/**
 * @see https://github.com/javers/javers/issues/127
 * @author bartosz.walacik
 */
class ComparingValueObjectWithItsSubclassTest extends Specification {

    def "should compare ValueObject with its subclass even if subclass has more fields"(){
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def diff = javers.compare(new Store(bicycles: [new Mountenbike(seatHeight: 1)]),
                                  new Store(bicycles: [new Bicycle()]))

        println diff

        then:
        diff.changes.size() == 1
        diff.changes[0].propertyName == "seatHeight"
        diff.changes[0].left == 1
        diff.changes[0].right == null
    }
}