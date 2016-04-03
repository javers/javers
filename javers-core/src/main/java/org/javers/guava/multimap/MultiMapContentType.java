package org.javers.guava.multimap;

import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.MapContentType;

/**
 * @author akrystian
 */
public class MultiMapContentType extends MapContentType{
    public MultiMapContentType(JaversType keyType, JaversType valueType){
        super(keyType, valueType);
    }
}
