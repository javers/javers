package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.core.GraphFactory;
import org.javers.core.Javers;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.commit.CommitFactory;
import org.javers.core.commit.CommitSeqGenerator;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.appenders.*;
import org.javers.core.graph.LiveCdoFactory;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.json.typeadapter.CdoSnapshotTypeAdapter;
import org.javers.core.json.typeadapter.GlobalCdoIdTypeAdapter;
import org.javers.core.json.typeadapter.MapChangeTypeAdapter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.snapshot.GraphShadowFactory;
import org.javers.core.snapshot.GraphSnapshotFactory;
import org.javers.core.snapshot.SnapshotFactory;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Piotr Betkier
 */
public class CoreJaversModule implements JaversModule {

    private static final Class[] moduleComponents = new Class[]{
            Javers.class,
            DiffFactory.class,
            ObjectGraphBuilder.class,
            NewObjectAppender.class,
            MapChangeAppender.class,
            ListChangeAppender.class,
            SetChangeAppender.class,
            ArrayChangeAppender.class,
            ObjectRemovedAppender.class,
            ReferenceChangeAppender.class,
            JsonConverterBuilder.class,
            ValueChangeAppender.class,
            TypeMapper.class,
            TypeFactory.class,
            JaversCoreConfiguration.class,
            CommitFactory.class,
            SnapshotFactory.class,
            GraphSnapshotFactory.class,
            GraphShadowFactory.class,
            JaversExtendedRepository.class,
            LiveCdoFactory.class,
            LiveGraphFactory.class,
            GlobalIdFactory.class,
            GraphFactory.class,
            CommitSeqGenerator.class,
            GlobalCdoIdTypeAdapter.class,
            MapChangeTypeAdapter.class,
            CdoSnapshotTypeAdapter.class
    };

    @Override
    public Collection<Class> getModuleComponents() {
        return Arrays.asList(moduleComponents);
    }

}
