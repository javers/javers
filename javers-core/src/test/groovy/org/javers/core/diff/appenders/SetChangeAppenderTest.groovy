package org.javers.core.diff.appenders
/**
 * @author Sergey Kobyshev
 */
class SetChangeAppenderTest extends SetAppenderTest {

    def setupSpec() {
        propertyChangeAppender = setChangeAppender()
        commonFieldName = "stringSet"
        dateFieldName = "setOfDates"
    }
}