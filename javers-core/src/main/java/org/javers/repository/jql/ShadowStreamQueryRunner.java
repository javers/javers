package org.javers.repository.jql;

import org.javers.common.collections.Pair;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.shadow.Shadow;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.*;

class ShadowStreamQueryRunner {
    private final ShadowQueryRunner shadowQueryRunner;

    ShadowStreamQueryRunner(ShadowQueryRunner shadowQueryRunner) {
        this.shadowQueryRunner = shadowQueryRunner;
    }

    Stream<Shadow> queryForShadowsStream(JqlQuery query) {

        if (query.getQueryParams().skip() > 0) {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "skip can't be set on a JqlStreamQuery. Use Stream.skip() on a resulting Stream.");
        }

        int characteristics = IMMUTABLE | ORDERED;
        StreamQuery streamQuery = new StreamQuery(query);
        Spliterator<Shadow> spliterator = Spliterators
                .spliteratorUnknownSize(streamQuery.lazyIterator(), characteristics);

        Stream<Shadow> stream = StreamSupport.stream(spliterator, false);

        return stream;
    }

    class StreamQuery {
        private JqlQuery awaitingQuery;
        private final List<JqlQuery> queries = new ArrayList<>();
        private final List<CdoSnapshot> filledGapsSnapshots = new ArrayList<>();

        StreamQuery(JqlQuery initialQuery) {
            Validate.argumentIsNotNull(initialQuery);
            this.awaitingQuery = initialQuery;
        }

        List<Shadow> loadNextPage() {
            JqlQuery currentQuery = awaitingQuery;

            Pair<List<Shadow>, List<CdoSnapshot>> result =
                    shadowQueryRunner.queryForShadows(currentQuery, filledGapsSnapshots);

            queries.add(currentQuery);
            queries.get(0).appendNextStatsForStream(currentQuery.stats());
            filledGapsSnapshots.addAll(result.right());

            awaitingQuery = currentQuery.nextQueryForStream();

            return result.left();
        }

        Iterator<Shadow> lazyIterator() {
            return new LazyIterator();
        }

        class LazyIterator implements Iterator<Shadow> {
            private boolean terminated = false;
            private List<Shadow> loadedShadows = new ArrayList<>();
            private int nextIdx = 0;

            @Override
            public boolean hasNext() {
                if (terminated) {
                    return false;
                }

                if (shouldLoadNextPage()) {
                    List<Shadow> nextPage = loadNextPage();

                    if (nextPage.size() == 0) {
                        terminate();
                        return false;
                    } else {
                        loadedShadows.addAll(nextPage);
                    }
                }

                return !terminated;
            }

            private void terminate() {
                loadedShadows.clear();
                terminated = true;
            }

            private boolean shouldLoadNextPage() {
                return nextIdx >= loadedShadows.size();
            }

            @Override
            public Shadow next() {
                if (terminated) {
                    throw new IllegalStateException("attempt to read from the terminated iterator");
                }

                Shadow result = loadedShadows.get(nextIdx);
                nextIdx++;
                return result;
            }
        }
    }
}

