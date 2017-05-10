package org.javers.core.examples.typeNames;

import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import java.math.BigDecimal;

/**
 * @author bartosz.walacik
 */
@TypeName("myName")
@Entity
public class OldEntityWithTypeAlias {
    @Id
    private BigDecimal id;

    private int val;

    private NewValueObjectWithTypeAlias valueObject;

    @Id
    public BigDecimal getId() {
        return id;
    }

    public int getVal() {
        return val;
    }

    public NewValueObjectWithTypeAlias getValueObject() {
        return valueObject;
    }
}
