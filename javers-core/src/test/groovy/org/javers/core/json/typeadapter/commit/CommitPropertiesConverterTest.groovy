package org.javers.core.json.typeadapter.commit

import com.google.gson.JsonElement
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.StreamSupport

class CommitPropertiesConverterTest extends Specification {
    def 'should filter out keys with null values'() {
        given:
        final properties = ['name': 'Skif', 'breed': 'scottish straight', 'age': null]

        when:
        final array = CommitPropertiesConverter.toJson(properties) as Iterable<JsonElement>

        then:
        final keys = StreamSupport.stream(array.spliterator(), false)
                .map { element -> element['key'] }
                .map { primitive -> primitive.getAsString() }
                .collect(Collectors.toList())

        assert 'name' in keys
    }
}
