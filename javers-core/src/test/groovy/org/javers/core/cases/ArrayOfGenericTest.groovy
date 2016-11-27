package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.model.ArrayOfGeneric;
import spock.lang.Specification;

/**
 * Test how arrays of generics are handled
 */
public class ArrayOfGenericTest  extends Specification {
    def "should ... "(){
        when:
        ArrayOfGeneric<String> o = new ArrayOfGeneric({ -> ""})
        Javers javers = JaversBuilder.javers().build()
        Diff diff = javers.compare(o, o)
        println(diff)

        then:
        diff.changes.size() == 0
    }
}
