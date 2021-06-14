package org.javers.spring.boot.mongo.bug1099;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javers.core.Javers;
import org.javers.spring.boot.mongo.TestApplication;
import org.javers.spring.boot.mongo.bug1099.model.A;
import org.javers.spring.boot.mongo.bug1099.model.B;
import org.javers.spring.boot.mongo.bug1099.model.C;
import org.javers.spring.boot.mongo.bug1099.model.D;
import org.javers.spring.boot.mongo.bug1099.model.E;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TestApplication.class})
class AServiceTest {
	@Autowired
	private ARepository repository;
	
    @Autowired
    private Javers javers;

	private AService service = new AService(javers);

	@Test
	void testJavers() {
		repository.save(buildEntity());
		String info = service.getInitSnapshot("id");
		
		assertNotNull(info);
	}

	private A buildEntity() {
		B b = new B("id", "name", 10);
		D d = new D("city", "country");
		C c =  new C("companyName", d);
		
		Map<String, List<E>> map = new HashMap<>();
		map.put("key1", asList(new E("p1", asList("teacher", "director")), new E("p3", asList("manager", "director"))));
		map.put("key2", asList(new E("p2", asList("student", "pupil"))));
		
		return new A(b, c, map);
	}
}
