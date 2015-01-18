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
package org.polyjdbc.core.query.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.polyjdbc.core.exception.QueryLoaderException;

/**
 *
 * @author Adam Dubiel
 */
public class ClasspathQueryLoader implements QueryLoader {

    @Override
    public String getQuery(String resourceName) {
        InputStream stream;
        BufferedReader fileStream = null;
        try {
            stream = this.getClass().getResourceAsStream(resourceName);
            if (stream == null) {
                throw new IOException("resource " + resourceName + " not found");
            }
            fileStream = new BufferedReader(new InputStreamReader(stream));

            return readAsString(fileStream);
        } catch (IOException exception) {
            throw new QueryLoaderException("CLASSPATH_LOADING_ERROR", "Exception wile reading query from file " + resourceName, exception);
        } finally {
            closeStream(fileStream);
        }
    }

    private void closeStream(BufferedReader stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException exception) {
            throw new QueryLoaderException("RESOURCE_CLEANUP_ERROR", "Failed to close stream after reading schema definition!", exception);
        }
    }

    private String readAsString(BufferedReader source) throws IOException {
        StringWriter target = new StringWriter();
        String line = source.readLine();
        while (line != null) {
            target.write(line + "\n");
            line = source.readLine();
        }

        return target.toString();
    }
}
