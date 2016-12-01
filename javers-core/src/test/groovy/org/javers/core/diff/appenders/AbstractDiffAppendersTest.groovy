package org.javers.core.diff.appenders;

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender
import org.javers.guava.multimap.MultimapChangeAppender
import org.javers.guava.multiset.MultisetChangeAppender;

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

    MultimapChangeAppender multimapChangeAppender() {
        new MultimapChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }

    OptionalChangeAppender optionalChangeAppender(){
        new OptionalChangeAppender(javers.globalIdFactory, javers.typeMapper)
    }

    LevenshteinListChangeAppender levenshteinListChangeAppender() {
        new LevenshteinListChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }

    ArrayChangeAppender arrayChangeAppender() {
        new ArrayChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    SetChangeAppender setChangeAppender() {
        new SetChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }

    MultisetChangeAppender multisetChangeAppender() {
        new MultisetChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }
}
