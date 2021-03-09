package org.javers.repository.jql;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.shadow.Shadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.ORDERED;
import static org.javers.repository.jql.ShadowQueryRunner.*;

class ShadowStreamQueryRunner {
    private static final Logger logger = LoggerFactory.getLogger(JqlQuery.JQL_LOGGER_NAME);

    private final ShadowQueryRunner shadowQueryRunner;

    ShadowStreamQueryRunner(ShadowQueryRunner shadowQueryRunner) {
        this.shadowQueryRunner = shadowQueryRunner;
    }

    Stream<Shadow> queryForShadowsStream(JqlQuery query) {
        int shadowsLimit = query.getQueryParams().limit();
        query.changeToAggregated();

        int characteristics = IMMUTABLE | ORDERED;
        StreamQuery streamQuery = new StreamQuery(query, shadowsLimit);
        Spliterator<Shadow> spliterator = Spliterators
                .spliteratorUnknownSize(streamQuery.lazyIterator(), characteristics);

        Stream<Shadow> stream = StreamSupport.stream(spliterator, false);

        if (query.getQueryParams().skip() > 0) {
            stream = stream.skip(query.getQueryParams().skip());
        }

        query.setShadowQueryRunnerStats(streamQuery.streamStats);
        return stream;
    }

    private class StreamQuery {
        private JqlQuery awaitingQuery;
        private ShadowStreamStats streamStats = new ShadowStreamStats();
        private final List<CdoSnapshot> filledGapsSnapshots = new ArrayList<>();
        private final int snapshotBatchSize;
        private final int shadowsLimit;

        StreamQuery(JqlQuery initialQuery, int shadowsLimit) {
            Validate.argumentIsNotNull(initialQuery);
            this.snapshotBatchSize = initialQuery.getQueryParams().hasSnapshotQueryLimit()
                    ? initialQuery.getQueryParams().snapshotQueryLimit().get()
                    : 100;

            this.awaitingQuery = initialQuery.changeLimit(this.snapshotBatchSize);
            this.shadowsLimit = shadowsLimit;
        }

        List<Shadow> loadNextPage() {
            JqlQuery currentQuery = awaitingQuery;

            ShadowQueryResult result = shadowQueryRunner.queryForShadows(currentQuery, filledGapsSnapshots);
            logger.debug("Shadow stream query (frame " +(streamStats.size()+1)+") executed:\nJqlQuery {\n" +
                    "  "+currentQuery.getFilterDefinition() + "\n"+
                    "  "+currentQuery.getQueryParams() + "\n" +
                    "  shadowScope: "+currentQuery.getShadowScope() + "\n" +
                    "  "+result.getQueryStats() + "\n" +
                    "}");

            streamStats.addNextFrameStats(result.getQueryStats());
            filledGapsSnapshots.addAll(result.getFilledGapsSnapshots());

            awaitingQuery = currentQuery.nextQueryForStream();
            return result.getShadows();
        }

        Iterator<Shadow> lazyIterator() {
            return new LazyIterator();
        }

        class LazyIterator implements Iterator<Shadow> {
            private boolean terminated = false;
            Deque<Shadow> shadowsToGo = new ArrayDeque<>();
            private int nextIdx = 0;
            private boolean noMorePages;

            @Override
            public boolean hasNext() {

                if (nextIdx == shadowsLimit) {
                    terminate();
                    return false;
                }

                if (shadowsToGo.size() > 0) {
                    return true;
                } else {
                    if (noMorePages) {
                        return false;
                    }
                    else {
                        List<Shadow> nextPage = loadNextPage();
                        if (nextPage.size() < snapshotBatchSize) {
                            noMorePages = true;
                        }

                        if (nextPage.size() == 0) {
                            terminate();
                            return false;
                        } else {
                            shadowsToGo.addAll(nextPage);
                        }
                        return true;
                    }
                }
            }

            private void terminate() {
                shadowsToGo.clear();
                terminated = true;
            }

            @Override
            public Shadow next() {
                if (terminated) {
                    throw new IllegalStateException("attempt to read from the terminated iterator");
                }

                Shadow result = shadowsToGo.poll();
                nextIdx++;
                return result;
            }
        }
    }

    public static class ShadowStreamStats extends ShadowStats {
        private final List<ShadowStats> frameQueriesStats = new ArrayList<>();

        void addNextFrameStats(ShadowStats next) {
            this.frameQueriesStats.add(next);
        }

        int size() {
            return frameQueriesStats.size();
        }

        @Override
        List<Object> toStringProps() {
            return Lists.join(super.toStringProps(),
                    Lists.asList("Shadow stream frame queries", getShadowQueriesCount()));
        }

        @Override
        public long getStartTimestamp() {
            return frameQueriesStats.get(0).getStartTimestamp();
        }

        @Override
        public long getEndTimestamp() {
            if (frameQueriesStats.size() == 0) {
                return 0;
            }
            return frameQueriesStats.get(frameQueriesStats.size() - 1).getEndTimestamp();
        }

        @Override
        public int getDbQueriesCount() {
            return frameQueriesStats.stream().mapToInt(it -> it.getDbQueriesCount()).sum();
        }

        public int getShadowQueriesCount() {
            return frameQueriesStats.size();
        }

        @Override
        public int getAllSnapshotsCount() {
            return frameQueriesStats.stream().mapToInt(it -> it.getAllSnapshotsCount()).sum();
        }

        @Override
        public int getShallowSnapshotsCount() {
            return frameQueriesStats.stream().mapToInt(it -> it.getShallowSnapshotsCount()).sum();
        }

        @Override
        public int getDeepPlusSnapshotsCount() {
            return frameQueriesStats.stream().mapToInt(it -> it.getDeepPlusSnapshotsCount()).sum();
        }

        @Override
        public int getCommitDeepSnapshotsCount() {
            return frameQueriesStats.stream().mapToInt(it -> it.getCommitDeepSnapshotsCount()).sum();
        }

        @Override
        public int getChildVOSnapshotsCount() {
            return frameQueriesStats.stream().mapToInt(it -> it.getChildVOSnapshotsCount()).sum();
        }

        @Override
        public int getDeepPlusGapsFilled() {
            return frameQueriesStats.stream().mapToInt(it -> it.getDeepPlusGapsFilled()).sum();
        }

        @Override
        public int getDeepPlusGapsLeft() {
            return frameQueriesStats.stream().mapToInt(it -> it.getDeepPlusGapsLeft()).sum();
        }
    }
}

