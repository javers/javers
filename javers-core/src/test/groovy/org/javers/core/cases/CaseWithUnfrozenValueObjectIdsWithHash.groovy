package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import jakarta.persistence.Id

class LimitEntity {
    @Id
    public UUID id
    public String caption
    public Set<LimitRangeEntity> limitRanges

    LimitEntity(UUID id, String caption) {
        this.id = id
        this.caption = caption
    }
}

class LimitRangeEntity {
    UUID id
    String caption
    LimitEntity limit
    Set<LimitSignatureEntity> limitSignatures
}

class LimitSignatureEntity {
    UUID id
    String caption
    LimitRangeEntity limitRange
}

class CaseWithUnfrozenValueObjectIdsWithHash extends Specification {

    LimitEntity getLimits() {
        LimitEntity limitEntity = new LimitEntity(UUID.randomUUID(), "LimitEntity")
        limitEntity.limitRanges = genLimitRanges(limitEntity)
        limitEntity
    }

    Set<LimitRangeEntity> genLimitRanges(LimitEntity limitEntity) {
        def limitRangeEntity = new LimitRangeEntity()
        limitRangeEntity.limit = limitEntity
        limitRangeEntity.caption = "LimitRangeEntity"
        limitRangeEntity.limitSignatures = genLimitSignatures(limitRangeEntity)
        [limitRangeEntity] as Set
    }

    Set<LimitSignatureEntity> genLimitSignatures(LimitRangeEntity limitRangeEntity) {
        def limitSignatureEntity = new LimitSignatureEntity()
        limitSignatureEntity.limitRange = limitRangeEntity
        limitSignatureEntity.caption = "LimitSignatureEntity"
        [limitSignatureEntity] as Set
    }

    def "should freeze ValueObjectIdWithHash when an ObjectGraph is built"() {
        given:
        def javers = JaversBuilder.javers().build()
        def limitEntity = getLimits()
        javers.commit("a", limitEntity)
        def limitRangeEntity = limitEntity.limitRanges.iterator().next()
        def limitSignatureEntity = limitRangeEntity.getLimitSignatures().iterator().next()

        when:
        limitSignatureEntity.caption = "1 Change"
        javers.commit("a", limitEntity)

        then:
        def shadowList = javers.findShadows(QueryBuilder.byInstanceId(limitEntity.id, LimitEntity).build())
        shadowList.size() == 2
    }
}
