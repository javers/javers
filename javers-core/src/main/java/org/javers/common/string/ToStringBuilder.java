package org.javers.common.string;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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

    public static String setToString(Set set){
        return listToString(new ArrayList(set));
    }

    public static String listToString(List list){
        return "[" + join(list) + "]";
    }

    public static String join(Collection list){
        if (list == null || list.size() == 0){
            return "";
        }

        StringBuilder out = new StringBuilder();

        boolean first = true;
        for (Object it : list) {
            if (!first) {
                out.append( ",");
            }
            out.append(format(it) );
            first = false;
        }

        return out.toString();
    }

    public static String toStringSimple(Object... fieldsMap){
        return toString(", ", fieldsMap);
    }

    private static String toString(String sep, Object... fieldsMap){
        argumentCheck(fieldsMap.length % 2 == 0, "map expected");

        StringBuilder out = new StringBuilder();
        boolean first = true;
        for (int i=0; i<fieldsMap.length; i+=2){
            Object name = fieldsMap[i];
            Object value = fieldsMap[i+1];

            if (isNullOrEmpty(value)) continue;

            if (first){
                out.append(addFirstField(name + "", value));
            }
            else{
                out.append(addField(name + "", value, sep));
            }
            first = false;
        }

        return out.toString();
    }

    public static String format(Object value) {
        if ( value instanceof Set) return ToStringBuilder.setToString((Set)value);
        if ( value instanceof List) return ToStringBuilder.listToString((List)value);
        if ( value instanceof Optional) {
          if ( ((Optional)value).isPresent()) {
              return format(((Optional) value).get());
          } else {
              return "empty";
          }
        }
        return "'"+value+"'";
    }

    private static boolean isNullOrEmpty(Object value) {
        if ( value == null) return true;
        if ( value instanceof Boolean) return !(boolean)value;
        if ( value instanceof Integer) return ((Integer)value) == 0;
        if ( value instanceof Optional)  return !((Optional)value).isPresent();
        if ( value instanceof String)  return ((String)value).isEmpty();
        if ( value instanceof Collection) return ((Collection)value).isEmpty();
        if ( value instanceof Map) return ((Map)value).isEmpty();
        return false;
    }

    public static String toStringBlockStyle(Object instance, String baseIndent, Object... fieldsMap) {
        argumentIsNotNull(instance);
        String indent = baseIndent + "\n  "+baseIndent;
        String lastIndent = baseIndent + "\n"+baseIndent;
        return instance.getClass().getSimpleName() + "{" + indent + toString(indent, fieldsMap) + lastIndent + "}";
    }

    public static String toString(Object instance, Object... fieldsMap) {
         argumentIsNotNull(instance);
         return instance.getClass().getSimpleName()+"{ "+toStringSimple(fieldsMap)+" }";
     }

    public static String addField(String fieldName, Object value) {
        return addField(fieldName, value, ", ");
    }

    public static String addFirstField(String fieldName, Object value) {
        return addField(fieldName, value, "");
    }

    public static String addField(String fieldName, Object value, String separator) {
        return separator + fieldName + ": " + format(value);
    }

    public static String addEnumField(String fieldName, Object value) {
        return ", "+fieldName+": ["+ (value != null ? value.toString() : "null")+"]";
    }
}
