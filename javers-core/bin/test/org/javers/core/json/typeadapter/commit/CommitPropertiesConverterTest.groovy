package org.javers.core.json.typeadapter.commit

import com.google.gson.JsonElement
import spock.lang.Specification

class CommitPropertiesConverterTest extends Specification {
    def 'should filter out keys with null values'() {
        given:
        final properties = ['name': 'Skif', 'breed': 'scottish straight', 'age': null]

        when:
        final array = CommitPropertiesConverter.toJson(properties) as Iterable<JsonElement>

        then:
        final keys = array.collect { it['key'].getAsString() }

        !keys.contains('age')
    }
}
