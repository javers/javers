package org.javers.repository.jql;

import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;

class QueryCompiler {
    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;

    QueryCompiler(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
    }

    void compile(JqlQuery query) {
        query.compile(globalIdFactory, typeMapper);

        if(query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            globalIdFactory.touchValueObjectFromPath(filter.getOwnerEntity(), filter.getPath());
        }
    }
}
