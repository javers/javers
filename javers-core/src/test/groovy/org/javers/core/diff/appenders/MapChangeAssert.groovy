package org.javers.core.diff.appenders

import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange

/**
 * @author bartosz walacik
 */
public class MapChangeAssert {
    MapChange actual

    static assertThat = { MapChange actual ->
        new MapChangeAssert(actual: actual)
    }

    MapChangeAssert hasSize(int expected) {
        assert actual.changes.size() == expected
        this
    }

    MapChangeAssert hasEntryValueChange(def key, def expectedLeftVal, def expectedRightVal) {
        assert actual.changes.find{it.key == key && it instanceof EntryValueChange && it.leftValue == expectedLeftVal && it.rightValue == expectedRightVal}
        this
    }
}
