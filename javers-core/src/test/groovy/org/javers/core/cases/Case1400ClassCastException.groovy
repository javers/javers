package org.javers.core.cases


import org.javers.core.JaversBuilder
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * see https://github.com/javers/javers/issues/1400
 *
 * @author bartebor
 */
class Case1400ClassCastException extends Specification {

    static class Container {
        final String id;
        final Base base;

        Container(String id, Base base) {
            this.id = id;
            this.base = base;
        }
    }

    static abstract class Base {

        static class Concrete1 extends Base {
            final Optional<String> string1;

            Concrete1(String string1) {
                this.string1 = Optional.of(string1);
            }
        }

        static class Concrete2 extends Base {
            final Optional<String> string2;

            Concrete2(String string2) {
                this.string2 = Optional.of(string2);
            }
        }
    }

    def "should Not Fail When Comparing Objects With Different Optional fields"() {
        given:
        def javers = JaversBuilder.javers()
            .registerEntity(new EntityDefinition(Container.class, "id"))
            .build()

        def c1 = new Container("id", new Base.Concrete1("string1"));
        def c2 = new Container("id", new Base.Concrete2("string2"));

        when:
        def changes = javers.compare(c1, c2)

        then:
        true
    }
}
