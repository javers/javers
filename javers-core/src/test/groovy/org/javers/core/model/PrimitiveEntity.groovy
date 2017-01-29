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
}

enum SomeEnum { A, B}