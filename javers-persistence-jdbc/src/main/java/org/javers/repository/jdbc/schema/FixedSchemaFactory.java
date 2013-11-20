package org.javers.repository.jdbc.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.schema.model.Schema;

/**
 *
 * @author bartosz walacik
 */
public class FixedSchemaFactory {

    public Schema getSchema() {
        Schema schema = new Schema(DialectRegistry.dialect("H2"));

        schema.addRelation("javers_diff")
                .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                .withAttribute().string("user_id").withMaxLength(200).notNull().and()
                .primaryKey("javers_diff_pk").using("id").and()
                .build();
        schema.addSequence("seq_javers_diff").build();

        return schema;
    }
}
