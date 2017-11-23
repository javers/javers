package org.javers.core.diff.appenders
/**
 * @author Sergey Kobyshev
 */
class SetListChangeAppenderTest extends SetAppenderTest {

    def setupSpec() {
        propertyChangeAppender = setListChangeAppender()
        commonFieldName = "stringList"
        dateFieldName = "listOfDates"
    }

}