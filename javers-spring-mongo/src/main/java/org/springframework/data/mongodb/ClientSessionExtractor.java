package org.springframework.data.mongodb;

import com.mongodb.client.ClientSession;

import org.springframework.data.mongodb.MongoTransactionManager.MongoTransactionObject;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * this class exists because {@link MongoTransactionObject} is not public
 */
public class ClientSessionExtractor {
    public static ClientSession getFrom(DefaultTransactionStatus transactionStatus) {
        MongoTransactionObject trans = (MongoTransactionObject)transactionStatus.getTransaction();

        return trans.getSession();
    }
}
