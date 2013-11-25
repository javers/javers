package org.javers.repository.jdbc.schema;

import org.javers.model.domain.Change;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.model.DateAttribute;
import org.polyjdbc.core.schema.model.Schema;

/**
 * non-configurable schema factory, gives you schema with default table names
 *
 * @author bartosz walacik
 */
public class FixedSchemaFactory {

    public static final String DIFF_TABLE_NAME = "javers_diff";
    public static final String CHANGE_TABLE_NAME = "javers_change";

    public Schema getSchema(Dialect dialect) {
        Schema schema = new Schema(dialect);

        schema.addRelation(DIFF_TABLE_NAME)
              .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
              .withAttribute().text("user_id").notNull().and()
              .withAttribute().timestamp("diff_date").notNull().and()
              .primaryKey("javers_diff_pk").using("id").and()
              .build();
        schema.addSequence("seq_javers_diff").build();

        schema.addRelation(CHANGE_TABLE_NAME)
              .withAttribute().longAttr("fk_diff").notNull().and()
              .withAttribute().integer("change_no").notNull().and()
              .foreignKey("javers_change_fk_diff").on("fk_diff").references(DIFF_TABLE_NAME,"id").and()
              .primaryKey("javers_change_pk").using("fk_diff","change_no").and()
              .build();

        schema.addIndex("javers_diff_pk_idx").indexing("id").on(DIFF_TABLE_NAME).build();
        schema.addIndex("javers_change_fk_diff_idx").indexing("fk_diff").on(CHANGE_TABLE_NAME).build();

        return schema;
    }

    public String getDiffTableName() {
        return DIFF_TABLE_NAME;
    }
}
