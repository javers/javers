package org.javers.spring.boot.aot;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

import static org.fest.assertions.api.Assertions.assertThat;

class JaversSqlNativeHintsConfigurationTest {
    @Test
    void registerNativeHints() {
        assertThat(JaversSqlNativeHintsConfiguration.registerNativeHints(new RuntimeHints())).isEqualTo(true);
    }
}