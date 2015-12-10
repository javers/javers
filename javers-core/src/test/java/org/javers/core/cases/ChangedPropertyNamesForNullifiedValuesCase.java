package org.javers.core.cases;

import org.fest.assertions.api.Assertions;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.junit.Test;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


/**
 * see https://github.com/javers/javers/issues/213
 * @author bartosz.walacik
 */
public class ChangedPropertyNamesForNullifiedValuesCase {

    @Test
    public void shouldCalculateChangedPropertyNamesForNullifiedValues() {
        //given
        Javers javers = JaversBuilder.javers().build();
        SimpleTypes obj = new SimpleTypes("1");
        javers.commit("anonymous", obj);

        //when
        obj.shortNumber = -1;
        javers.commit("anonymous", obj);
        CdoSnapshot s = javers.getLatestSnapshot("1", SimpleTypes.class).get();

        //then
        Assertions.assertThat(s.getChanged()).containsExactly("shortNumber");

        //when
        obj.nullify();
        javers.commit("anonymous", obj);
        s = javers.getLatestSnapshot("1", SimpleTypes.class).get();

        //then
        Assertions.assertThat(s.getChanged()).hasSize(11);
    }

    static class SimpleTypes {
        @Id
        String id;

        enum TestEnum { ONE, TWO }

        Date date = new Date();
        java.sql.Date dateSql = new java.sql.Date(date.getTime());
        Timestamp ts = new Timestamp(date.getTime());
        String text = "test";
        Boolean bool = true;
        Long longNumber = 1l;
        Integer integerNumber = 1;
        Short shortNumber = 1;
        Double doubleNumber = 1.0;
        Float floatNumber = 1.0f;
        BigDecimal bigDecimalNumber = BigDecimal.ONE;
        byte byteFiled = 0x1;
        TestEnum testEnum = TestEnum.ONE;

        public SimpleTypes(String id) {
            this.id = id;
        }

        public void nullify() {
            date = null;
            ts = null;
            text = null;
            bool = null;
            longNumber = null;
            integerNumber = null;
            shortNumber = null;
            doubleNumber = null;
            floatNumber = null;
            bigDecimalNumber = null;
            testEnum = null;
        }
    }

}
