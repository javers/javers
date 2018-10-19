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

      def waitFlag = new AtomicBoolean(true)
      def executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100))

      executor.submit({
          while(waitFlag.get()) {
          }
          logger.info("start executor")
      })

      when:
      def cf = javers.commitAsync("author", p, [:], executor)
      def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1,SnapshotEntity).build())

      then:
      snapshots.size() == 0

      when:
      waitFlag.set(false)
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
