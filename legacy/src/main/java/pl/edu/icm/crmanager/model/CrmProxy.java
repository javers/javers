package pl.edu.icm.crmanager.model;

import pl.edu.icm.sedno.common.model.DataObject;

/**
 * @author bart
 */
public interface CrmProxy {
    public DataObject getInstance();
    public Revision getRevision();
    public boolean isDetached();
    
    public void setInstance(DataObject instance);
    public void setRevision(Revision crmRevision);
    public void setDetached(boolean detached);
}
