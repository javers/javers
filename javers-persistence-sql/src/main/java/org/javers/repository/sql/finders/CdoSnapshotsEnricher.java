package org.javers.repository.sql.finders;

import org.javers.core.json.CdoSnapshotSerialized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CdoSnapshotsEnricher {
    void enrichWithCommitProperties(List<CdoSnapshotSerialized> serializedSnapshots,
                                    List<CommitPropertyDTO> commitPropertyDTOs) {

        final Map<Long, Map<String, String>> commitsProperties = convertCommitPropertiesToMap(commitPropertyDTOs);

        for (CdoSnapshotSerialized serializedSnapshot : serializedSnapshots) {
            serializedSnapshot.withCommitProperties(commitsProperties.get(serializedSnapshot.getCommitPk()));
        }
    }

    private Map<Long, Map<String, String>> convertCommitPropertiesToMap(List<CommitPropertyDTO> commitPropertyDTOs) {
        Map<Long, Map<String, String>> commitsProperties = new HashMap<>();
        for (CommitPropertyDTO commitPropertyDTO : commitPropertyDTOs) {
            if (!commitsProperties.containsKey(commitPropertyDTO.getCommitPK())) {
                commitsProperties.put(commitPropertyDTO.getCommitPK(), new HashMap<String, String>());
            }
            commitsProperties.get(commitPropertyDTO.getCommitPK()).put(commitPropertyDTO.getName(), commitPropertyDTO.getValue());
        }
        return commitsProperties;
    }
}
