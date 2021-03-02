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
        def commitId = new CommitId(13, 7)

        when:
        def jsonText = javers.jsonConverter.toJson(commitId)

        then:
        jsonText == "13.07"
    }

    def "should deserialize CommitId"() {

        given:
        def json = "12.9"

        when:
        def commitId = javersTestAssembly().jsonConverter.fromJson(json, CommitId)

        then:
        commitId.getMajorId() == 12
        commitId.getMinorId() == 90
    }
}
