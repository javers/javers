package org.javers.spring.boot.mongo

import com.mongodb.client.MongoClient;
import org.bson.Document
import com.mongodb.client.ClientSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.DefaultTransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

@Repository
public class DummyTransactionalRepository {
    @Autowired
    MongoClient mongoClient

    @Transactional
    def saveInTransactionAndFail(Document doc) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager)
        def mongo = mongoClient.getDatabase("test")
        def col = mongo.getCollection(DummyTransactionalService.name)

        transactionTemplate.execute(new TransactionCallback<Object>() {
             Object doInTransaction(TransactionStatus status) {

                 DefaultTransactionStatus defaultTransactionStatus = (DefaultTransactionStatus)status
                 MongoTransactionManager.MongoTransactionObject trans =
                         (MongoTransactionManager.MongoTransactionObject)defaultTransactionStatus.getTransaction()

                 ClientSession session = trans.getSession()
                 col.insertOne(session, doc)
             }
        })

        throw new RuntimeException("rollback me!")
    }
}
