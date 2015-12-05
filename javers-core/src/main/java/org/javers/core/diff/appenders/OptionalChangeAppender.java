package org.javers.core.diff.appenders;

import org.javers.common.collections.Function;
import org.javers.common.collections.Objects;
import org.javers.common.exception.JaversException;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

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

    @Override
    public PropertyChange calculateChanges(NodePair pair, Property property) {

        OptionalType optionalType = typeMapper.getPropertyType(property);
        JaversType contentType = typeMapper.getJaversType(optionalType.getItemType());

        Object leftOptional =  optionalType.normalize(pair.getLeftPropertyValue(property));
        Object rightOptional = optionalType.normalize(pair.getRightPropertyValue(property));

        if (contentType instanceof ManagedType){
            GlobalId leftId  =  getAndDehydrate(optionalType, leftOptional, contentType);
            GlobalId rightId = getAndDehydrate(optionalType, rightOptional, contentType);

            if (Objects.nullSafeEquals(leftId, rightId)) {
                return null;
            }
            return new ReferenceChange(pair.getGlobalId(), property.getName(), leftId, rightId);
        }
        if (contentType instanceof PrimitiveOrValueType) {
            if (Objects.nullSafeEquals(leftOptional, rightOptional)) {
                return null;
            }
            return new ValueChange(pair.getGlobalId(), property.getName(), leftOptional, rightOptional);
        }

        throw new JaversException(UNSUPPORTED_OPTIONAL_CONTENT_TYPE, contentType);
    }

    private GlobalId getAndDehydrate(OptionalType optionalType, Object optional, final JaversType contentType){
         return (GlobalId) optionalType.mapAndGet(optional, new Function() {
             public Object apply(Object input) {
                 return globalIdFactory.dehydrate(input, contentType, null);
             }
         });
    }
}
