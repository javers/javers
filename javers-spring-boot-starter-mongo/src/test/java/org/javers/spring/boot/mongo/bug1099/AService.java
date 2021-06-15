package org.javers.spring.boot.mongo.bug1099;

import java.util.List;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.boot.mongo.bug1099.model.A;
import org.springframework.stereotype.Service;

@Service
public class AService {

	private Javers javers;
	
	public AService(Javers javers) {
		this.javers = javers;
	}

	public String getInitSnapshot(String id) {
		QueryBuilder query = QueryBuilder.byClass(A.class);
		List<CdoSnapshot> snapshots = javers.findSnapshots(query.build());

		return javers.getJsonConverter().toJson(snapshots);
	}
}
