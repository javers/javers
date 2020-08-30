package org.javers.core.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.junit.Test;

import javax.persistence.Id;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class CaseShadowWithInitializedPrimitiveBooleanField {

    @Test
    public void shouldFindShadowWithSameBooleanValue() {
        Javers javers = JaversBuilder.javers().build();

        Long personId = 1L;
        Person original = new Person();
        original.setPersonId( personId );
        original.setActive( false );

        javers.commit( "author", original );

        JqlQuery query = QueryBuilder.byInstanceId( personId, Person.class ).build();
        List<Shadow<Person>> shadows = javers.findShadows( query );
        List<CdoSnapshot> snapshots = javers.findSnapshots( query);

        Person shadow = shadows.get( 0 ).get();

        System.out.println("loaded snapshot " + snapshots.get(0));
        System.out.println("original " + original.isActive());
        System.out.println("shadow " + shadow.isActive());

        assertThat( shadow.isActive() ).isEqualTo( original.isActive() );
    }

    @TypeName( "Person" )
    static class Person {
        @Id
        private Long personId;

        private boolean isActive = true;

        public Long getPersonId() {
            return personId;
        }

        public void setPersonId( Long personId ) {
            this.personId = personId;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive( boolean active ) {
            isActive = active;
        }
    }
}
