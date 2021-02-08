package org.javers.core.commit

import spock.lang.Specification

import java.time.ZonedDateTime

class CommitMetadataTest extends Specification {

    def "should init commitDateInstant from commitDate using current Zone if commitDateInstant is null "(){
      given:
      def nowZ = ZonedDateTime.now()
      def nowI = nowZ.toInstant()
      def nowL = nowZ.toLocalDateTime()

      println 'now Instant: '+ nowI
      println 'now LocalDateTime: '+ nowL

      when:
      def c = new CommitMetadata('', [:], nowL, null, new CommitId(1,1))

      then:
      c.commitDate == nowL
      c.commitDateInstant == nowI
    }
}
