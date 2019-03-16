package org.javers.core.diff;

import org.javers.core.graph.ObjectGraph;

import java.util.Collections;

class EmptyGraph extends ObjectGraph {
    EmptyGraph() {
        super(Collections.emptySet());
    }
}

