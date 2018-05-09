package org.javers.core.diff;

import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.appenders.ListAsSetChangeAppender;
import org.javers.core.diff.appenders.SimpleListChangeAppender;
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;
import org.javers.core.diff.changetype.container.ListChange;

public enum ListCompareAlgorithm {

    SIMPLE(SimpleListChangeAppender.class),
    LEVENSHTEIN_DISTANCE(LevenshteinListChangeAppender.class),
    AS_SET(ListAsSetChangeAppender.class),;

    private final Class<? extends CorePropertyChangeAppender<ListChange>> listChangeAppender;

    ListCompareAlgorithm(Class<? extends CorePropertyChangeAppender<ListChange>> listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    public Class<? extends CorePropertyChangeAppender<ListChange>> getAppenderClass() {
        return listChangeAppender;
    }
}
