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

    def "should read shadows for classes with EmbeddedId"() {

        given:
        def javers = JaversBuilder.javers().build()

        when:
        Agreement agreement = new Agreement()
        agreement.agreementId = UUID.randomUUID()
        agreement.locationId = UUID.randomUUID()

        AgreementMember.AgreementMemberId agreementMemberId = new AgreementMember.AgreementMemberId()
        agreementMemberId.agreementId = agreement.agreementId
        agreementMemberId.memberId = UUID.randomUUID()

        AgreementMember agreementMember = new AgreementMember()
        agreementMember.agreementMemberId = agreementMemberId

        List<AgreementMember> agreementMemberList = new ArrayList<>()
        agreementMemberList.add(agreementMember)
        agreement.agreementMembers = agreementMemberList

        javers.commit("Agreement", agreement)

        JqlQuery query = QueryBuilder.byInstanceId(agreement.agreementId, Agreement.class).build()
        List<Shadow<Agreement>> shadows = javers.findShadows(query)

        then:
        shadows.size() > 0
    }

    @TypeName("Agreement")
    class Agreement {

        @Id
        private UUID agreementId;

        private UUID locationId;

        @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
        @ShallowReference
        private List<AgreementMember> agreementMembers;

        //other fields ...
    }

    @TypeName("AgreementMember")
    class AgreementMember {

        public AgreementMemberId getId() {
            return agreementMemberId;
        }

        @Embeddable
        @TypeName("AgreementMemberId")
        public static class AgreementMemberId implements Serializable {
            private UUID agreementId;
            private UUID memberId;
        }

        @EmbeddedId
        private AgreementMemberId agreementMemberId;

        //other fields ...
    }

}
