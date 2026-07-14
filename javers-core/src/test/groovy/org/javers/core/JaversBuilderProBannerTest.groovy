package org.javers.core

import spock.lang.Specification

/**
 * @see <a href="https://github.com/javers/javers/issues/1488">#1488</a>
 */
class JaversBuilderProBannerTest extends Specification {

    private static final String BANNER_MARKER = "JaVers is Evolving to Open Core!"

    private PrintStream originalOut
    private ByteArrayOutputStream captured

    def setup() {
        // the banner is printed at most once per JVM, so the guard has to be
        // cleared or an earlier build() in the suite would mask the assertion
        resetProBannerPrintedFlag()
        originalOut = System.out
        captured = new ByteArrayOutputStream()
        System.setOut(new PrintStream(captured))
    }

    def cleanup() {
        System.setOut(originalOut)
        resetProBannerPrintedFlag()
    }

    def "should print pro banner by default"() {
        when:
        JaversBuilder.javers().build()

        then:
        capturedOut().contains(BANNER_MARKER)
    }

    def "should not print pro banner when disabled in JaversBuilder"() {
        when:
        JaversBuilder.javers().withPrintProBanner(false).build()

        then:
        !capturedOut().contains(BANNER_MARKER)
    }

    def "should not print pro banner when disabled in JaversCoreProperties"() {
        given:
        def properties = new JaversCoreProperties() {}
        properties.setPrintProBanner(false)

        when:
        JaversBuilder.javers().withProperties(properties).build()

        then:
        !capturedOut().contains(BANNER_MARKER)
    }

    def "should print pro banner when JaversCoreProperties leaves it unset"() {
        given:
        def properties = new JaversCoreProperties() {}

        when:
        JaversBuilder.javers().withProperties(properties).build()

        then:
        capturedOut().contains(BANNER_MARKER)
    }

    private String capturedOut() {
        System.out.flush()
        captured.toString()
    }

    private static void resetProBannerPrintedFlag() {
        def field = JaversBuilder.getDeclaredField("proBannerPrinted")
        field.setAccessible(true)
        field.setBoolean(null, false)
    }
}
