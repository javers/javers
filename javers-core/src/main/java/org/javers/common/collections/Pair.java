package org.javers.common.collections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
public class Pair<L,R> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    public static <L,R> Set<R> collectRightAsSet(List<Pair<L,R>> pairList) {
        Set<R> result = new HashSet<>();
        for (Pair<L,R> pair : pairList) {
            result.add(pair.right);
        }
        return result;
    }
}
