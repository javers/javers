package org.javers.core


import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDateTime

class CommitIdGeneratorTest extends Specification {

    @Unroll
    def "Commit Id Comparator should distinguish between duplicate commit ids with different timestamps in metadata"() {
        given:
        def firstDate = LocalDateTime.now()
        def firstInstant = Instant.now()
        def laterDate = firstDate.plusSeconds(1)
        def laterInstant = firstInstant.plusSeconds(1)
        def earlierDate = firstDate.plusSeconds(-1)
        def earlierInstant = firstInstant.plusSeconds(-1)
        def commitId = new CommitId(2, 0)

        def commitMetadata0 = new CommitMetadata("duro", [:], earlierDate, earlierInstant, commitId)
        def commitMetadata1 = new CommitMetadata("duro", [:], firstDate, firstInstant, new CommitId(1, 0))
        def commitMetadata2 = new CommitMetadata("duro", [:], firstDate, firstInstant, commitId)
        def commitMetadata3 = new CommitMetadata("duro", [:], firstDate, firstInstant, new CommitId(3, 0))
        def commitMetadata4 = new CommitMetadata("duro", [:], laterDate, laterInstant, commitId)

        when: "List containing duplicate commit ids is sorted"
        def metadataList = new ArrayList<CommitMetadata>()
        metadataList.add(commitMetadata4)
        metadataList.add(commitMetadata2)
        metadataList.add(commitMetadata0)
        metadataList.add(commitMetadata3)
        metadataList.add(commitMetadata1)
        metadataList.sort(CommitIdGenerator.SYNCHRONIZED_SEQUENCE.comparator)

        then: "Sort includes timestamp and commit id for ordering"
        metadataList.get(0) == commitMetadata0
        metadataList.get(1) == commitMetadata1
        metadataList.get(2) == commitMetadata2
        metadataList.get(3) == commitMetadata3
        metadataList.get(4) == commitMetadata4

        // Seperately verify timestamps
        metadataList.get(0).commitDateInstant == commitMetadata0.commitDateInstant
        metadataList.get(1).commitDateInstant == commitMetadata1.commitDateInstant
        metadataList.get(4).commitDateInstant == commitMetadata4.commitDateInstant

    }
}