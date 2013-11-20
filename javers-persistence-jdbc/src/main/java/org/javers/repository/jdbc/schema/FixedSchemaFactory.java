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
              .withAttribute().string("user_id").withMaxLength(255).notNull().and()
               // .with(new DateAttribute(dialect, "diff_date"))
              .primaryKey("javers_diff_pk").using("id").and()
              .build();
        schema.addSequence("seq_javers_diff").build();

        schema.addRelation(CHANGE_TABLE_NAME)
              .withAttribute().longAttr("fk_diff").notNull().and()
              .withAttribute().integer("change_no").notNull().and()
              .foreignKey("javers_change_fk_diff").on("fk_diff").references(DIFF_TABLE_NAME,"id").and()
              .primaryKey("javers_change_pk").using("fk_diff","change_no").and()
              .build();

        return schema;
    }

    public String getDiffTableName() {
        return DIFF_TABLE_NAME;
    }
}
