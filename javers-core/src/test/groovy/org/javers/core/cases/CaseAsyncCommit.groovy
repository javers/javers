package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class CaseAsyncCommit extends Specification {

    def "should commit asynchronously "(){
      given:
      def p = new SnapshotEntity(id:1, intProperty:2)
      def javers = JaversBuilder.javers().build()

      def queue = new ArrayBlockingQueue<>(100)
      def executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue)

      when:
      def cf = javers.commitAsync("author", p, [:], executor)

      then:
      !javers.findSnapshots(QueryBuilder.byInstanceId(1,SnapshotEntity).build())

      when:
      while(executor.completedTaskCount < 2) {
          println "executor.completedTaskCount: " + executor.completedTaskCount
          println "queue.size:                  " + executor.completedTaskCount
      }

      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1,SnapshotEntity).build()).get(0)
      def commit = cf.get()

      then:
      commit.author == "author"
      commit.snapshots[0].getPropertyValue("intProperty") == 2
      snapshot.getPropertyValue("intProperty") == 2
    }
}
