package pl.edu.icm.crmanager.logic;

import pl.edu.icm.crmanager.model.WorkTestEntity;

public interface RevisionServiceTestBeanI {

    public abstract int testFlush_simpleChangeOnPersistentObject(
            WorkTestEntity work, boolean autoAccept);

    public abstract int testFlush_complexChangeOnTransientObject(
            WorkTestEntity work, 
            boolean autoAccept);

    public abstract int testFlush_addTransientChildToPersistetObject(
            WorkTestEntity work, boolean autoAccept);
    
    /**
     * exception expected
     */
    public void testAttachingPersistentObjectForNoAcceptSession();

}