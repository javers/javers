package org.javers.core.diff.appenders

import org.javers.core.diff.changetype.ContainerChange

/**
 * @author bartosz walacik
 */
class ContainerChangeAssert {

    ContainerChange actual

    static assertThat = { ContainerChange actual ->
        new ContainerChangeAssert(actual: actual)
    }

    ContainerChangeAssert hasSize(int expected){
        assert actual.changes.size() == expected
        this
    }
}
