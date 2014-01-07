package org.javers.core.json.builder

import org.javers.model.domain.GlobalCdoId
import org.javers.model.mapping.Entity
import static EntityTestBuilder.entity

/**
 * @author bartosz walacik
 */
class GlobalCdoIdTestBuilder {

      static GlobalCdoId globalCdoId(Object cdo){
          if (cdo == null) {
              return null
          }

          Entity entity = entity(cdo.class)

          new GlobalCdoId(entity.getCdoIdOf(cdo), entity)
      }
}
