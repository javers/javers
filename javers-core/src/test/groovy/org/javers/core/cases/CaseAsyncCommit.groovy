package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class CaseAsyncCommit extends Specification {
    private static final Logger logger = LoggerFactory.getLogger(CaseAsyncCommit)

    def "should commit asynchronously "(){
      given:
      def p = new SnapshotEntity(id:1, intProperty:2)
      def javers = JaversBuilder.javers().build()

      def goFlag = new AtomicBoolean(false)
      def executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100))

      executor.submit({
          while(goFlag.get()) {}
      })

      when:
      def cf = javers.commitAsync("author", p, [:], executor)

      then:
      !javers.findSnapshots(QueryBuilder.byInstanceId(1,SnapshotEntity).build())

      when:
      goFlag.set(true)
      logger.info "waiting for future competition ...."
      while(executor.completedTaskCount < 3) {}

      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1,SnapshotEntity).build()).get(0)
      def commit = cf.get()

      then:
      commit.author == "author"
      commit.snapshots[0].getPropertyValue("intProperty") == 2
      snapshot.getPropertyValue("intProperty") == 2
    }
}
