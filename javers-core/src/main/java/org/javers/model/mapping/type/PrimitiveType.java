package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * Primitive or primitive box
 *
 * @author bartosz walacik
 */
public class PrimitiveType extends JaversType {


   /* public boolean isMappingForJavaType(Type givenType) {
        System.out.println("== "+ baseJavaType +" == "+givenType);
        System.out.println(baseJavaType == givenType);
        return baseJavaType == givenType;
    } */

    public PrimitiveType(Class javaType) {
        super(javaType);
    }
}
