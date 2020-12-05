package org.javers.repository.jql;

import org.javers.core.JaversCoreConfiguration;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;

class QueryCompiler {
    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;
    private final JaversCoreConfiguration javersCoreConfiguration;

    public QueryCompiler(GlobalIdFactory globalIdFactory, TypeMapper typeMapper, JaversCoreConfiguration javersCoreConfiguration) {
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
        this.javersCoreConfiguration = javersCoreConfiguration;
    }

    void compile(JqlQuery query) {
        query.compile(globalIdFactory, typeMapper, javersCoreConfiguration.getCommitIdGenerator());
    }
}
