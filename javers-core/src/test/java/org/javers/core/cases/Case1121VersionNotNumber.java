package org.javers.core.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.jql.QueryBuilder;
import org.junit.jupiter.api.Test;

/**
 * see https://github.com/javers/javers/issues/1121
 *
 * @author narsereg
 */
public class Case1121VersionNotNumber {
    private static class Clazz {
    }

    @Test
    public void shouldNotFailWhenSelectOnlyDeletedVChanges() {
        //given
        Javers javers = JaversBuilder.javers().build();
        javers.commitShallowDelete("me", new Clazz());

        javers.findChanges(
                QueryBuilder.byClass(Clazz.class)
                        .withChildValueObjects()
                        .build()
        );
    }
}
