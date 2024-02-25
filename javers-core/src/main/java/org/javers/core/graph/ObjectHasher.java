package org.javers.core.graph;

import java.util.List;

/**
 * @author bartosz.walacik
 */
public interface ObjectHasher {
    String hash(List<LiveCdo> objects);
}
