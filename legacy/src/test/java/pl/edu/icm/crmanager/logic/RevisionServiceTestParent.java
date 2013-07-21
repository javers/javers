package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import pl.edu.icm.crmanager.model.WorkTestEntity;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;

public class RevisionServiceTestParent {
    @Autowired
    protected DataObjectDAO dataObjectDAO;
    
    protected  WorkTestEntity createTransientWork() {
        WorkTestEntity work = new WorkTestEntity();
        work.setSomeInt(5);
        return work;
    }
    

    
    protected WorkTestEntity createPersistentWork() {
        WorkTestEntity work = new WorkTestEntity();
        work.setSomeInt(5);
        
        dataObjectDAO.saveOrUpdate(work); 
        //dataObjectDAO.flush();
        Assert.assertTrue(work.getId() > 0);        
        return work;   
    }
}
