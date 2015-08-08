package org.javers.core.commit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class HandedOutIds {
    private static final Logger logger = LoggerFactory.getLogger(HandedOutIds.class);

    private int limit = 5;

    private List<CommitId> handedOutList = new ArrayList<>();

    void put (CommitId handedOut) {
        if (handedOutList.size() == limit) {
            handedOutList.remove(limit - 1);
        }

        int found = findIndex(handedOut.getMajorId());

        if (found < 0){
            handedOutList.add(0, handedOut);
        } else {
            handedOutList.remove(found);
            handedOutList.add(found, handedOut);
        }
    }

    private int findIndex(Long majorId){
        for (int i=0; i<handedOutList.size(); i++){
            CommitId c = handedOutList.get(i);
            if (c.getMajorId() == majorId){
                return i;
            }

            if (c.getMajorId() < majorId){
                return -1;
            }
        }
        return -1;
    }

    CommitId get(Long majorId)  {
        for (CommitId id : handedOutList){
            if (id.getMajorId() == majorId){
                return id;
            }
        }
        return null;
    }

}
