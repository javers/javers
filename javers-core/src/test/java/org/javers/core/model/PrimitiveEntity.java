package org.javers.core.model;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class PrimitiveEntity {
    @Id
    private int      intField;
    private long     longField;
    private double   doubleField;
    private float    floatField;
    private char     charField;
    private byte     byteField;
    private short    shortField;
    private boolean  booleanField;
}
