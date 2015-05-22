package org.javers.core.diff.appenders.levenshtein;

import org.javers.common.collections.Objects;
import org.javers.common.validation.Validate;
import org.javers.core.diff.EqualsFunction;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.List;

/**
 * @author kornel kiełczewski
 */
public class LevenshteinListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    LevenshteinListChangeAppender(TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        Validate.argumentsAreNotNull(typeMapper, globalIdFactory);
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(final NodePair pair, final Property property) {

        final List leftList = (List) pair.getLeftPropertyValue(property);
        final List rightList = (List) pair.getRightPropertyValue(property);

        EqualsFunction equalsFunction = createDehydratingEqualsFunction(pair, property);
        Backtrack backtrack = new Backtrack(equalsFunction);
        StepsToChanges stepsToChanges = new StepsToChanges(equalsFunction);

        final BacktrackSteps[][] steps = backtrack.evaluateSteps(leftList, rightList);
        final List<ContainerElementChange> changes = stepsToChanges.convert(steps, leftList, rightList);

        return getListChange(pair.getGlobalId(), property, changes);
    }

    private EqualsFunction createDehydratingEqualsFunction(NodePair pair, Property property){
        ListType listType = typeMapper.getPropertyType(property);
        final JaversType listContentType = typeMapper.getJaversType(listType.getItemType());
        final OwnerContext owner = new OwnerContext(pair.getGlobalId(), property.getName());
        return new EqualsFunction() {
            public boolean nullSafeEquals(Object left, Object right) {
                Object leftDehydrated = globalIdFactory.dehydrate(left, listContentType, owner);
                Object rightDehydrated = globalIdFactory.dehydrate(right, listContentType, owner);
                return Objects.nullSafeEquals(leftDehydrated, rightDehydrated);
            }
        };
    }

    private ListChange getListChange(final GlobalId affectedCdoId, final Property property,
                                     final List<ContainerElementChange> changes) {
        final ListChange result;

        if (changes.size() == 0) {
            result = null;
        } else {
            result = new ListChange(affectedCdoId, property, changes);
        }
        return result;
    }

}
