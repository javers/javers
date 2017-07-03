package org.javers.spring.boot.sql;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author f-aubert
 */
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan("org.javers.spring.boot.sql")
public class Issue558Application {
//    @Bean(name = "javers")
//    @ConditionalOnMissingBean
//    public Javers javers(JaversSqlRepository sqlRepository, PlatformTransactionManager transactionManager) {
//        return TransactionalJaversBuilder.javers()
//                .withTxManager(transactionManager)
//                .registerJaversRepository(sqlRepository)
//                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).withObjectAccessHook(new HibernateUnproxyObjectAccessHook()).build();
//    }

//    @Bean
//    public ObjectAccessHook objectAccessHook() {
//        return new HibernateUnproxyObjectAccessHook();
//    }
}

