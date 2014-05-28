package org.javers.core.json.typeadapter

import org.javers.core.commit.CommitId
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
* @author pawel szymczyk
*/
class CommitIdTypeAdapterTest extends Specification{

    def "should serialize CommitId to Json"() {

        given:
        def javers = javersTestAssembly()
        def commitId = new CommitId(1, 0)

        when:
        def jsonText = javers.jsonConverter.toJson(commitId)

        then:
        jsonText == /"1.0"/
    }

    def "should deserialize CommitId"() {

        given:
        def json = /"1.0"/

        when:
        def commitId = javersTestAssembly().jsonConverter.fromJson(json, CommitId)

        then:
        commitId.getMajorId() == 1
        commitId.getMinorId() == 0
    }
}
