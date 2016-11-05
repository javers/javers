package org.javers.core.commit;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.JaversCoreConfiguration;
import org.javers.repository.api.JaversExtendedRepository;

import static org.javers.core.CommitIdGenerator.RANDOM;
import static org.javers.core.CommitIdGenerator.SYNCHRONIZED_SEQUENCE;

class CommitIdFactory {
    private final JaversCoreConfiguration javersCoreConfiguration;
    private final JaversExtendedRepository javersRepository;
    private final CommitSeqGenerator commitSeqGenerator;
    private final DistributedCommitSeqGenerator distributedCommitSeqGenerator;

    CommitIdFactory(JaversCoreConfiguration javersCoreConfiguration, JaversExtendedRepository javersRepository, CommitSeqGenerator commitSeqGenerator, DistributedCommitSeqGenerator distributedCommitSeqGenerator) {
        this.javersCoreConfiguration = javersCoreConfiguration;
        this.javersRepository = javersRepository;
        this.commitSeqGenerator = commitSeqGenerator;
        this.distributedCommitSeqGenerator = distributedCommitSeqGenerator;
    }

    CommitId nextId() {
        if (javersCoreConfiguration.getCommitIdGenerator().equals(SYNCHRONIZED_SEQUENCE)) {
            CommitId head = javersRepository.getHeadId();
            return commitSeqGenerator.nextId(head);
        }

        if (javersCoreConfiguration.getCommitIdGenerator().equals(RANDOM)) {
            return distributedCommitSeqGenerator.nextId();
        }

        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }
}
