package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.ShallowReference
import org.javers.core.metamodel.annotation.TypeName
import org.javers.repository.jql.JqlQuery
import org.javers.repository.jql.QueryBuilder
import org.javers.shadow.Shadow
import spock.lang.Specification

import javax.persistence.CascadeType
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Id
import javax.persistence.OneToMany

class Case886ProblemReadingShadowsWithEmbeddedId extends Specification {

    @TypeName("Agreement")
    class Agreement {

        @Id
        private UUID agreementId

        private UUID locationId

        @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
        @ShallowReference
        private List<AgreementMember> agreementMembers
    }

    @Embeddable
    @TypeName("AgreementMemberId")
    class AgreementMemberId implements Serializable {
        private UUID agreementId
        private UUID memberId
    }

    @TypeName("AgreementMember")
    class AgreementMember {
        @EmbeddedId
        private AgreementMemberId agreementMemberId

        public AgreementMemberId getId() {
            return agreementMemberId
        }
    }

    def "should read shadows for classes with EmbeddedId"() {
        given:
        def javers = JaversBuilder.javers().build()

        println javers.getTypeMapping(Agreement).prettyPrint()
        println javers.getTypeMapping(UUID).prettyPrint()

        when:
        UUID agreementId = UUID.randomUUID()

        AgreementMemberId agreementMemberId = new AgreementMemberId(
                agreementId: agreementId,
                memberId: UUID.randomUUID() )

        AgreementMember agreementMember = new AgreementMember(agreementMemberId:agreementMemberId)

        Agreement agreement = new Agreement(
                agreementId: agreementId,
                locationId: UUID.randomUUID(),
                agreementMembers: [agreementMember])

        def c = javers.commit("Agreement", agreement)

        JqlQuery query = QueryBuilder.byInstanceId(agreementId, Agreement.class).build()
        List<Shadow<Agreement>> shadows = javers.findShadows(query)

        then:
        shadows.size() > 0
    }
}
