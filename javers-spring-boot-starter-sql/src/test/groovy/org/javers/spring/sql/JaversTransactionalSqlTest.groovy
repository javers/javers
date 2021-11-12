package org.javers.spring.sql

import org.javers.spring.transactions.JaversTransactionalTest
import org.javers.spring.boot.TestApplication
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TestApplication])
class JaversTransactionalSqlTest extends JaversTransactionalTest {
}
