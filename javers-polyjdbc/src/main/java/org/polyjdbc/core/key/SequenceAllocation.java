/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.polyjdbc.core.key;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.transaction.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Adam Dubiel
 */
public class SequenceAllocation implements KeyGenerator {

    private static final long SEQUENCE_ALLOCATION_SIZE = 100;

    private final Object lock = new Object();

    private final Dialect dialect;

    private Map<String, Sequence> sequences = new ConcurrentHashMap<String, Sequence>();

    private ThreadLocal<Long> lastKey = new ThreadLocal<Long>();

    public SequenceAllocation(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public long generateKey(String sequenceName, Transaction transaction) throws SQLException {
        Sequence sequence = findSequence(sequenceName);
        if (sequence.recalculationNeeded()) {
            synchronized (lock) {
                long currentSequenceValue = fetchSequenceValue(sequenceName, transaction);
                sequence.recalculate(currentSequenceValue);
            }
        }
        lastKey.set(sequence.nextValue());
        return lastKey.get();
    }

    private Sequence findSequence(String sequenceName) {
        if (sequences.containsKey(sequenceName)) {
            return sequences.get(sequenceName);
        } else {
            Sequence sequence = new Sequence(sequenceName, SEQUENCE_ALLOCATION_SIZE);
            sequences.put(sequenceName, sequence);
            return sequence;
        }
    }

    private long fetchSequenceValue(String sequenceName, Transaction transaction) throws SQLException {
        PreparedStatement statement = transaction.prepareStatement(dialect.nextFromSequence(sequenceName));
        ResultSet resultSet = statement.executeQuery();
        transaction.registerCursor(resultSet);

        resultSet.next();
        return resultSet.getLong(1);
    }

    @Override
    public long getKeyFromLastInsert(Transaction transaction) {
        return lastKey.get();
    }

    public void reset() {
        synchronized (lock) {
            sequences.clear();
        }
    }
}
