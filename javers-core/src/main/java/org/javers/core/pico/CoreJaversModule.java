package org.javers.core.pico;

import org.javers.common.date.DefaultDateProvider;
import org.javers.common.pico.JaversModule;
import org.javers.core.GraphFactory;
import org.javers.core.Javers;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.commit.CommitFactory;
import org.javers.core.commit.CommitSeqGenerator;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.appenders.ArrayChangeAppender;
import org.javers.core.diff.appenders.ListChangeAppender;
import org.javers.core.diff.appenders.MapChangeAppender;
import org.javers.core.diff.appenders.NewObjectAppender;
import org.javers.core.diff.appenders.ObjectRemovedAppender;
import org.javers.core.diff.appenders.ReferenceChangeAppender;
import org.javers.core.diff.appenders.SetChangeAppender;
import org.javers.core.diff.appenders.ValueChangeAppender;
import org.javers.core.graph.LiveCdoFactory;
import org.javers.core.graph.LiveGraphFactory;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.json.typeadapter.CdoSnapshotTypeAdapter;
import org.javers.core.json.typeadapter.CommitIdTypeAdapter;
import org.javers.core.json.typeadapter.GlobalIdTypeAdapter;
import org.javers.core.json.typeadapter.InstanceIdDTOTypeAdapter;
import org.javers.core.json.typeadapter.change.ArrayChangeTypeAdapter;
import org.javers.core.json.typeadapter.change.ListChangeTypeAdapter;
import org.javers.core.json.typeadapter.change.MapChangeTypeAdapter;
import org.javers.core.json.typeadapter.change.SetChangeTypeAdapter;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.snapshot.GraphShadowFactory;
import org.javers.core.snapshot.GraphSnapshotFactory;
import org.javers.core.snapshot.SnapshotDiffer;
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
            GlobalIdTypeAdapter.class,
            InstanceIdDTOTypeAdapter.class,
            MapChangeTypeAdapter.class,
            ArrayChangeTypeAdapter.class,
            ListChangeTypeAdapter.class,
            SetChangeTypeAdapter.class,
            CdoSnapshotTypeAdapter.class,
            CommitIdTypeAdapter.class,
            DefaultDateProvider.class,
            SnapshotDiffer.class
    };

    @Override
    public Collection<Class> getModuleComponents() {
        return Arrays.asList(moduleComponents);
    }

}
