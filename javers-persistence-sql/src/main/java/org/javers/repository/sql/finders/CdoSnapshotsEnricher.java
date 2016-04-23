package org.javers.repository.sql.finders;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.lang.reflect.Field;
import java.util.*;

public class CdoSnapshotsEnricher {

    private CommitPropertyFinder commitPropertyFinder;

    public CdoSnapshotsEnricher(CommitPropertyFinder commitPropertyFinder) {
        this.commitPropertyFinder = commitPropertyFinder;
    }

    List<CdoSnapshot> enrichSnapshots(List<CdoSnapshotDTO> snapshotDTOs) {
        if (snapshotDTOs.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Map<String, String>> commitsProperties = prepareCommitsProperties(snapshotDTOs);
        return enrichSnapshotsWithCommitProperties(snapshotDTOs, commitsProperties);
    }

    private Map<Long, Map<String, String>> prepareCommitsProperties(List<CdoSnapshotDTO> snapshotDTOs) {
        List<Long> commitPKs = getCommitPKs(snapshotDTOs);
        List<CommitPropertyDTO> commitPropertyDTOs = commitPropertyFinder.findPropertiesOfCommits(commitPKs);
        return convertToMap(commitPropertyDTOs);

    }

    private List<Long> getCommitPKs(List<CdoSnapshotDTO> snapshotDTOs) {
        List<Long> commitPKs = new ArrayList<>();
        for (CdoSnapshotDTO snapshotDTO : snapshotDTOs) {
            if (!commitPKs.contains(snapshotDTO.getCommitPK())) {
                commitPKs.add(snapshotDTO.getCommitPK());
            }
        }
        return  commitPKs;
    }

    private Map<Long, Map<String, String>> convertToMap(List<CommitPropertyDTO> commitPropertyDTOs) {
        Map<Long, Map<String, String>> commitsProperties = new HashMap<>();
        for (CommitPropertyDTO commitPropertyDTO : commitPropertyDTOs) {
            if (!commitsProperties.containsKey(commitPropertyDTO.getCommitPK())) {
                commitsProperties.put(commitPropertyDTO.getCommitPK(), new HashMap<String, String>());
            }
            commitsProperties.get(commitPropertyDTO.getCommitPK()).put(commitPropertyDTO.getName(), commitPropertyDTO.getValue());
        }
        return commitsProperties;
    }

    private List<CdoSnapshot> enrichSnapshotsWithCommitProperties(List<CdoSnapshotDTO> snapshotDTOs, final Map<Long, Map<String, String>> commitsProperties) {
        return Lists.transform(snapshotDTOs, new Function<CdoSnapshotDTO, CdoSnapshot>() {
            @Override
            public CdoSnapshot apply(CdoSnapshotDTO cdoSnapshotDTO) {
                CdoSnapshot snapshot = cdoSnapshotDTO.getSnapshot();
                long commitPK = cdoSnapshotDTO.getCommitPK();
                if (commitsProperties.containsKey(commitPK)) {
                    CommitMetadata commitMetadata = snapshot.getCommitMetadata();
                    Map<String, String> commitProperties = commitsProperties.get(commitPK);
                    setPropertiesOnCommitMetadata(commitMetadata, commitProperties);
                }
                return snapshot;
            }
            private void setPropertiesOnCommitMetadata(CommitMetadata commitMetadata, Map<String, String> commitProperties) {
                try {
                    Field field = commitMetadata.getClass().getDeclaredField("properties");
                    field.setAccessible(true);
                    field.set(commitMetadata, commitProperties);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("No field properties on CommitMetadata", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access properties on CommitMetadata", e);
                }
            }
        });
    }

}
