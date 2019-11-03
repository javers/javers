package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.ShallowReference
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.annotation.Value
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.InstanceId
import org.javers.repository.api.JaversRepository
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.JqlQuery
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import javax.persistence.Id
import static java.util.UUID.*

/**
 * https://github.com/javers/javers/issues/897
 */
class CaseEmbeddedIdDeserializeProblem extends Specification {

    protected JaversRepository repository = new InMemoryRepository()
    protected Javers javers

    def setup() {
        javers = buildJaversInstance()
    }

    Javers buildJaversInstance() {
        def javersBuilder = JaversBuilder
                .javers()
                .registerJaversRepository(repository)

        javersBuilder.build()
    }

    @TypeName("Agreement")
    class Agreement {

        @Id
        UUID agreementId

        @ShallowReference
        List<AgreementMember> agreementMembers
    }

    @TypeName("AgreementMember")
    class AgreementMember {

        @Id
        AgreementMemberId agreementMemberId
    }

    @Value
    static class AgreementMemberId implements Serializable {
        UUID agreementId
        UUID memberId
    }

    def "should read shadows for classes with EmbeddedId"() {
        given:
        def agreementId = randomUUID()

        AgreementMemberId agreementMemberId = new AgreementMemberId(
                agreementId:agreementId,
                memberId:randomUUID()
        )

        AgreementMember agreementMember = new AgreementMember(agreementMemberId:agreementMemberId)
        Agreement agreement = new Agreement(
                agreementId: agreementId,
                agreementMembers: [agreementMember]
        )

        javers.commit("Agreement", agreement)

        //query by typeName:
        JqlQuery query = QueryBuilder.byInstanceId(agreement.agreementId, "Agreement").build()

        //read immediately:
        when:
        List<CdoSnapshot> snapshots1 = javers.findSnapshots(query)

        then:
        snapshots1.size() > 0
        snapshots1[0].state.getPropertyValue("agreementMembers")[0] instanceof InstanceId
        snapshots1[0].getManagedType().baseJavaClass.getName().equals(this.class.name + "\$Agreement")

        //read same data after restart:
        when:
        def javers2 = buildJaversInstance()

        //This is critical, because fresh Javers instance doesn't know the Agreement class.
        //On production, JaversBuilder.withPackagesToScan() should be used.
        javers2.getTypeMapping(Agreement)

        List<CdoSnapshot> snapshots2 = javers2.findSnapshots(query)

        //expecting the same result but fail:
        then:
        snapshots2.size() > 0
        snapshots2[0].state.getPropertyValue("agreementMembers")[0] instanceof InstanceId
        snapshots2[0].getManagedType().baseJavaClass.getName().equals(this.class.name + "\$Agreement")//java.lang.Object
    }
}