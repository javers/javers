package org.javers.core.commit

import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class CommitSeqGeneratorTest extends Specification {

    def "should return 1.0 when first commit"() {
        when:
        def gen1 = new CommitSeqGenerator().nextId(null)

        then:
        gen1.value() == "1.0"
    }

    def "should inc minor and assign 0 to minor when seq calls"() {
        given:
        def head = new CommitId(1,5)
        def commitSeqGenerator = new CommitSeqGenerator()

        when:
        def gen1 = commitSeqGenerator.nextId(head)

        then:
        gen1.value() == "2.0"

        when:
        def gen2 = commitSeqGenerator.nextId(gen1)

        then:
        gen2.value() == "3.0"
    }

    def "should inc minor when the same head"() {
        given:
        def commitSeqGenerator = new CommitSeqGenerator()
        def commit1 = commitSeqGenerator.nextId(null)     //1.0
        def commit2 = commitSeqGenerator.nextId(commit1)  //2.0

        expect:
        commitSeqGenerator.nextId(commit1)  == new CommitId(2,1)
        commitSeqGenerator.nextId(commit2)  == new CommitId(3,0)
        commitSeqGenerator.nextId(commit1)  == new CommitId(2,2)
        commitSeqGenerator.nextId(commit2)  == new CommitId(3,1)
    }
}
