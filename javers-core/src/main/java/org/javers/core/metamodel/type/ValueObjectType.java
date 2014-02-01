package org.javers.core.metamodel.type;

/**
 * @author bartosz walacik
 */
//TODO merge with ValueObject
@Deprecated
public class ValueObjectType extends JaversType{

    public ValueObjectType(Class voClass){
        super(voClass);
    }
}
