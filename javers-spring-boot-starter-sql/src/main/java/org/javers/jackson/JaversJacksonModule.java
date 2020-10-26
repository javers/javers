package org.javers.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.io.IOException;
public final class JaversJacksonModule extends Module {
    private final Javers javers;

    public JaversJacksonModule(Javers javers) {
        this.javers = javers;
    }

    @Override
    public void setupModule(final SetupContext context) {
        SimpleModule module = (new SimpleModule())
            .addSerializer(Commit.class, new JsonSerializer<Commit>() {
                @Override
                public void serialize(Commit value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeRawValue(javers.getJsonConverter().toJson(value));
                }
            })
            .addSerializer(Change.class, new JsonSerializer<Change>() {
                @Override
                public void serialize(Change value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeRawValue(javers.getJsonConverter().toJson(value));
                }
            })
            .addSerializer(Diff.class, new JsonSerializer<Diff>() {
                @Override
                public void serialize(Diff value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeRawValue(javers.getJsonConverter().toJson(value));
                }
            })
            .addSerializer(CdoSnapshot.class, new JsonSerializer<CdoSnapshot>() {
                @Override
                public void serialize(CdoSnapshot value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeRawValue(javers.getJsonConverter().toJson(value));
                }
            });

        module.setupModule(context);
    }

    @Override
    public String getModuleName() {
        return JaversJacksonModule.class.getSimpleName();
    }

    @Override
    //TODO: this should be dynamic somehow, can't use VersionUtil.mavenVersionFor because it's deprecated and we're using gradle
    //@see <a href="https://fasterxml.github.io/jackson-core/javadoc/2.6/com/fasterxml/jackson/core/util/VersionUtil.html#mavenVersionFor(java.lang.ClassLoader,%20java.lang.String,%20java.lang.String)">https://fasterxml.github.io/jackson-core/javadoc/2.6/com/fasterxml/jackson/core/util/VersionUtil.html#mavenVersionFor(java.lang.ClassLoader,%20java.lang.String,%20java.lang.String)</a>
    public Version version() {
        return new Version(5, 10, 1, "", "org.javers", "javers-spring-boot-starter-sql");
    }
}
