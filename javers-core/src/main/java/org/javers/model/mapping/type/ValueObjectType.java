package org.javers.model.mapping.type;

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
