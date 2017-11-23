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

/**
 * Managed property type
 * <br/><br/>
 *
 * This is the convenient abstraction layer awkward
 * java.lang.reflect.Type and java.lang.Class
 *
 * @author bartosz walacik
 */
public abstract class JaversType {
    public static final Class DEFAULT_TYPE_PARAMETER = Object.class;
    private final Type  baseJavaType;
    private final List<Type> concreteTypeArguments;
    private final String name;

    JaversType(Type baseJavaType) {
        this(baseJavaType, Optional.empty(), 0);
    }

    JaversType(Type baseJavaType, Optional<String> name, int expectedArgs) {
        Validate.argumentsAreNotNull(baseJavaType, name);
        this.baseJavaType = baseJavaType;
        this.name = name.orElse(baseJavaType.getTypeName());
        this.concreteTypeArguments = Collections.unmodifiableList(
                buildListOfConcreteTypeArguments(baseJavaType, expectedArgs));
    }

    /**
     * Factory method, delegates to self constructor
     */
    JaversType spawn(Type baseJavaType) {
        try {
            Constructor c = this.getClass().getConstructor(Type.class);
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

    /**
     * JaversType name, clientsClass.name by default
     * or value of {@link TypeName} annotation.
     */
    public String getName() {
        return name;
    }

    public abstract boolean isInstance(Object cdo);

    public abstract boolean canBePrototype();

    /**
     * Used for comparing as Values
     */
    public boolean equals(Object left, Object right) {
        return Objects.equals(left, right);
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

    /**
     * Prints this object to String
     */
    public final String prettyPrint(){
        return prettyPrintBuilder().build();
    }

    protected PrettyPrintBuilder prettyPrintBuilder(){
        return new PrettyPrintBuilder(this)
                .addField("baseType", baseJavaType)
                .addField("typeName", getName());
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
}
