package org.javers.common.string;

import static org.javers.common.validation.Validate.argumentCheck;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class ToStringBuilder {
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
