package org.javers.repository.mongo

import org.javers.core.JaversTestBuilder
import org.javers.core.commit.Commit
import org.javers.core.commit.CommitId
import org.javers.core.diff.Diff
import org.joda.time.LocalDateTime
import spock.lang.Specification


class ModelMapper2Test extends Specification {

    def "should map Commit to MongoCommit"() {

        given:
        def commit = new Commit(new CommitId(1, 0), "", new LocalDateTime(), Collections.EMPTY_LIST, Stub(Diff))
        def modelMapper = new ModelMapper2(JaversTestBuilder.javersTestAssembly().jsonConverter)

        when:
        def mongoCommit = modelMapper.toMongoCommit(commit)

        then:
        mongoCommit.getCommitId() == "1.0"
    }

    def "test toMongoChange"() {
//        given:
//
//        when:
//        TODO implement stimulus
//        then:
        // TODO implement assertions
    }

    def "test toMongoSnaphot"() {
//        given:
//
//        when:
//        TODO implement stimulus
//        then:
//        TODO implement assertions
    }
}
