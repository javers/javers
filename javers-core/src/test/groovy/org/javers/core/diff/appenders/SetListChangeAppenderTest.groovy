package org.javers.core.diff.appenders

/**
 * @author Sergey Kobyshev
 */
class SetListChangeAppenderTest extends AbstractSetAppenderTest {

    def setupSpec() {
        setChangeAppender = setListChangeAppender()
        commonFieldName = "stringList"
        dateFieldName = "listOfDates"
    }
}