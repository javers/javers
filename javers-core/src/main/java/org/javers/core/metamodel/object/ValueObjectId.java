package org.javers.core.metamodel.object;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.type.EntityType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * ValueObject global unique identifier.
 * <br> <br>
 *
 * Since ValueObjects doesn't have public Id,
 * they are identified by <i>fragment path</i> in the context of owning Entity instance.
 *
 * @author bartosz walacik
 */
public class ValueObjectId extends GlobalId {
    private static final String SEGMENT_SEP = "/";
    private final GlobalId ownerId;
    private final String fragment;

    public ValueObjectId(String typeName, GlobalId ownerId, String fragment) {
        super(typeName);
        argumentsAreNotNull(ownerId, fragment);
        this.ownerId = ownerId;
        this.fragment = fragment;
    }

    /**
     * Path to ValueObject, should be unique in the Entity <b>instance</b> scope.
     * Usually, property name.
     * It works like <i>fragment identifier</i> in URL
     */
    public String getFragment() {
        return fragment;
    }

    public boolean hasOwnerOfType(EntityType entityType){
        return ownerId.getTypeName().equals(entityType.getName());
    }

    public GlobalId getOwnerId() {
        return ownerId;
    }

    @Override
    public String value() {
        return getOwnerId().value() +"#"+ fragment;
    }

    public Set<ValueObjectId> getParentValueObjectIds() {
        List<String> segments = segments();
        if (segments.size() == 1) {
            return Collections.emptySet();
        }

        return segments.stream()
                .limit(segments.size()-1)
                .map(s -> new ValueObjectId(this.getTypeName(), ownerId, s))
                .collect(Collectors.toSet());
    }

    private List<String> segments() {
        String[] segments = fragment.split(SEGMENT_SEP);
        List<String> joinedSegments = Lists.asList(segments[0]);

        if (segments.length > 1) {
            for (int i = 1; i < segments.length; i++) {
                joinedSegments.add(joinedSegments.get(i - 1) + SEGMENT_SEP + segments[i]);
            }
        }

        return joinedSegments;
    }

    @Override
    public String toString() {
        return getOwnerId().toString() +"#"+ fragment;
    }
}
