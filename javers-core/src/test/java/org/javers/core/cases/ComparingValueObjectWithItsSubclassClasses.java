package org.javers.core.cases;

import org.javers.core.metamodel.annotation.Id;

import java.util.List;

/**
 * @author bartosz.walacik
 */
class ComparingValueObjectWithItsSubclassClasses {
    static class Store {
        @Id
        int id = 1;
        List<Bicycle> bicycles;
    }

    static class Bicycle {
        int speed;
    }

    static class Mountenbike extends Bicycle{
        int seatHeight;
    }
}
