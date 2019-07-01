package org.javers.core.commit

import spock.lang.Specification
import spock.lang.Unroll

class CommitIdTest extends Specification {

    @Unroll
    def "should create CommitId with large majorId=#majorId and minorId=#minorId from BigDecimal #input"() {
        when:
        def commitId = CommitId.valueOf(new BigDecimal(input))

        then:
        commitId.majorId == majorId
        commitId.minorId == minorId

        where:
        input                     | majorId             | minorId
        "8804939115786087745"     | 8804939115786087745 | 0
        "8804939115786087745.01"  | 8804939115786087745 | 1
        "8804939115786087745.10"  | 8804939115786087745 | 10
        "8804939115786087745.12"  | 8804939115786087745 | 12
        "8804939115786087745.123" | 8804939115786087745 | 12
    }

    @Unroll
    def "should get large valueAsNumber #valueAsNumber when majorId=#majorId and minorId=#minorId"() {
        when:
        def commitId = new CommitId(majorId, minorId)

        then:
        commitId.valueAsNumber() == valueAsNumber
        commitId.minorId == minorId

        where:
        majorId             | minorId | valueAsNumber
        8804939115786087745 | 0       | 8804939115786087745
        8804939115786087745 | 1       | 8804939115786087745.01
        8804939115786087745 | 10      | 8804939115786087745.10
        8804939115786087745 | 12      | 8804939115786087745.12
    }
}
