package org.javers.core.metamodel.type;

import static org.javers.common.reflection.ReflectionUtil.calculateHierarchyDistance;

/**
* @author bartosz walacik
*/
class DistancePair implements Comparable<DistancePair> {
    Integer distance;
    JaversType javersType;
    Class javaClass;

    DistancePair(Class javaClass, JaversType javersType) {
        this.javaClass = javaClass;
        this.javersType = javersType;
        distance = calculateHierarchyDistance(javaClass, javersType.getBaseJavaClass());
    }

    @Override
    public int compareTo(DistancePair other) {
        return distance.compareTo(other.distance);
    }

    boolean isMax() {
        return distance == Integer.MAX_VALUE;
    }
}
