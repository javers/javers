package org.javers.core;

import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.appenders.ListChangeAppender;
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;
import org.javers.core.diff.changetype.container.ListChange;

public enum ListCompareAlgorithm {

    SIMPLE(ListChangeAppender.class),
    LEVENSTEIN_EDIT_DISTANCE(LevenshteinListChangeAppender.class);

    private final Class<? extends CorePropertyChangeAppender<ListChange>> listChangeAppender;

    private ListCompareAlgorithm(Class<? extends CorePropertyChangeAppender<ListChange>> listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    public Class<? extends CorePropertyChangeAppender<ListChange>> getAppenderClass() {
        return listChangeAppender;
    }
}
