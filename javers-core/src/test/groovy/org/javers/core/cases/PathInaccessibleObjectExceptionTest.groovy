import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

import java.nio.file.Path

/**
 * case https://github.com/javers/javers/issues/1350
 */
class PathInaccessibleObjectExceptionTest extends Specification {

    class Entity {
        @Id int id
        Path path
    }

    def "should support java.nio.file.Path" () {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def diff = javers.compare(
                new Entity(id:1, path: Path.of("foo")),
                new Entity(id:1, path: Path.of("bar")))

        then:
        println(diff)
        diff.changes.size() == 1
    }
}
