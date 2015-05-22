package org.javers.common.string;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentCheck;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class ToStringBuilder {
    public static String typeName(Type type){
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            parameterizedType.getActualTypeArguments();

            StringBuilder out = new StringBuilder();
            out.append(typeName(parameterizedType.getRawType()) + "<");
            Type[] args  = ((ParameterizedType)type).getActualTypeArguments();

            out.append( typeName(args[0]));
            for (int i=1;i<args.length;i++){
                out.append(", "+typeName(args[0]) );
            }
            out.append(">");
            return out.toString();
        } else if (type instanceof Class) {
            return ((Class)type).getSimpleName();
        } else {
            return type.toString();
        }
    }


    public static String toStringSimple(Object... fieldsMap){
        argumentCheck(fieldsMap.length % 2 == 0, "map expected");

        StringBuilder out = new StringBuilder();
        for (int i=0; i<fieldsMap.length; i+=2){
            if (i==0){
                out.append( addFirstField(fieldsMap[i]+"",fieldsMap[i+1]) );
            }
            else{
                out.append( addField(fieldsMap[i]+"",fieldsMap[i+1]) );
            }
        }

        return out.toString();
    }

    public static String toString(Object instance, Object... fieldsMap){
         argumentIsNotNull(instance);

         return instance.getClass().getSimpleName()+"{"+toStringSimple(fieldsMap)+"}";
     }

    public static String addField(String fieldName, Object value) {
        return ", "+addFirstField(fieldName, value);
    }

    public static String addFirstField(String fieldName, Object value) {
        return fieldName+":'"+ (value != null ? value.toString() : "")+"'";
    }

    public static String addEnumField(String fieldName, Object value) {
        return ", "+fieldName+":["+ (value != null ? value.toString() : "")+"]";
    }
}
