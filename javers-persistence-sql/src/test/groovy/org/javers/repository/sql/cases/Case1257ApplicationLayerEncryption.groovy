package org.javers.repository.sql.cases

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.fest.assertions.api.Assertions
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.repository.sql.H2RepositoryBuilder
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.codecs.CdoSnapshotStateCodec
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

class Case1257ApplicationLayerEncryption extends Specification {

    @Entity
    static class EntityForTest {

        @Id
        private long id
        private String value

        EntityForTest(long id, String value) {
            this.id = id
            this.value = value
        }
    }

    def "default no-op codec should store plain text as-is"() {
        given:
        JaversSqlRepository sqlRepository = new H2RepositoryBuilder().build()
        Javers javers = JaversBuilder.javers()
                .registerJaversRepository(sqlRepository)
                .build()

        when:
        "entity is committed"(javers, 1, "qwer")
        "entity is committed"(javers, 2, "asdf")
        "entity is committed"(javers, 3, "yxcv")

        then:
        "latest snapshot has value"(javers, 1, "qwer")
        "latest snapshot has value"(javers, 2, "asdf")
        "latest snapshot has value"(javers, 3, "yxcv")
    }

    def "explicit no-op codec should store plain text as-is"() {
        given:
        JaversSqlRepository sqlRepository = new H2RepositoryBuilder()
                .withCdoSnapshotStateCodec(new CdoSnapshotStateCodec.NoOp())
                .build()
        Javers javers = JaversBuilder.javers()
                .registerJaversRepository(sqlRepository)
                .build()

        when:
        "entity is committed"(javers, 4, "qwer")
        "entity is committed"(javers, 5, "asdf")
        "entity is committed"(javers, 6, "yxcv")

        then:
        "latest snapshot has value"(javers, 4, "qwer")
        "latest snapshot has value"(javers, 5, "asdf")
        "latest snapshot has value"(javers, 6, "yxcv")
    }


    def "obfuscator should store anything else than plain text"() {
        given:
        CdoSnapshotStateCodec valueObfuscation = new CdoSnapshotStateCodec() {

            private Pattern valuePattern = Pattern.compile("(\"value\": \")(.*)(\")")

            @Override
            String encode(String plain) {
                Matcher matcher = valuePattern.matcher(plain)
                matcher.find()
                return matcher.replaceFirst("\$1\\?\\?\\?\\?\$3")
            }

            @Override
            String decode(String obfuscated) {
                return obfuscated // pass-through intended
            }
        }
        JaversSqlRepository sqlRepository = new H2RepositoryBuilder()
                .withCdoSnapshotStateCodec(valueObfuscation)
                .build()
        Javers javers = JaversBuilder.javers()
                .registerJaversRepository(sqlRepository)
                .build()

        when:
        "entity is committed"(javers, 7, "qwer")
        "entity is committed"(javers, 8, "asdf")
        "entity is committed"(javers, 9, "yxcv")

        then:
        "latest snapshot has value"(javers, 7, "????")
        "latest snapshot has value"(javers, 8, "????")
        "latest snapshot has value"(javers, 9, "????")
    }

    def "symmetrical codec should recover plain text as it was"() {
        given:
        JaversSqlRepository sqlRepository = new H2RepositoryBuilder()
                .withCdoSnapshotStateCodec(new CdoSnapshotStateCodec() {

                    @Override
                    String encode(String plain) {
                        def utf8chars = StandardCharsets.UTF_8.encode(plain)

                        def compressed = new ByteArrayOutputStream()
                        def gzip = new GzipCompressorOutputStream(compressed)
                        gzip.write(utf8chars.array(), 0, utf8chars.limit())
                        gzip.close()

                        return Base64.encoder.encodeToString(compressed.toByteArray())
                    }

                    @Override
                    String decode(String base64) {
                        def compressed = Base64.decoder.decode(base64)

                        def gunzip = new GzipCompressorInputStream(new ByteArrayInputStream(compressed))
                        def decompressed = gunzip.readAllBytes()

                        return new String(decompressed, StandardCharsets.UTF_8)
                    }
                })
                .build()
        Javers javers = JaversBuilder.javers()
                .registerJaversRepository(sqlRepository)
                .build()

        when:
        "entity is committed"(javers, 10, "qwer")
        "entity is committed"(javers, 11, "asdf")
        "entity is committed"(javers, 12, "yxcv")

        then:
        "latest snapshot has value"(javers, 10, "qwer")
        "latest snapshot has value"(javers, 11, "asdf")
        "latest snapshot has value"(javers, 12, "yxcv")
    }

    def "entity is committed"(Javers javers, long id, String value) {
        EntityForTest e = new EntityForTest(id, value)
        javers.commit("author", e)
    }

    def "latest snapshot has value"(Javers javers, long id, String value) {
        CdoSnapshot snapshot = javers.getLatestSnapshot(id, EntityForTest.class).get()
        Assertions.assertThat(snapshot.getPropertyValue("value")).isEqualTo(value)
    }

}
