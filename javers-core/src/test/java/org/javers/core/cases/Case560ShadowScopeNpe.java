package org.javers.core.cases;


import org.bson.types.ObjectId;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.junit.BeforeClass;
import org.junit.Test;
import org.spockframework.util.Assert;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * see https://github.com/javers/javers/issues/560
 * @author Fatih Soylemez on 03/07/17.
 */
public class Case560ShadowScopeNpe {

    private static Javers javers;

    private static ObjectId id;


    @BeforeClass
    public  static void initClass(){
        javers = JaversBuilder.javers().build();
        id = ObjectId.get();
    }

    @Test
    public void commitEntity(){
        MongoStoredEntity entity1 = new MongoStoredEntity(id, "alg1", "1.0", "name");

        javers.commit(id.toString(),entity1);
    }

    @Test
    public void getShadow(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        QueryBuilder queryBuilder = QueryBuilder.byInstanceId(id.toString(), MongoStoredEntity.class).withShadowScopeDeep();

      //  queryBuilder.to(LocalDateTime.parse("2017-07-25 13:30",formatter));

        List<CdoSnapshot> snapshots = javers.findSnapshots(queryBuilder.build());
        List<Shadow<MongoStoredEntity>> shadows = javers.findShadows(queryBuilder.build());
        Assert.notNull(shadows);
    }

}
