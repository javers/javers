package org.javers.spring.auditable.integration

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@DataMongoTest
@ContextConfiguration(classes = [TestApplicationConfig])
@ActiveProfiles('test')
abstract class BaseSpecification extends Specification{
}
