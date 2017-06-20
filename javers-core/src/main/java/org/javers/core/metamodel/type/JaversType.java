package org.javers.core.metamodel.type;

import java.util.*;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.string.PrettyPrintBuilder;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.annotation.TypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Managed property type
 * <br/><br/>
 *
 * This is a convenient abstraction layer over raw and awkward
 * java.lang.reflect.Type and java.lang.Class
 *
 * @author bartosz walacik
 */
public abstract class JaversType {
    public static final Class DEFAULT_TYPE_PARAMETER = Object.class;

    private final Type  baseJavaType;
    private final Class baseJavaClass;
    private final List<Type> concreteTypeArguments;
    private final String name;

    /**
     * @param baseJavaType Class or ParametrizedType
     */
    JaversType(Type baseJavaType) {
        this(baseJavaType, Optional.<String>empty());
    }

    JaversType(Type baseJavaType, Optional<String> name) {
        this(baseJavaType, name, 0);
    }

    JaversType(Type baseJavaType, Optional<String> name, int expectedArgs) {
        Validate.argumentIsNotNull(baseJavaType);
        Validate.argumentIsNotNull(name);

        this.baseJavaType = baseJavaType;
        this.baseJavaClass = extractClass(baseJavaType);
        this.concreteTypeArguments = Collections.unmodifiableList(
                buildListOfConcreteTypeArguments(baseJavaType, expectedArgs));
        if (name.isPresent()) {
            this.name = name.get();
        }else {
            this.name = extractClass(baseJavaType).getName();
        }
    }

    /**
     * Factory method, delegates to self constructor
     */
    JaversType spawn(Type baseJavaType) {
        try {
            Constructor c = this.getClass().getConstructor(new Class<?>[]{Type.class});
            return (JaversType)c.newInstance(new Object[]{baseJavaType});
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("error calling Constructor for " + this.getClass().getName(), exception);
        }
    }

    public boolean isGenericType() {
        return (baseJavaType instanceof ParameterizedType);
    }

    public Type getBaseJavaType() {
        return baseJavaType;
    }

    public Class getBaseJavaClass() {
        return baseJavaClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JaversType)) return false;

        JaversType that = (JaversType) o;
        return baseJavaType.equals(that.baseJavaType);
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "baseType", baseJavaType);
    }

    @Override
    public int hashCode() {
        return baseJavaType.hashCode();
    }

    /**
     * For generic types, returns a list of actual Class arguments.
     * For example, for Set&lt;String&gt, returns String.
     * Non-concrete (like ?) or missing type arguments like are defaulted to Object.
     * <br/><br/>
     *
     * For array, returns List with {@link Class#getComponentType()}
     */
    public List<Type> getConcreteClassTypeArguments() {
        return concreteTypeArguments;
    }

    private static List<Type> buildListOfConcreteTypeArguments(Type baseJavaType, int expectedSize) {

        List<Type> allTypeArguments = ReflectionUtil.getAllTypeArguments(baseJavaType);

        List<Type> concreteTypeArguments = new ArrayList<>(expectedSize);

        for (int i=0; i<expectedSize; i++) {
            Type existingArgument = null;
            if (!allTypeArguments.isEmpty() && i<allTypeArguments.size()){
                existingArgument = allTypeArguments.get(i);
            }
            concreteTypeArguments.add(getActualClassTypeArgument(existingArgument));
        }

        return concreteTypeArguments;
    }

    private static Type getActualClassTypeArgument(Type existingArgument) {
        if (existingArgument == null) {
             return DEFAULT_TYPE_PARAMETER;
        }

        Optional<Type> concreteType = ReflectionUtil.isConcreteType(existingArgument);
        if (concreteType.isPresent()) {
            return concreteType.get();
        } else {
            return DEFAULT_TYPE_PARAMETER;
        }
    }

    /**
     * Type for JSON representation.
     *
     * For Values it's simply baseJavaType.
     *
     * For ManagedTypes (references to Entities and ValueObjects) it's GlobalId
     * because JaVers serializes references in the 'dehydrated' form.
     */
     Type getRawDehydratedType() {
         return getBaseJavaClass();
     }

    /**
     * Prints this object to String
     */
     public final String prettyPrint(){
         return prettyPrintBuilder().build();
     }

    /**
     * JaversType name, clientsClass.name by default
     * or value of {@link TypeName} annotation.
     */
     public String getName() {
        return name;
     }

     public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);
        return baseJavaClass.isAssignableFrom(cdo.getClass());
     }

    /**
     * Used for comparing as Values
     */
     public boolean equals(Object left, Object right) {
         return Objects.equals(left, right);
     }

     protected PrettyPrintBuilder prettyPrintBuilder(){
         return new PrettyPrintBuilder(this)
                 .addField("baseType", baseJavaType)
                 .addField("typeName", name);
     }
}
