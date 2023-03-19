package org.javers.core.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TokenType;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.junit.jupiter.api.Test;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * see https://github.com/javers/javers/issues/1252
 *
 * @author Madhav Jha
 */
public class Case1252ObjectToNumber {

    @Test
    public void objectToNumberShouldMatch() {
        //Given
        final Javers javers = JaversBuilder.javers().build();
        UserToken original = new UserToken();
        int userId=1;
        original.setId(userId);
        original.setToken(999999999L);
        javers.commit("author", original);

        //When
        JqlQuery query = QueryBuilder.byInstanceId(userId, UserToken.class).build();
        List<Shadow<UserToken>> shadows = javers.findShadows(query);

        UserToken shadow = shadows.get(0).get();

        //Then

        /* Before fix
            expected:<9[99999999L]> but was:<9[.99999999E8]>
            Expected :999999999L
            Actual   :9.99999999E8
        */
        assertThat(shadow.getToken()).isEqualTo(original.getToken());
    }
}

class UserToken {
    @Id
    private int id;
    private Object token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getToken() {
        return token;
    }

    public void setToken(Object token) {
        this.token = token;
    }
}
