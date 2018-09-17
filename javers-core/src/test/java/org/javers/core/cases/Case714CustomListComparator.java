package org.javers.core.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.commit.Commit;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.metamodel.annotation.Id;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * see https://github.com/javers/javers/issues/714
 *
 * @author Tomasz Kucharzyk
 */
public class Case714CustomListComparator {


    private static class Case714TestObject {
        @Id
        private Long id;
        private String name;
        private List<Long> numbers;

        public Case714TestObject(Long id, String name, List<Long> numbers) {
            this.id = id;
            this.name = name;
            this.numbers = numbers;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<Long> getNumbers() {
            return numbers;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNumbers(List<Long> numbers) {
            this.numbers = numbers;
        }
    }


    @Test
    public void shouldAllowCustomComparatorForList() {
        //given
        Javers javers = JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .registerCustomComparator((left, right, affectedId, property) -> null, List.class)
                .build();

        Case714TestObject testObject = new Case714TestObject(42L, "name", new ArrayList<>());

        //when
        javers.commit("author", testObject);

        testObject.getNumbers().add(42L);

        Commit commit = javers.commit("author", testObject);

        //then
        assertThat(commit.getChanges()).hasSize(0);
    }
}
