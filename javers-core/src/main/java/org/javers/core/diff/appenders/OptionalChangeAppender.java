package org.javers.core.diff.appenders;

import org.javers.common.exception.JaversException;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChangeFactory;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.javers.common.exception.JaversExceptionCode.UNSUPPORTED_OPTIONAL_CONTENT_TYPE;

/**
 * @author bartosz.walacik
 */
public class OptionalChangeAppender implements PropertyChangeAppender<PropertyChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof OptionalType;
    }

    @Override
    public PropertyChange calculateChanges(NodePair pair, JaversProperty property) {
        OptionalType optionalType = property.getType();
        JaversType contentType = optionalType.getItemJaversType();

        Optional leftOptional =  normalize((Optional) pair.getLeftDehydratedPropertyValueAndSanitize(property));
        Optional rightOptional = normalize((Optional) pair.getRightDehydratedPropertyValueAndSanitize(property));

        if (Objects.equals(leftOptional, rightOptional)) {
            return null;
        }
        if (contentType instanceof ManagedType) {
            return new ReferenceChange(pair.createPropertyChangeMetadata(property),
                    first(pair.getLeftReferences(property)),
                    first(pair.getRightReferences(property)),
                    flat(pair.getLeftPropertyValue(property)),
                    flat(pair.getRightPropertyValue(property)));
        }
        if (contentType instanceof PrimitiveOrValueType) {
            return ValueChangeFactory.create(pair, property, leftOptional, rightOptional);
        }

        throw new JaversException(UNSUPPORTED_OPTIONAL_CONTENT_TYPE, contentType);
    }

    private GlobalId first(List<GlobalId> refs){
        if (refs != null && refs.size() > 0) {
            return refs.get(0);
        }
        return null;
    }

    private Object flat(Object optional){
        if (optional instanceof Optional) {
            return ((Optional) optional).orElse(null);
        }
        return optional;
    }

    private Optional normalize(Optional optional) {
        if (optional == null) {
            return Optional.empty();
        }
        return optional;
    }
}
