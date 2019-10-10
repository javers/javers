package org.javers.core.cases


import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.javers.common.date.DateProvider
import org.javers.core.*
import org.javers.core.commit.CommitMetadata
import org.javers.core.metamodel.annotation.ShallowReference
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.repository.api.JaversRepository
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.JqlQuery
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import javax.persistence.*
import java.time.ZonedDateTime


class CaseEmbeddedIdDeserializeProblem extends Specification {

    protected JaversRepository repository
    protected Javers javers
    private DateProvider dateProvider
    private RandomCommitGenerator randomCommitGenerator = null

    def setup() {
        buildJaversInstance()
    }

    void buildJaversInstance() {
        dateProvider = prepareDateProvider()
        repository = prepareJaversRepository()

        def javersBuilder = JaversBuilder
                .javers()
                .withDateTimeProvider(dateProvider)
                .registerJaversRepository(repository)
                .registerValueGsonTypeAdapter(AgreementMember.AgreementMemberId.class, new AgreementMemberIdTypeAdapter())

        if (useRandomCommitIdGenerator()) {
            randomCommitGenerator = new RandomCommitGenerator()
            javersBuilder.withCustomCommitIdGenerator(randomCommitGenerator)
        }

        javers = javersBuilder.build()
    }

    protected int commitSeq(CommitMetadata commit) {
        if (useRandomCommitIdGenerator()) {
            return randomCommitGenerator.getSeq(commit.id)
        }
        commit.id.majorId
    }

    protected DateProvider prepareDateProvider() {
        if (useRandomCommitIdGenerator()) {
            return new TikDateProvider()
        }
        new FakeDateProvider()
    }

    protected setNow(ZonedDateTime dateTime) {
        dateProvider.set(dateTime)
    }

    protected JaversRepository prepareJaversRepository() {
        new InMemoryRepository()
    }

    protected boolean useRandomCommitIdGenerator() {
        false
    }

    def getNewJaversInstance() {
        def javersBuilder = JaversBuilder
                .javers()
                .withDateTimeProvider(dateProvider)
                .registerJaversRepository(repository)
                .registerValueGsonTypeAdapter(AgreementMember.AgreementMemberId.class, new AgreementMemberIdTypeAdapter())

        if (useRandomCommitIdGenerator()) {
            randomCommitGenerator = new RandomCommitGenerator()
            javersBuilder.withCustomCommitIdGenerator(randomCommitGenerator)
        }

        def javers2 = javersBuilder.build()
        return javers2
    }

    def "should read shadows for classes with EmbeddedId"() {

        given:
        //create entity:
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

        //query by typeName:
        JqlQuery query = QueryBuilder.byInstanceId(agreement.agreementId, "Agreement").build()

        //read immediately:
        when:
        List<CdoSnapshot> snapshots1 = javers.findSnapshots(query)

        then:
        snapshots1.size() > 0
        snapshots1.get(0).state.getPropertyValue("agreementMembers").getAt("typeName").get(0) == "AgreementMember"
        snapshots1.get(0).getManagedType().baseJavaClass.getName().equals("org.javers.core.cases.CaseEmbeddedIdDeserializeProblem\$Agreement")

        //read same data after restart:
        when:
        def javers2 = getNewJaversInstance()
        List<CdoSnapshot> snapshots2 = javers2.findSnapshots(query)

        //expecing the same result but fail:
        then:
        snapshots2.size() > 0
        snapshots2.get(0).state.getPropertyValue("agreementMembers").getAt("typeName").get(0) == "AgreementMember"//raw serialized data
        snapshots2.get(0).getManagedType().baseJavaClass.getName().equals("org.javers.core.cases.CaseEmbeddedIdDeserializeProblem\$Agreement")//java.lang.Object
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

            UUID getAgreementId() {
                return agreementId
            }

            void setAgreementId(UUID agreementId) {
                this.agreementId = agreementId
            }

            UUID getMemberId() {
                return memberId
            }

            void setMemberId(UUID memberId) {
                this.memberId = memberId
            }
        }

        @EmbeddedId
        private AgreementMemberId agreementMemberId;

        //other fields ...
    }


    class AgreementMemberIdTypeAdapter extends TypeAdapter<AgreementMember.AgreementMemberId> {

        @Override
        public void write(JsonWriter jsonWriter, AgreementMember.AgreementMemberId agreementMemberId) throws IOException {
            if (agreementMemberId != null) {
                jsonWriter.beginObject();
                if (agreementMemberId.getAgreementId() != null) {
                    jsonWriter.name("agreementId").value(agreementMemberId.getAgreementId().toString());
                }
                if (agreementMemberId.getMemberId() != null) {
                    jsonWriter.name("memberId").value(agreementMemberId.getMemberId().toString());
                }
                jsonWriter.endObject();
            }
        }

        @Override
        public AgreementMember.AgreementMemberId read(JsonReader jsonReader) throws IOException {
            AgreementMember.AgreementMemberId agreementMemberId = new AgreementMember.AgreementMemberId();

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case "agreementId":
                        agreementMemberId.setAgreementId(UUID.fromString(jsonReader.nextString()));
                        break;
                    case "memberId":
                        agreementMemberId.setMemberId(UUID.fromString(jsonReader.nextString()));
                        break;
                    default:
                        break;
                }
            }
            jsonReader.endObject();

            return agreementMemberId;
        }
    }


}
