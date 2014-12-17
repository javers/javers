package org.javers.core.diff.appenders;

import org.javers.core.diff.AbstractDiffTest;

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffAppendersTest extends AbstractDiffTest {

    ListChangeAppender listChangeAppender() {
        new ListChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    MapChangeAppender mapChangeAppender() {
        new MapChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }

    ArrayChangeAppender arrayChangeAppender() {
        new ArrayChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    SetChangeAppender setChangeAppender() {
        new SetChangeAppender(mapChangeAppender(), javers.typeMapper, javers.globalIdFactory)
    }
}
