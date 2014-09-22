package org.javers.core;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ValueObject;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.metamodel.type.ValueType;
import org.javers.core.graph.ObjectGraphBuilder;

/**
 * <h1>Why domain model mapping?</h1>
 * Many frameworks which deal with user domain model (aka data model) use some kind of <b>mapping</b>.
 * For example JPA uses annotations in order to map user classes into relational database.
 * Plenty of XML and JSON serializers uses various approaches to mapping, usually based on annotations.
 * <br><br>
 *
 * When combined together, all of those framework-specific annotations could be a pain and
 * pollution in Your business domain code.
 * <br><br>
 *
 * Mapping is also a case in JaVers but don't worry:
 * <ul>
 *     <li>It's far more simple than JPA</li>
 *     <li>Javers uses reasonable defaults and take advantage of type inferring algorithm.
 *          So for a quick start just let it do the mapping for You.
 *          Later on, it would be advisable to refine it to optimize diff semantics</li>
 *     <li>We believe that domain model classes should be framework agnostic,
 *          so we do not ask You to embrace another annotation set</li>
 * </ul>
 *
 * JaVers wants to know only a few basic facts about your domain model classes,
 * particularly Javers Type of each class spotted in runtime.
 * Proper mapping is essential for diff algorithm, for example we need to know if objects of given class
 * should be compared property-by-property or using equals().
 *
 * <h2>Javers Types</h2>
 * We use <b>Entity</b> and <b>Value Objects</b> notions following Eric Evans
 * Domain Driven Design terminology (DDD).
 * Furthermore, we use <b>Values</b>, <b>Primitives</b> and <b>Containers</b>.
 * The last two types are internals and can't be mapped by user.
 * <br><br>
 *
 * To make long story short, You as a user are asked to label your domain model classes as
 * Entities, Value Objects or Values.
 * <br><br>
 *
 * Do achieve this, use {@link JaversBuilder} methods:
 * <ul>
 *     <li>{@link JaversBuilder#registerEntity(Class)}</li>
 *     <li>{@link JaversBuilder#registerValueObject(Class)}</li>
 *     <li>{@link JaversBuilder#registerValue(Class)}</li>
 * </ul>
 *
 * Let's examine these three fundamental types more closely.
 *
 * <h3>Entity</h3>
 * JaVers {@link Entity} has exactly the same semantic like DDD Entity or JPA Entity.
 * <br><br>
 *
 * Usually, each entity instance represents concrete physical object.
 * Entity has a list of mutable properties and its own <i>identity</i> hold in <i>id property</i>.
 * Entity can contain ValueObjects, References (to entity instances), Containers, Values & Primitives.
 * <br><br>
 *
 * For example Entities are: Person, Company.
 *
 * <h3>Value Object</h3>
 * JaVers {@link ValueObject} is similar to DDD ValueObject and JPA Embeddable.
 * It's a complex value holder with a list of mutable properties but no unique identifier.
 * <br><br>
 *
 * In strict DDD approach, Value Objects can't exists independently and have to be bound do Entity instances
 * (as a part of an Aggregate). Javers is not such radical and supports both embedded and dangling Value Objects.
 * So in JaVers, ValueObject is just Entity without identity.
 * <br><br>
 *
 * For example Value Objects are: Address, Point
 *
 * <h3>Value</h3>
 * JaVers {@link ValueType} is a simple (scalar) value holder.
 * Two Values are compared using equals() so
 * its highly important to implement it properly by comparing underlying state.
 * <br><br>
 *
 * For example Values are: BigDecimal, LocalDate
 * <br><br>
 *
 * For Values it's advisable to customize JSON serialization by implementing Type Adapters, see {@link JsonConverter}.
 *
 * <h1>TypeMapper and type inferring policy</h1>
 * Javers use lazy approach to type mapping so types are resolved only for classes spotted in runtime.
 * <br><br>
 *
 * To show You how it works, assume that Javers is calculating diff on two graphs of objects
 * and currently two Person.class instances are compared.
 * {@link ObjectGraphBuilder} asks {@link TypeMapper} about {@link JaversType} of Person.class.
 * <br><br>
 *
 * {@link TypeMapper} does the following
 * <ul>
 *     <li>If Person.class was spotted before in the graphs, TypeMapper has exact mapping for it and just returns already known JaversType</li>
 *     <li>If this is a first question about Person.class, TypeMapper checks if it was registered in {@link JaversBuilder}
 *          as one of Entity, Value Object or Value. If so, answer is easy.</li>
 *     <li>Then TypeMapper tries to find so called Prototype&mdash;nearest class or interface that is already mapped and is assignable from Person.class.
 *          So as You can see, it's easy to map whole bunch of classes with common superclass or interface with one call to {@link JaversBuilder}.
 *          Just register those high level concepts.</li>
 *     <li>When Prototype is not found, Javers tries to infer Type by looking for Id annotation at property level
 *         (only the annotation class name is important, package is not checked,
 *          so you can use well known javax.persistence.Id or custom annotation).
 *         If @Id is found, class would be mapped as an Entity, otherwise as a ValueObject.
 * </ul>
 *
 * To summarize, identify Entities and Value Objects and Values in your domain model.
 * Try to distinct them by high level abstract classes, interfaces or JPA annotations.
 * Minimize your {@link JaversBuilder} configuration by taking advantage of type inferring policy.
 * For Values, remember about implementing equals() and consider implementing JSON type adapters.
 *
 * @author bartosz walacik
 */
public abstract class MappingDocumentation {
}
