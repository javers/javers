package org.javers.spring.data

import org.javers.common.collections.Optional
import org.objenesis.ObjenesisStd
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created by gessnerfl on 21.02.15.
 */
class JaversSpringDataAuditEventTest extends Specification {

    def "should match method name"(){
        given:
        def event = JaversSpringDataAuditEvent.SAVE

        expect:
        event.isEventMethodName("save") == true
    }

    def "should match save method name"(){
        given:
        def event = JaversSpringDataAuditEvent.SAVE

        expect:
        event.isEventMethodName("foo") == false
    }

    def "should match method"(){
        given:
        def objenesis = new ObjenesisStd();
        def method = objenesis.getInstantiatorOf(Method.class).newInstance()
        method.name = "save"

        def event = JaversSpringDataAuditEvent.SAVE

        expect:
        event.isEventMethod(method) == true
    }

    def "should not match method"(){
        given:
        def objenesis = new ObjenesisStd();
        def method = objenesis.getInstantiatorOf(Method.class).newInstance()
        method.name = "foo"

        def event = JaversSpringDataAuditEvent.SAVE

        expect:
        event.isEventMethod(method) == false
    }

    def "should return save event"(){
        given:
        def name = "save"

        when:
        def event = JaversSpringDataAuditEvent.byMethodName(name)

        then:
        JaversSpringDataAuditEvent.SAVE == event.get()
    }

    def "should return delete event"(){
        given:
        def name = "delete"

        when:
        def event = JaversSpringDataAuditEvent.byMethodName(name)

        then:
        JaversSpringDataAuditEvent.DELETE == event.get()
    }

    def "should return no event"(){
        given:
        def name = "foo"

        expect:
        JaversSpringDataAuditEvent.byMethodName(name) == Optional.empty()
    }
}
