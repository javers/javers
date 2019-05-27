package org.javers.repository.sql.session;

import org.javers.repository.sql.session.KeyGeneratorDefinition.AutoincrementDefinition;
import org.javers.repository.sql.session.KeyGeneratorDefinition.SequenceDefinition;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * forked from org.polyjdbc.core.key.KeyGenerator
 *
 * @author Adam Dubiel
 */
interface KeyGenerator {

    long generateKey(String sequenceName, Session session);

    long getKeyFromLastInsert(Session session);

    void reset();

    class SequenceAllocation implements KeyGenerator {

        private final Object lock = new Object();

        private final SequenceDefinition sequenceDefinition;

        private Map<String, Map<String, Sequence>> sequences = new ConcurrentHashMap();

        private ThreadLocal<Long> lastKey = new ThreadLocal<>();

        SequenceAllocation(SequenceDefinition sequenceDefinition) {
            this.sequenceDefinition = sequenceDefinition;
        }

        @Override
        public long generateKey(String sequenceName, Session session) {
            long nextVal = findSequence(sequenceName, session).nextValue(session);
            lastKey.set(nextVal);
            return nextVal;
        }

        @Override
        public long getKeyFromLastInsert(Session session) {
            return lastKey.get();
        }

        @Override
        public void reset() {
            synchronized (lock) {
                sequences.clear();
            }
        }

        /**
         * For test only
         */
        public Map<String, Map<String, Sequence>> getSequences() {
            return sequences;
        }

        private Sequence findSequence(String sequenceName, Session session) {
            Map<String, Sequence> databaseSequences = findDatabaseSequences(session);
            if (!databaseSequences.containsKey(sequenceName)) {
                synchronized (lock) {
                    //double check, condition could change while obtaining the lock
                    if (!databaseSequences.containsKey(sequenceName)) {
                        databaseSequences.computeIfAbsent(sequenceName, sequence -> new Sequence(sequenceName, sequenceDefinition));
                    }
                }
            }
            return databaseSequences.get(sequenceName);
        }

        private Map<String, Sequence> findDatabaseSequences(Session session) {
            String databaseUrl = extractDatabaseUrl(session);
            if (!sequences.containsKey(databaseUrl)) {
                synchronized (lock) {
                    //double check, condition could change while obtaining the lock
                    if (!sequences.containsKey(databaseUrl)) {
                        sequences.computeIfAbsent(databaseUrl, map -> new ConcurrentHashMap<>());
                    }
                }
            }
            return sequences.get(databaseUrl);
        }

        private String extractDatabaseUrl(Session session) {
            try {
                return session.getConnectionProvider().getConnection().getMetaData().getURL();
            } catch (SQLException e) {
                throw new SqlUncheckedException("fail to retrieve db url", e);
            }
        }
    }

    class AutoincrementGenerator implements KeyGenerator {
        private final AutoincrementDefinition autoincrementDefinition;

        AutoincrementGenerator(AutoincrementDefinition autoincrementDefinition) {
            this.autoincrementDefinition = autoincrementDefinition;
        }

        @Override
        public long generateKey(String sequenceName, Session session) {
            throw new RuntimeException("Not implemented. Can't generate key on AutoIncremented");
        }

        @Override
        public long getKeyFromLastInsert(Session session) {
            return session.executeQueryForLong(new Select("last autoincrementDefinition id", autoincrementDefinition.lastInsertedAutoincrement()));
        }

        public void reset() {
        }
    }
}
