package org.javers.core.diff.appenders.levenshtein;

import org.javers.common.validation.Validate;
import org.javers.core.diff.EqualsFunction;
import org.javers.core.diff.appenders.ListChangeAppender;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;
import java.util.Objects;

/**
 * @author kornel kielczewski
 */
public class LevenshteinListChangeAppender extends ListChangeAppender {

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    LevenshteinListChangeAppender(TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        Validate.argumentsAreNotNull(typeMapper, globalIdFactory);
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {
        CollectionType listType = property.getType();
        JaversType itemType = typeMapper.getJaversType(listType.getItemType());
        DehydrateContainerFunction dehydrateFunction = new DehydrateContainerFunction(itemType, globalIdFactory);
        OwnerContext owner = new PropertyOwnerContext(affectedId, property.getName());

        final List leftList =  (List) listType.map(leftValue, dehydrateFunction, owner);
        final List rightList = (List) listType.map(rightValue, dehydrateFunction, owner);

        EqualsFunction equalsFunction = (left, right) -> Objects.equals(left, right);
        Backtrack backtrack = new Backtrack(equalsFunction);
        StepsToChanges stepsToChanges = new StepsToChanges(equalsFunction);

        final BacktrackSteps[][] steps = backtrack.evaluateSteps(leftList, rightList);
        final List<ContainerElementChange> changes = stepsToChanges.convert(steps, leftList, rightList);

        ListChange result = getListChange(affectedId, property, changes);
        if (result != null) {
            renderNotParametrizedWarningIfNeeded(listType.getItemType(), "item", "List", property);
        }
        return result;
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
