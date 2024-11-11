package org.javers.spring.boot.aot;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

import static org.fest.assertions.api.Assertions.assertThat;


class JaversMongoNativeHintsConfigurationTest {
    @Test
    void registerNativeHints() {
        assertThat(JaversMongoNativeHintsConfiguration.registerNativeHints(new RuntimeHints())).isEqualTo(true);
    }
}