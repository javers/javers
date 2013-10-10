package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;

public class RevisionServiceTestBean implements RevisionServiceTestBeanI {
    Logger logger = LoggerFactory.getLogger(RevisionServiceTestBean.class);      
    
    @Autowired
    private CrmProxyFactory crmProxyFactory;
    
    @Autowired
    private CrmSessionFactory crmSessionFactory;
    
    @Autowired
    private RevisionService revisionService;
    
    @Autowired
    private ChangeRequestManager changeRequestManager;
    
    @Autowired
    private DataObjectDAO dataObjectDAO;
    
    
    /**
     * exception expected
     */
    @Override
    public void testAttachingPersistentObjectForNoAcceptSession() {
       
        try {
        WorkTestEntity work = new WorkTestEntity();
        dataObjectDAO.saveOrUpdate(work);
        
        changeRequestManager.openCrmSession("mock");
        
        work = changeRequestManager.attach(work);
        work.setSomeInt(4);
        
       logger.info( dataObjectDAO.getObjectShortDesc(work) );
       
        changeRequestManager.closeCrmSessionWithNoAccept();
        }
        catch (RuntimeException e) {
           logger.info("expected exception: ", e.getMessage());
           Assert.assertTrue(e.getMessage().contains("CRM is not going to evict it"));
           throw e;
        }
    }
    
    @Override
    public int testFlush_simpleChangeOnPersistentObject(WorkTestEntity work, boolean autoAccept) {
        int revId = crmSessionFactory.startNewSession("mock").getRevisionId();
        
        work = changeRequestManager.attach(work);
         
        //simple change on persistent object
        
        work.setSomeInt(6);
        Assert.assertEquals(6, work.getSomeInt());
        
        //eof changes
        
        if (autoAccept)
            changeRequestManager.closeCrmSessionWithAutoAccept();
        else
            changeRequestManager.closeCrmSessionWithNoAccept();
                 
        //test if revision is persisted in database - flush() test
        Revision rev = dataObjectDAO.get(Revision.class, revId);
        logger.info(rev.getShortDesc());
        Assert.assertEquals(1, rev.getChangesCount());
        Assert.assertEquals(5, rev.getChangeRequests().get(0).getOldValue());
        Assert.assertEquals(6, rev.getChangeRequests().get(0).getNewValue());
        
        //test revision metadata setting
        if (autoAccept) {
            WorkTestEntity freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId()); 
            Assert.assertEquals(revId, freshWork.getLastRevision().getIdRevision() );
            Assert.assertEquals(1, freshWork.getCrmVersionNo().intValue() );
            Assert.assertEquals("mock", freshWork.getLastChangeAuthor() );
        }
        
        return revId;
    }
  
    @Override
    public int testFlush_complexChangeOnTransientObject(WorkTestEntity work, boolean autoAccept) {
        Assert.assertFalse (dataObjectDAO.isPersistent(work));
        ContributionTestEntity contrib = createPersistentContrib();
        Assert.assertFalse (contrib.isTransient());
        
        
        int revId = crmSessionFactory.startNewSession("mock").getRevisionId();
        work = changeRequestManager.attach(work);  
        
        //persistent child add
        work.setSomeInt(6);
              
        logger.info("id:"+work.getIdWork());
        work.addContribution(contrib);
        
        //eof changes
        
        if (autoAccept)
            changeRequestManager.closeCrmSessionWithAutoAccept();
        else
            changeRequestManager.closeCrmSessionWithNoAccept();
        
        Revision rev = dataObjectDAO.get(Revision.class, revId);
        logger.info(rev.getShortDesc());
        
        return revId;
    }
    
    
    @Override
    public int testFlush_addTransientChildToPersistetObject(WorkTestEntity work, boolean autoAccept) {
        logger.info(dataObjectDAO.getObjectShortDesc(work));
        Assert.assertTrue (dataObjectDAO.isDetached(work));
        int revId = crmSessionFactory.startNewSession("mock").getRevisionId();
        work = changeRequestManager.attach(work);
        
        work.addContribution(new ContributionTestEntity());
        
        //eof changes
        
        if (autoAccept)
            changeRequestManager.closeCrmSessionWithAutoAccept();
        else
            changeRequestManager.closeCrmSessionWithNoAccept();
        
        Revision rev = dataObjectDAO.get(Revision.class, revId);
        logger.info(rev.getShortDesc());
        
        return revId;
    }
    
    protected ContributionTestEntity createPersistentContrib() {
        ContributionTestEntity c = new ContributionTestEntity();
        dataObjectDAO.saveOrUpdate(c);
        
        return c;
    }
    
    
    public void setCrmProxyFactory(CrmProxyFactory crmProxyFactory) {
        this.crmProxyFactory = crmProxyFactory;
    }

    public void setCrmSessionFactory(CrmSessionFactory crmSessionFactory) {
        this.crmSessionFactory = crmSessionFactory;
    }

    public void setRevisionService(RevisionService revisionService) {
        this.revisionService = revisionService;
    }

    public void setDataObjectDAO(DataObjectDAO dataObjectDAO) {
        this.dataObjectDAO = dataObjectDAO;
    }
    
    public void setChangeRequestManager(
            ChangeRequestManager changeRequestManager) {
        this.changeRequestManager = changeRequestManager;
    }
}
