package org.javers.core.diff.appenders.levenshtein

import org.javers.core.diff.AbstractDiffTest


class AbstractLevenshteinListTest extends AbstractDiffTest {

    LevenshteinListChangeAppender levenshteinListChangeAppender() {
        new LevenshteinListChangeAppender()
    }

}
