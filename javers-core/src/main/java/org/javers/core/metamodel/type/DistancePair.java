package org.javers.core.metamodel.type;

import static org.javers.common.reflection.ReflectionUtil.calculateHierarchyDistance;

/**
* @author bartosz walacik
*/
class DistancePair implements Comparable<DistancePair> {
    private final Integer distance;
    private final JaversType javersType;

    DistancePair(Class javaClass, JaversType javersType) {
        this.javersType = javersType;
        distance = calculateHierarchyDistance(javaClass, javersType.getBaseJavaClass());
    }

    @Override
    public int compareTo(DistancePair other) {
        return distance.compareTo(other.distance);
    }

    Integer getDistance() {
        return distance;
    }

    boolean isMax() {
        return distance == Integer.MAX_VALUE;
    }

    JaversType getJaversType() {
        return javersType;
    }
}
