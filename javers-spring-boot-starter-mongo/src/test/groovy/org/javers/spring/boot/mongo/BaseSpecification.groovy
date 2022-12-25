package org.javers.spring.boot.mongo

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@DataMongoTest
@ContextConfiguration(classes = [TestApplication])
abstract class BaseSpecification extends Specification{
}
