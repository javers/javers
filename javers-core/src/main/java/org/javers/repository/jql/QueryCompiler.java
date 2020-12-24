package org.javers.repository.jql;

import org.javers.core.CoreConfiguration;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;

class QueryCompiler {
    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;
    private final CoreConfiguration javersCoreConfiguration;

    public QueryCompiler(GlobalIdFactory globalIdFactory, TypeMapper typeMapper, CoreConfiguration javersCoreConfiguration) {
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
        this.javersCoreConfiguration = javersCoreConfiguration;
    }

    void compile(JqlQuery query) {
        query.compile(globalIdFactory, typeMapper, javersCoreConfiguration.getCommitIdGenerator());
    }
}
