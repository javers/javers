package org.javers.core.selftest;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;

/**
 * @author bartosz walacik
 */
public class Application {

    public static void main(String[] args) {
        System.out.println(".. Starting javers-core runtime environment self test ...");


        System.out.println("java.runtime.name:          " + System.getProperty("java.runtime.name"));
        System.out.println("java.vendor:                " + System.getProperty("java.vendor"));
        System.out.println("java.runtime.version:       " + System.getProperty("java.runtime.version"));
        System.out.println("java.version:               " + System.getProperty("java.version"));
        System.out.println("java.home:                  " + System.getProperty("java.home"));
        System.out.println("os.name & ver:              " + System.getProperty("os.name")+" v."+System.getProperty("os.version"));


        System.out.println(".. building JaVers instance ...");
        Javers javers = JaversBuilder.javers().build();

        SampleValueObject left = new SampleValueObject("red");
        SampleValueObject right = new SampleValueObject("green");

        System.out.println(".. calculating diff for two simple ValueObjects...");
        Diff diff = javers.compare(left, right);

        assert diff.getChanges().size() == 1;
        assert diff.getPropertyChanges("color").size() == 1;

        System.out.println(".. self test PASSED ..");
    }

    private static class SampleValueObject {
        private String color;

        private SampleValueObject(String value) {
            this.color = value;
        }
    }
}
