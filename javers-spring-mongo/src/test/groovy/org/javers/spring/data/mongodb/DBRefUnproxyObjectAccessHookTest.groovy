package org.javers.spring.data.mongodb


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
//@Ignore //TODO
class DBRefUnproxyObjectAccessHookTest extends Specification {

}