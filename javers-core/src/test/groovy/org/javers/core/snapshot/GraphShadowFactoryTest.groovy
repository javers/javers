package org.javers.core.snapshot

import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class GraphShadowFactoryTest extends Specification {

    def "should create one node Shadow"() {
        given:
        def javers = javersTestAssembly()
        def cdo = new SnapshotEntity(id: 1, intProperty: 5)
        javers.javers().commit("user",cdo)

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(cdo)

        then:
        shadow.wrappedCdo().class == CdoSnapshot
        shadow.globalCdoId ==  instanceId(1, SnapshotEntity)
        shadow.getPropertyValue("intProperty") == 5
    }

}
