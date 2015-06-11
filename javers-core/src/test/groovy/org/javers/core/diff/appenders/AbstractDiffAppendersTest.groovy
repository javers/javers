package org.javers.core.diff.appenders;

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffAppendersTest extends AbstractDiffTest {

    SimpleListChangeAppender listChangeAppender() {
        new SimpleListChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    MapChangeAppender mapChangeAppender() {
        new MapChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }

    LevenshteinListChangeAppender levenshteinListChangeAppender() {
        new LevenshteinListChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }

    ArrayChangeAppender arrayChangeAppender() {
        new ArrayChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    SetChangeAppender setChangeAppender() {
        new SetChangeAppender(mapChangeAppender(), javers.typeMapper, javers.globalIdFactory)
    }
}
