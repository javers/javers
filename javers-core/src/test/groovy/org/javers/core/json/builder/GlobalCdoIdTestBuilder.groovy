package org.javers.core.json.builder

import org.javers.model.domain.GlobalCdoId
import org.javers.model.domain.InstanceId
import org.javers.model.domain.ValueObjectId
import org.javers.model.mapping.Entity
import static EntityTestBuilder.entity

/**
 * @author bartosz walacik
 */
class GlobalCdoIdTestBuilder {

      static InstanceId instanceId(Object cdo){
          if (cdo == null) {
              return null
          }

          Entity entity = entity(cdo.class)

          new InstanceId(entity.getCdoIdOf(cdo), entity)
      }

    static ValueObjectId valueObjectId(Object instanceCdo, String fragment){
        if (instanceCdo == null) {
            return null
        }

        Entity entity = entity(instanceCdo.class)

        new ValueObjectId(entity.getCdoIdOf(instanceCdo), entity, fragment)
    }
}
