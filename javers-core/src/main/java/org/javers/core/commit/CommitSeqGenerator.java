package org.javers.core.commit;

import org.javers.common.collections.Objects;

/**
 * Generates unique and monotonically increasing commit identifiers. <br/>
 * Thread safe
 *
 * @author bartosz walacik
 */
public class CommitSeqGenerator {

    private int seq;
    private CommitId lastHead;

    public synchronized CommitId nextId(CommitId head) {
        long major = getHeadMajorId(head) + 1;
        int  minor = seq;

        if (Objects.nullSafeEquals(head,lastHead)){
            seq++;
        }
        else{
            seq = 0;
            lastHead = head;
        }

        return new CommitId(major, minor);
    }

    long getHeadMajorId(CommitId head){
        if (head == null){
            return 0;
        }
        return head.getMajorId();
    }
}

