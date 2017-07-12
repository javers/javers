package org.javers.core.diff.appenders.levenshtein;

import org.javers.common.validation.Validate;
import org.javers.core.diff.EqualsFunction;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.CorePropertyChangeAppender;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;
import java.util.Objects;

import static org.javers.common.collections.Lists.wrapNull;

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
    public ListChange calculateChanges(final NodePair pair, final JaversProperty property) {

        final List leftList =  wrapNull( (List) pair.getLeftPropertyValue(property) );
        final List rightList = wrapNull((List) pair.getRightPropertyValue(property));

        EqualsFunction equalsFunction = createDehydratingEqualsFunction(pair, property);
        Backtrack backtrack = new Backtrack(equalsFunction);
        StepsToChanges stepsToChanges = new StepsToChanges(equalsFunction);

        final BacktrackSteps[][] steps = backtrack.evaluateSteps(leftList, rightList);
        final List<ContainerElementChange> changes = stepsToChanges.convert(steps, leftList, rightList);

        ListChange result = getListChange(pair.getGlobalId(), property, changes);
        if (result != null) {
            ListType listType = ((JaversProperty) property).getType();
            renderNotParametrizedWarningIfNeeded(listType.getItemType(), "item", "List", property);
        }
        return result;
    }

    private EqualsFunction createDehydratingEqualsFunction(NodePair pair, Property property){
        ListType listType = ((JaversProperty) property).getType();
        final JaversType listContentType = typeMapper.getJaversType(listType.getItemType());
        final OwnerContext owner = new PropertyOwnerContext(pair.getGlobalId(), property.getName());
        return new EqualsFunction() {
            public boolean nullSafeEquals(Object left, Object right) {
                Object leftDehydrated = globalIdFactory.dehydrate(left, listContentType, owner);
                Object rightDehydrated = globalIdFactory.dehydrate(right, listContentType, owner);
                return Objects.equals(leftDehydrated, rightDehydrated);
            }
        };
    }

    private ListChange getListChange(final GlobalId affectedCdoId, final Property property,
                                     final List<ContainerElementChange> changes) {
        final ListChange result;

        if (changes.size() == 0) {
            result = null;
        } else {
            result = new ListChange(affectedCdoId, property.getName(), changes);
        }
        return result;
    }
}
