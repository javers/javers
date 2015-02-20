package org.javers.repository.sql.finders;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotBuilder;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;

import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;

/**
 * Assembles CdoSnapshots from wide SQL ResultSet
 * @author bartosz walacik
 */
class SnapshotAssembler {

    private final JsonConverter jsonConverter;

    public SnapshotAssembler(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    List<CdoSnapshot> assemble(List<SnapshotWideDto> rows, PersistentGlobalId globalId) {
        List<CdoSnapshot>  assembled = new ArrayList<>();
        CdoSnapshotBuilder cdoSnapshotBuilder = null;
        int lastSnapshotPk = -1;

        //tricky assembly loop
        for (SnapshotWideDto row : rows){
            if (row.snapshotPk != lastSnapshotPk) {
                //yield
                if (cdoSnapshotBuilder != null){
                    assembled.add(cdoSnapshotBuilder.build());
                }

                cdoSnapshotBuilder = cdoSnapshot(globalId.instance, row.getCommitMetadata()).withType(row.snapshotType);
                lastSnapshotPk = row.snapshotPk;
            }

            if (row.hasProperty()) { //terminal snapshots come wth properties
                Property jProperty = globalId.getProperty(row.snapshotPropertyName);
                Object propertyValue = jsonConverter.deserializePropertyValue(jProperty, row.snapshotPropertyValue);
                cdoSnapshotBuilder.withPropertyValue(jProperty, propertyValue);
            }
        }
        //yield
        if (cdoSnapshotBuilder != null){
            assembled.add(cdoSnapshotBuilder.build());
        }

        return assembled;
    }

}
