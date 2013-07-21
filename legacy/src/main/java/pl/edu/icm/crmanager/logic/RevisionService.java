package pl.edu.icm.crmanager.logic;

import pl.edu.icm.crmanager.model.ChangeRequest;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.model.DataObject;

/**
 * Niskopoziomowy serwis do obsługi CRM revisions
 * 
 * @author bart
 */
public interface RevisionService {
    enum OpType {ADD, REMOVE}

    /**
     * Persystowanie revision oraz,
     * 
     * dla wszystkich mod-pointów:
     * - jeśli są nowe       - persist
     * - jeśli są persistent - throw exception (!)
     * - jeśli są detached   - 
     * 
     * Przestawia status z TRANSIENT na NEW
     */
    void flushWithNoAccept(Revision revision);
    
    /**
     * Persystowanie revision,
     * zmiany wykonane na modelu zostaną od razu zapisane.
     * 
     * dla wszystkich mod-pointów:
     * - jeśli są nowe       - ustawienie NEW = false, persist
     * - jeśli są persistent - 
     * - jeśli są detached   - reattach
     * 
     * Przestawia status z TRANSIENT na ACCEPTED
     */
    void flushWithAutoAccept(Revision revision);
    
    
    /**
     * Dla wszystkich mod-pointów:
     * - jeśli są nowe     - ustawienie flagi isNew na false  + persist
     * - jeśli nie są nowe - aplikacja nagranych zmian        + persist
     * 
     * Przestawia status z NEW na ACCEPTED
     */
    void accept(Revision revision, String acceptedBy);
    
    void cancel(Revision revision, String cancelledBy);
    
    void persist(Revision revision);
    
    void persistNew(Revision revision);
    
    /**
     * could be time consuming
     */
    void initializeTransientReferences(Revision revision);
    
    DataObject loadReference(int refId, String refClass, boolean nullAllowed);

    void dbSafeOperationOnCollection(DataObject modPoint, ChangeRequest cr, OpType opType, DataObject ref);
}
