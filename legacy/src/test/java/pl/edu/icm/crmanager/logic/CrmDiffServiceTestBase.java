package pl.edu.icm.crmanager.logic;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import pl.edu.icm.crmanager.diff.CrmDiffService;
import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.WorkTestEntity;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;

public class CrmDiffServiceTestBase {
    static final int OLD_SOME_INT = 5;

	protected WorkTestEntity work  =  null;    
    
    @Autowired
    protected CrmDiffService crmDiffService;  
    
    @Autowired
    protected ChangeRequestManager changeRequestManager;
    
    @Autowired
    protected DataObjectDAO dataObjectDAO;
    
    protected JdbcTemplate template;

    @Autowired
    public void setSednoCoreDB(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }
    
    public static void deleteTestWorks(JdbcTemplate template) {
    	template.update("delete from te_work_many_to_many");
		template.update("update te_work set fk_main_contributor = null, fk_parent_work = null");
		template.update("delete from te_contribution");
		template.update("delete from te_work");
    }
    
    public void before() {        
        
    	deleteTestWorks(template);
    	
        work  =  new WorkTestEntity();
        work.setSomeInt(OLD_SOME_INT);
        //work.setType(WorkType.A);
        dataObjectDAO.saveOrUpdate(work);  
        
        fillWithPersistentContribs(work);
        
        dataObjectDAO.evict(work);
                
        rebuildSet(work.getContributions());
    }
    
    private void fillWithPersistentContribs(WorkTestEntity work) {
        ContributionTestEntity con = new ContributionTestEntity("e1");     
        work.addContribution(con);
        
        ContributionTestEntity con2 = new ContributionTestEntity("e2");
        work.addContribution(con2);
        
        dataObjectDAO.saveOrUpdate(con);
        dataObjectDAO.saveOrUpdate(con2);
    }
    
    protected void rebuildSet(Set set) {
        Set copy = new HashSet(set);
        set.clear();
        for (Object o : copy)
            set.add(o);
    }
    
    protected boolean containsContribName(WorkTestEntity work, String contribName) {
        for (ContributionTestEntity c : work.getContributions()) {
            if (contribName.equals(c.getContributorName())) return true;
        }
        return false;
    }
    
    protected ContributionTestEntity getContribByName(WorkTestEntity work, String contribName) {
        for (ContributionTestEntity c : work.getContributions()) {
            if (contribName.equals(c.getContributorName())) return c;
        }
        return null;
    }
}
