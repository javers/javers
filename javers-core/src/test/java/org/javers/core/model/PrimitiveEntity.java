package org.javers.core.model;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class PrimitiveEntity {
    @Id
    private String   id;
    private int      intField;
    private long     longField;
    private double   doubleField;
    private float    floatField;
    private char     charField;
    private byte     byteField;
    private short    shortField;
    private boolean  booleanField;

    private Integer  IntegerField;
    private Long     LongField;
    private Double   DoubleField;
    private Float    FloatField;
    private Byte     ByteField;
    private Short    ShortField;
    private Boolean  BooleanField;
}
