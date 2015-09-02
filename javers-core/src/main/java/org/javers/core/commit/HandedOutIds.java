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

    private int qLimit = 50;

    private List<CommitId> handedOutList = new ArrayList<>();

    void put (CommitId handedOut) {

        int found = findExistingIndex(handedOut.getMajorId());

        if (found >= 0){
            handedOutList.remove(found);
            handedOutList.add(found, handedOut);
            maintainQueueSize(found);
        } else {
            int insertTo = findInsertIndex(handedOut.getMajorId());
            handedOutList.add(insertTo, handedOut);
            maintainQueueSize(insertTo);
        }

    }

    private void maintainQueueSize(int touchedIndex) {
        if (touchedIndex < qLimit /2) {
            if (handedOutList.size() > qLimit) {
                handedOutList.remove(handedOutList.size() - 1);
            }
        }
        else {
            qLimit += qLimit/10;
        }

    }

    private int findInsertIndex(Long majorId) {
        if (handedOutList.size() == 0){
            return 0;
        }

        int i = 0;
        while (i < handedOutList.size() &&  handedOutList.get(i).getMajorId() > majorId) {
            i++;
        }

        if (i == handedOutList.size()) {
            logger.error("DANGER, inserting {} at the end of handedOutList: ",majorId);
            logger.error(handedOutList.toString());
        }

        return i;
    }

    private int findExistingIndex(Long majorId){
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
