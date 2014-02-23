package org.javers.core;

import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ValueObject;
import org.javers.core.metamodel.type.ValueType;

/**
 * <h1>Why domain model mapping?</h1>
 * Many frameworks which deal with user domain model (aka data model) use some kind of <b>mapping</b>.
 * For example JPA uses object-relational mapping in order to map user classes into relational database.
 * <br/>
 * This is also a case in JaVers but don't worry, it's far more simple than ORM.
 * JaVers wants to know only a few basic facts about your classes, particularly Javers Type of each class spotted
 * in your domain model.
 *
 * <h2>Javers Type</h2>
 * We use <b>Entity</b> and <b>Value Objects</b> notions following Eric Evans DDD terminology.
 * Furthermore, we use <b>Values</b>, <b>Primitives</b> and <b>Containers</b>
 *
 * <h3>Entity</h3>
 * JaVers {@link Entity} has exactly the same semantic like DDD Entity or JPA Entity.
 * <br/>
 * Usually, each entity instance represents concrete physical object.
 * Entity has a list of mutable properties and its own identity hold in id property.
 * <br/>
 * For example Entities are: Person, Company.
 *
 * <h3>Value Object</h3>
 * JaVers {@link ValueObject} is similar to DDD ValueObject and JPA Embeddable.
 * It's a complex value holder with a list of mutable properties but no unique identifier.
 * <br/>
 * For example Value Objects are: Address, Point (x,y)
 * <br/>
 * Value Object is a default type in Javers type inferring policy (TODO link)
 *
 *
 * <h3>Value</h3>
 * JaVers {@link ValueType} is a simple value holder
 *
 * <h1>Type inferring policy</h1>
 *
 * @author bartosz walacik
 */
public interface MappingDocumentation {
}
