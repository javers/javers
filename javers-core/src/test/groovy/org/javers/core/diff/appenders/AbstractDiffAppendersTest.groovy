package org.javers.core.diff.appenders;

import org.javers.core.diff.AbstractDiffTest

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffAppendersTest extends AbstractDiffTest {

    SimpleListChangeAppender listChangeAppender() {
        new SimpleListChangeAppender(new MapChangeAppender())
    }

    ArrayChangeAppender arrayChangeAppender() {
        new ArrayChangeAppender(new MapChangeAppender())
    }

    ListAsSetChangeAppender listAsSetChangeAppender(){
        return new ListAsSetChangeAppender(setChangeAppender())
    }

    CollectionAsListChangeAppender collectionAsListChangeAppender(){
        return new CollectionAsListChangeAppender(mapChangeAppender(),javers.typeMapper);
    }
}
