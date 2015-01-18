package org.polyjdbc.core.dialect;

/**
 *
 * @author Adam Dubiel
 */
public class OracleDialectTypes extends DefaultDialectTypes {

    @Override
    public String string(int characters) {
        return "VARCHAR2(" + characters + ")";
    }

    @Override
    public String number(int integerPrecision, int decimalPrecision) {
        return "NUMBER(" + integerPrecision + "," + decimalPrecision + ")";
    }

    @Override
    public String floatType() {
        return "REAL";
    }
}
