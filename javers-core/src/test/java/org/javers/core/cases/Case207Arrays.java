package org.javers.core.cases;

import org.javers.core.metamodel.annotation.ValueObject;

import javax.persistence.Id;

/**
 * @author bartosz.walacik
 */
public class Case207Arrays {

    public static class Master {
        @Id
        String id;

        Detail[] array;
        int[] iArray;
    }

    @ValueObject
    public static class Detail {
        String data;

        public Detail(String data) {
            this.data = data;
        }
    }
}
