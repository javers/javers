package org.javers.spring.boot.aot;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

import static org.fest.assertions.api.Assertions.assertThat;

class JaversSqlNativeHintsTest {
    @Test
    void registerNativeHints() {
        assertThat(JaversSqlNativeHints.registerNativeHints(new RuntimeHints())).isEqualTo(true);
    }
}