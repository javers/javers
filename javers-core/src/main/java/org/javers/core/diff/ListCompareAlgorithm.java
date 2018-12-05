package org.javers.core.diff;

import org.javers.core.diff.appenders.ListAsSetChangeAppender;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.diff.appenders.SimpleListChangeAppender;
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;
import org.javers.core.diff.changetype.container.ListChange;

public enum ListCompareAlgorithm {

    SIMPLE(SimpleListChangeAppender.class),
    LEVENSHTEIN_DISTANCE(LevenshteinListChangeAppender.class),
    AS_SET(ListAsSetChangeAppender.class);

    private final Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender;

    ListCompareAlgorithm(Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    public Class<? extends PropertyChangeAppender<ListChange>> getAppenderClass() {
        return listChangeAppender;
    }
}
