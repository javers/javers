package org.javers.core.model

import groovy.transform.EqualsAndHashCode

import javax.persistence.Id

/**
 * @author bartosz walacik
 */
@EqualsAndHashCode
class PrimitiveEntity {
    @Id
    private String   id = "a"
    private int      intField
    private long     longField
    private double   doubleField
    private float    floatField
    private char     charField
    private byte     byteField
    private short    shortField
    private boolean  booleanField

    private Integer  IntegerField
    private Long     LongField
    private Double   DoubleField
    private Float    FloatField
    private Byte     ByteField
    private Short    ShortField
    private Boolean  BooleanField

    private SomeEnum someEnum

    @Id
    String getId() {
        return id
    }

    int getIntField() {
        return intField
    }

    long getLongField() {
        return longField
    }

    double getDoubleField() {
        return doubleField
    }

    float getFloatField() {
        return floatField
    }

    char getCharField() {
        return charField
    }

    byte getByteField() {
        return byteField
    }

    short getShortField() {
        return shortField
    }

    boolean getBooleanField() {
        return booleanField
    }

    SomeEnum getSomeEnum() {
        return someEnum
    }

    Integer getIntegerField() {
        return IntegerField
    }
}

enum SomeEnum { A, B}