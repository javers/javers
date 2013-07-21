package pl.edu.icm.sedno.common.model;

public enum ObjectState {
    /**
     * nowy, przed nadaniem PK, niedołączony do bieżącego persistent context'u
     */
    TRANSIENT, 
    /**
     * dołączony do bieżącego persistent context
     */
    PERSISTENT, 
    /**
     * odłączony od bieżącego persistent context'u
     */
    DETACHED
}