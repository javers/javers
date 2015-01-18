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

/**
 *
 * @author Adam Dubiel
 */
public final class StringUtils {

    private static final int SINGLE_VALUE_LENGTH = 20;

    private StringUtils() {
    }

    public static String concatenate(char separator, Object... values) {
        return concatenate(Character.toString(separator), values);
    }

    public static String concatenate(String separator, Object... values) {
        if (values.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder(values.length * SINGLE_VALUE_LENGTH);
        for (Object value : values) {
            builder.append(value == null ? "" : value.toString()).append(separator);
        }
        StringBuilderUtil.deleteLastCharacters(builder, separator.length());
        return builder.toString();
    }
}
