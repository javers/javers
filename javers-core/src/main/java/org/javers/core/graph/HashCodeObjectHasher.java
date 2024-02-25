package org.javers.core.graph;

import java.util.List;
import java.util.stream.Collectors;

import org.javers.common.string.ShaDigest;

public class HashCodeObjectHasher implements ObjectHasher {

    @Override
    public String hash(List<LiveCdo> objects) {
        String hashes = objects.stream()
            .map(cdo -> String.valueOf(cdo.getWrappedCdo().orElse(cdo).hashCode()))
            .collect(Collectors.joining());
        return ShaDigest.longDigest(hashes);
    }

}
