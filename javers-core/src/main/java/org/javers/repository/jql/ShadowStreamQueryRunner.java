package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.shadow.Shadow;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.*;

class ShadowStreamQueryRunner {
    private final ShadowQueryRunner shadowQueryRunner;

    ShadowStreamQueryRunner(ShadowQueryRunner shadowQueryRunner) {
        this.shadowQueryRunner = shadowQueryRunner;
    }

    Stream<Shadow> queryForShadowsStream(JqlQuery query) {

        if (query.getQueryParams().skip() > 0) {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "skip can't be set on a JqlStreamQuery. Use Stream.skip() on a resulting Stream.");
        }

        List<Shadow> poc = shadowQueryRunner.queryForShadows(query);

        int characteristics = IMMUTABLE | ORDERED;
        Spliterator<Shadow> spliterator = Spliterators.spliteratorUnknownSize(poc.iterator(), characteristics);

        Stream<Shadow> stream = StreamSupport.stream(spliterator, false);

        return stream;
    }
}
