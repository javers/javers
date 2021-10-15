package org.javers.core.diff.appenders

import org.javers.core.model.SnapshotEntity
import spock.lang.Shared

import java.time.LocalDate

/**
 * @author lucas prestes
 */
class ListAsSetChangeAppenderTest extends AbstractDiffAppendersTest {

    @Shared
    ListAsSetChangeAppender listAsSetChangeAppender

    @Shared
    String commonFieldName

    @Shared
    String dateFieldName

    def setupSpec() {
        listAsSetChangeAppender = listAsSetChangeAppender()
        commonFieldName = "stringSet"
        dateFieldName = "setOfDates"
    }

    def "should set left and right value in change"(){
        given:
        def leftCdo = new SnapshotEntity("$dateFieldName": [LocalDate.of(2001, 5, 5), LocalDate.of(2001, 1, 1)])
        def rightCdo = new SnapshotEntity("$dateFieldName": [LocalDate.of(2001, 1, 1)])

        when:
        def change = listAsSetChangeAppender
            .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, dateFieldName))

        then:
        change.left.size() == 2
        change.right.size() == 1
        change.left.contains(LocalDate.of(2001,5,5))
        change.left.contains(LocalDate.of(2001,1,1))
        change.right.contains(LocalDate.of(2001,1,1))
        !change.right.contains(LocalDate.of(2001,5,5))
    }

}
