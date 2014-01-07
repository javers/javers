package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.Javers
import org.javers.core.JaversTestBuilder
import org.javers.core.diff.Change
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserWithDate
import org.joda.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.core.json.builder.ChangeTestBuilder.newObject
import static org.javers.core.json.builder.ChangeTestBuilder.objectRemoved
import static org.javers.core.json.builder.ChangeTestBuilder.referenceChanged
import static org.javers.core.json.builder.ChangeTestBuilder.valueChange
import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.core.model.DummyUserWithDate.dummyUserWithDate
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

/**
 * @author bartosz walacik
 */
class JsonConverterDiffIntegrationTest extends Specification {
    class ClassWithChange{
        Change change
    }

    def "should be null safe when converting to json"(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        Change change = null;
        String json = jsonConverter.toJson(new ClassWithChange())

        then:
        assert json.contains('"change": null')
    }

    def "should write property name, left & right values for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ValueChange change = valueChange(dummyUser("kaz").build(),"flag",true,false)

        when:
        String jsonText = jsonConverter.toJson(change)
        System.out.println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "flag"
        json.leftValue == true
        json.rightValue == false
    }


    def "should write property name, leftId & rightId for ReferenceChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ReferenceChange change = referenceChanged(dummyUser().build(),
                                                   "dummyUserDetails",
                                                   dummyUserDetails(1).build(),
                                                   dummyUserDetails(2).build())

        when:
        String jsonText = jsonConverter.toJson(change)
        System.out.println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "dummyUserDetails"
        json.leftReference.cdoId == 1
        json.leftReference.entity == "org.javers.core.model.DummyUserDetails"
        json.rightReference.cdoId == 2
        json.rightReference.entity == "org.javers.core.model.DummyUserDetails"
    }

    def "should be nullSafe when writing leftId & rightId for ReferenceChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ReferenceChange change = referenceChanged(dummyUser().build(),
                "dummyUserDetails",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)
        System.out.println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.rightReference == null
        json.leftReference == null
    }

    def "should be nullSafe when writing left & right value for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ValueChange change = valueChange(dummyUser("kaz").build(),"bigFlag",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)
        System.out.println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.leftValue == null
        json.rightValue == null

    }

    def "should use custom JsonTypeAdapter when writing ImmutableValues for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        def dob = new LocalDateTime()
        ValueChange change = valueChange(dummyUserWithDate("kaz"),"dob",null, dob)

        when:
        String jsonText = jsonConverter.toJson(change)
        System.out.println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.leftValue == LocalDateTimeTypeAdapter.ISO_FORMATTER.print(dob)
        json.rightValue == null
    }

    @Unroll
    def "should write globalCdoId for #change.class.simpleName"(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        String jsonText = jsonConverter.toJson(change)

        expect:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == expectedType
        json.globalCdoId.size() == 2
        json.globalCdoId.cdoId == "kaz"
        json.globalCdoId.entity == "org.javers.core.model.DummyUser"

        where:
        change << [newObject(dummyUser("kaz").build()),
                   objectRemoved(dummyUser("kaz").build()),
                   valueChange(dummyUser("kaz").build(),"flag",true,false),
                   referenceChanged(dummyUser("kaz").build(),"dummyUserDetails",dummyUserDetails(1).build(),null)]
        expectedType << [NewObject.simpleName,
                         ObjectRemoved.simpleName,
                         ValueChange.simpleName,
                         ReferenceChange.simpleName]

    }

    def "should serialize whole Diff"() {
        given:
        DummyUser user =  dummyUser("id").withSex(FEMALE).build();
        DummyUser user2 = dummyUser("id").withSex(MALE).withDetails(1).build();
        Javers javers = JaversTestBuilder.javers()

        when:
        Diff diff = javers.compare("user", user, user2)
        String jsonText = javers.toJson(diff)
        System.out.println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.size() == 4
        json.id == 0
        json.userId == "user"
        json.diffDate != null
        json.changes.size() == 3
        json.changes[0].changeType == "NewObject"
    }
}
