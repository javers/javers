package org.javers.core.diff.appenders;

import org.javers.common.exception.JaversException;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.*;

import java.util.Objects;
import java.util.Optional;

import static org.javers.common.exception.JaversExceptionCode.UNSUPPORTED_OPTIONAL_CONTENT_TYPE;

/**
 * @author bartosz.walacik
 */
public class OptionalChangeAppender extends CorePropertyChangeAppender<PropertyChange> {

    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;

    public OptionalChangeAppender(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
    }

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof OptionalType;
    }

    public PropertyChange calculateChanges(Object leftValue, Object rightValue, GlobalId affectedId, JaversProperty property) {

        OptionalType optionalType = ((JaversProperty) property).getType();
        JaversType contentType = typeMapper.getJaversType(optionalType.getItemType());

        Optional leftOptional =  normalize((Optional) leftValue);
        Optional rightOptional = normalize((Optional) rightValue);

        if (contentType instanceof ManagedType){
            GlobalId leftId  =  getAndDehydrate(leftOptional, contentType);
            GlobalId rightId = getAndDehydrate(rightOptional, contentType);

            if (Objects.equals(leftId, rightId)) {
                return null;
            }
            return new ReferenceChange(affectedId, property.getName(), leftId, rightId,
                    leftValue, rightValue);
        }
        if (contentType instanceof PrimitiveOrValueType) {
            if (leftOptional.equals(rightOptional)) {
                return null;
            }
            return new ValueChange(affectedId, property.getName(), leftOptional, rightOptional);
        }

        throw new JaversException(UNSUPPORTED_OPTIONAL_CONTENT_TYPE, contentType);
    }

    private GlobalId getAndDehydrate(Optional optional, final JaversType contentType){
         return (GlobalId) optional
                 .map(o -> globalIdFactory.dehydrate(o, contentType, null))
                 .orElse(null);
    }

    private Optional normalize(Optional optional) {
        if (optional == null) {
            return Optional.empty();
        }
        return optional;
    }
}
