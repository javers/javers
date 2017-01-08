package org.javers.guava;

import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.MapContentType;

/**
 * @author akrystian
 */
class MultiMapContentType extends MapContentType{
    public MultiMapContentType(JaversType keyType, JaversType valueType){
        super(keyType, valueType);
    }
}
