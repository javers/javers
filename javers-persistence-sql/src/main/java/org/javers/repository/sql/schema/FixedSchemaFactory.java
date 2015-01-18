package org.javers.repository.sql.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.model.RelationBuilder;
import org.polyjdbc.core.schema.model.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * non-configurable schema factory, gives you schema with default table names
 *
 * @author bartosz walacik
 */
public class FixedSchemaFactory {

    public static final String SNAPSHOT_TABLE_NAME = "jv_snapshot";
    public static final String COMMIT_TABLE_NAME = "jv_commit";
    public static final String GLOBAL_ID_TABLE_NAME = "jv_global_id";

    private Schema commitTableSchema(Dialect dialect, String tableName) {
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(tableName,"commit_pk",schema,relationBuilder);
        relationBuilder.withAttribute().string("author").withMaxLength(100).and()
                .withAttribute().timestamp("commit_date").notNull().and()
                .withAttribute().longAttr("major_id") .notNull().and()
                .withAttribute().integer("minor_id") .notNull().and()
                .build();
        return schema;
    }

    private Schema globalIdTableSchema(Dialect dialect, String tableName){
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(tableName,"global_id_pk",schema,relationBuilder);
        relationBuilder.withAttribute().string("dtype").withMaxLength(20).notNull().and()
                .withAttribute().text("managed_class").notNull().and()
                .withAttribute().text("cdo_id").notNull().and()
                .withAttribute().text("fragment").notNull().and()
                .withAttribute().integer("minor_id").notNull().and()
                .withAttribute().longAttr("owner_fk").notNull().and()
                .foreignKey(tableName+"_owner_fk").on("owner_fk").references(GLOBAL_ID_TABLE_NAME,"global_id_pk").and()
                .build();
        schema.addIndex(tableName+"_owner_fk_idx").indexing("owner_fk").on(tableName).build();
        return schema;
    }

    private void primaryKey(String tableName, String pkColName, Schema schema, RelationBuilder relationBuilder) {
        relationBuilder.withAttribute().longAttr(pkColName).withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                .primaryKey("jv_"+pkColName).using(pkColName).and();
        schema.addSequence("jv_"+pkColName+"_seq").build();
        schema.addIndex(tableName+"_pk_idx").indexing(pkColName).on(tableName).build();
    }

    private Schema snapshotTableSchema(Dialect dialect, String tableName){
        return null;
    }

    public Map<String, Schema> allTablesSchema(Dialect dialect) {
        Map<String, Schema> schema = new HashMap<>();
        schema.put(COMMIT_TABLE_NAME, commitTableSchema(dialect, COMMIT_TABLE_NAME));
        //schema.put(SNAPSHOT_TABLE_NAME, snapshotTableSchema(dialect, SNAPSHOT_TABLE_NAME));
        schema.put(GLOBAL_ID_TABLE_NAME, globalIdTableSchema(dialect, GLOBAL_ID_TABLE_NAME));
        return schema;
    }
}
