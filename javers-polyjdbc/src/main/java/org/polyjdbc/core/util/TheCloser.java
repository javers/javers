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
package org.polyjdbc.core.util;

import java.io.Closeable;
import java.io.IOException;
import org.polyjdbc.core.query.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Adam Dubiel
 */
public final class TheCloser {

    private static final Logger logger = LoggerFactory.getLogger(TheCloser.class);

    private TheCloser() {
    }

    public static void close(Closeable... toClose) {
        try {
            for (Closeable closeable : toClose) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException exception) {
            logger.warn("Failed to close resource", exception);
        }
    }

    public static void rollback(QueryRunner... toRollback) {
        for (QueryRunner closeable : toRollback) {
            if (closeable != null) {
                closeable.rollback();
            }
        }
    }
    
    public static void commit(QueryRunner... toCommit) {
        for (QueryRunner closeable : toCommit) {
            if (closeable != null) {
                closeable.commit();
            }
        }
    }
}
