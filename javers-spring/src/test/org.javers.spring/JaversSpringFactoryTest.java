import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyNetworkAddress;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.spring.JaversSpringFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.javers.test.assertion.Assertions.assertThat;


/**
 * @author Pawel Cierpiatka
 */
public class JaversSpringFactoryTest {

    @Test
    public void shouldRegisteredEntity() throws Exception {
        //given
        JaversSpringFactory javersSpringFactory = new JaversSpringFactory();

        //when
        javersSpringFactory.setEntityClasses(Arrays.<Class>asList(DummyUser.class, DummyUserDetails.class));
        javersSpringFactory.setValueObject(Arrays.<Class>asList(DummyAddress.class, DummyNetworkAddress.class));

        //then
        assertThat(javersSpringFactory.getObject().isManaged(DummyUser.class));
    }

    /*
    Uncomment after custom id will be merged
    @Test
    public void shouldRegisteredDescribedClass() throws Exception {
        //given
        JaversSpringFactory javersSpringFactory = new JaversSpringFactory();
        Map<Class,String> describedEntityClasses = new HashMap<>();
        describedEntityClasses.put(DummyUser.class, "age");

        //when
        javersSpringFactory.setDescribedEntityClasses(describedEntityClasses);
        javersSpringFactory.setValueObject(Arrays.<Class>asList(DummyAddress.class, DummyNetworkAddress.class));

        //then
        assertThat(javersSpringFactory.getObject().isManaged(DummyUser.class));
    }*/

}
