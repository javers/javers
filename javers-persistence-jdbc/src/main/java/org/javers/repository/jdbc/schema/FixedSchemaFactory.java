package org.javers.repository.jdbc.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.model.Schema;

/**
 * non-configurable schema factory, gives you schema with default table names
 *
 * @author bartosz walacik
 */
public class FixedSchemaFactory {

    public static final String DIFF_TABLE_NAME = "javers_diff";

    public Schema getSchema(Dialect dialect) {
        Schema schema = new Schema(dialect);

        schema.addRelation(DIFF_TABLE_NAME)
                .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                .withAttribute().string("user_id").withMaxLength(200).notNull().and()
                .primaryKey("javers_diff_pk").using("id").and()
                .build();
        schema.addSequence("seq_javers_diff").build();

        return schema;
    }

    public String getDiffTableName() {
        return DIFF_TABLE_NAME;
    }
}
