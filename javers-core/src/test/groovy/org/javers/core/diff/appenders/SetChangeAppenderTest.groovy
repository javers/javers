package org.javers.core.diff.appenders

/**
 * @author Sergey Kobyshev
 */
class SetChangeAppenderTest extends AbstractSetAppenderTest {

    def setupSpec() {
        setChangeAppender = setChangeAppender()
        commonFieldName = "stringSet"
        dateFieldName = "setOfDates"
    }
}