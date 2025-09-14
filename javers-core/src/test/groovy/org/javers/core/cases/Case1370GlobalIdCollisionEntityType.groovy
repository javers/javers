package org.javers.core.cases

import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.diff.changetype.NewObject
import org.javers.core.metamodel.clazz.EntityDefinition
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers

class Case1370GlobalIdCollisionEntityType extends Specification {
    class Leg {
        String legId;
    }

    /* GOOD SCENARIO: Works as expected after i change u-xOd -> u-xOda (no more hash collision with tN9oE)
    */

    def "GOOD SCENARIO: Entity Type Collection comparison works if there's NO hash collision"() {
        given:
        def javers = javers().registerEntity(new EntityDefinition(Leg.class, "legId"))
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build()
        def legList1 = new ArrayList();
        def legList2 = new ArrayList();

        def id1 = new Leg();
        id1.legId = "tN9oE"

        def id2 = new Leg();
        id2.legId = "u-xOda"

        legList1.add(id1);

        legList2.add(id1);
        legList2.add(id2);

        when:
        def diff = javers.compareCollections(legList1, legList2, Leg.class);
        println diff.prettyPrint()
        then:
        !diff.getChangesByType(NewObject.class).isEmpty() // we expect u-xOda should show up as NewObject.
    }

    /* BAD SCENARIO:
    org.javers.core.cases.Case1370GlobalIdCollisionEntityType$Leg/u-xOd and
    org.javers.core.cases.Case1370GlobalIdCollisionEntityType$Leg/tN9oE
    Both resolve to same string Hashcode.
     */

    def "BAD SCENARIO: Entity Type Collection comparison breaks if there's hash collision"() {
        given:
        def javers = javers().registerEntity(new EntityDefinition(Leg.class, "legId"))
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build()
        def legList1 = new ArrayList();
        def legList2 = new ArrayList();

        def id1 = new Leg();
        id1.legId = "tN9oE"

        def id2 = new Leg();
        id2.legId = "u-xOd"

        legList1.add(id1);

        legList2.add(id1);
        legList2.add(id2);

        when:
        def diff = javers.compareCollections(legList1, legList2, Leg.class);
        println diff.prettyPrint()
        then:
        !diff.getChangesByType(NewObject.class).isEmpty() // we expect u-xOd should show up as NewObject.
    }
}

