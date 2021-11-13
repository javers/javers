package org.javers.spring.boot.mongo

import com.mongodb.client.FindIterable
import com.mongodb.client.MongoClient
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DummyTransactionalService {

    @Autowired
    MongoClient mongoClient

    @Autowired
    DummyTransactionalRepository dummyTransactionalRepository

    Document createDocument() {
        new Document("_id", UUID.randomUUID().toString())
    }

    def saveAndCatch(Document doc) {
        try {
            dummyTransactionalRepository.saveInTransactionAndFail(doc)
        } catch (Exception e) {
            println("catched "+e)
        }
    }

    boolean documentExists(Document doc) {
        def mongo = mongoClient.getDatabase("test")

        FindIterable<Document> iterable =
        mongo.getCollection(DummyTransactionalService.name).find(doc)

        iterable.first() != null
    }
}
