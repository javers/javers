package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
import org.javers.common.string.PrettyPrintBuilder;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.annotation.TypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.javers.common.reflection.ReflectionUtil.extractActualClassTypeArguments;
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
    private final Type  baseJavaType;
    private final Class baseJavaClass;
    private final List<Type> actualTypeArguments;
    private final String name;

    /**
     * @param baseJavaType Class or ParametrizedType
     */
    JaversType(Type baseJavaType) {
        this(baseJavaType, Optional.<String>empty());
    }

    JaversType(Type baseJavaType, Optional<String> name) {
        Validate.argumentIsNotNull(baseJavaType);
        Validate.argumentIsNotNull(name);

        this.baseJavaType = baseJavaType;
        this.baseJavaClass = extractClass(baseJavaType);
        this.actualTypeArguments = extractActualClassTypeArguments(baseJavaType);
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
        if (o == null || !(o instanceof JaversType)) return false;

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
     * For generic types, returns a list of actual Class (or generic Class) arguments.
     * For example, for Set&lt;String&gt, returns [String.class].
     * <p/>
     *
     * For raw types like Set, returns empty List.
     * <p/>
     *
     * Skips unbounded type parameters like
     * &lt;E&gt;, &lt;?&gt;.
     * <p/>
     *
     * For array, returns List with {@link Class#getComponentType()}
     * <p/>
     */
     public List<Type> getActualTypeArguments(){
         return Collections.unmodifiableList(actualTypeArguments);
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

     protected PrettyPrintBuilder prettyPrintBuilder(){
         return new PrettyPrintBuilder(this)
                 .addField("baseType", baseJavaType)
                 .addField("typeName", name);
     }
}
