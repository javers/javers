/*
 * Copyright 2013 Adam Dubiel.
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

package org.polyjdbc.core.type;

import java.util.Date;

/**
 *
 * @author Adam Dubiel
 */
public class Timestamp implements TypeWrapper {

    private final Date timestamp;

    private Timestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public static Timestamp from(Date date) {
        return new Timestamp(date);
    }

    public static Timestamp from(long millis) {
        return new Timestamp(new Date(millis));
    }

    public Object value() {
        return timestamp;
    }

}
