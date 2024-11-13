package org.javers.spring.aot;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JaversSpringNativeHintsTest {
    @Test
    void registerNativeHints() {
        assertThat(JaversSpringNativeHints.registerNativeHints(new RuntimeHints())).isEqualTo(true);
    }

}