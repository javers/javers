package org.javers.core.diff;

import org.javers.core.diff.appenders.ListAsSetChangeAppender;
import org.javers.core.diff.appenders.ListChangeAppender;
import org.javers.core.diff.appenders.SimpleListChangeAppender;
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;

public enum ListCompareAlgorithm {

    SIMPLE(SimpleListChangeAppender.class),
    LEVENSHTEIN_DISTANCE(LevenshteinListChangeAppender.class),
    AS_SET(ListAsSetChangeAppender.class);

    private final Class<? extends ListChangeAppender> listChangeAppender;

    ListCompareAlgorithm(Class<? extends ListChangeAppender> listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    public Class<? extends ListChangeAppender> getAppenderClass() {
        return listChangeAppender;
    }
}
