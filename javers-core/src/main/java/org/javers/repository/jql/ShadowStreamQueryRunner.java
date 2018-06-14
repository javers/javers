package org.javers.repository.jql;

import org.javers.shadow.Shadow;

import java.util.stream.Stream;

class ShadowStreamQueryRunner {
    private final ShadowQueryRunner shadowQueryRunner;

    ShadowStreamQueryRunner(ShadowQueryRunner shadowQueryRunner) {
        this.shadowQueryRunner = shadowQueryRunner;
    }

    Stream<Shadow> queryForShadowsStream(JqlStreamQuery query) {

        return null;
    }
}
