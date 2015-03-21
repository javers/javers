package org.javers.core.cases;

/**
 * @author bartosz walacik
 */
class TypeMismatchExceptionClasses {
    static class Holder {
        A item;

        public Holder(A item) {
            this.item = item;
        }
    }

    static abstract class A {}

    static class B0 extends A {
        String b0 = "asdf";
    }

    static class B1 extends A {
        String b1 = "qwer";
    }
}
