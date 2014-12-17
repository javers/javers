package org.javers.core.commit;

/**
 * Generates unique and monotonically increasing commit identifiers. <br>
 * Thread safe
 *
 * @author bartosz walacik
 */
class CommitSeqGenerator {

    private int seq;
    private CommitId lastReturned;

    public synchronized CommitId nextId(CommitId head) {
        long major = getHeadMajorId(head) + 1;

        if (lastReturned!= null && major == lastReturned.getMajorId()){
            seq++;
        }
        else{
            seq = 0;
        }

        CommitId result = new CommitId(major, seq);
        lastReturned = result;
        return result;
    }

    long getHeadMajorId(CommitId head){
        if (head == null){
            return 0;
        }
        return head.getMajorId();
    }
}

