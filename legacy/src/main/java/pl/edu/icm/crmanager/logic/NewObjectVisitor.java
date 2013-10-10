package pl.edu.icm.crmanager.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.edu.icm.crmanager.model.CrmSession;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.patterns.Visitor;

/**
 * Visitor do rejestrowania nowych obiektów oraz ich child-objektów
 * @author bart
 */
public class NewObjectVisitor implements Visitor<DataObject> {

	private Set<String> dbObjectsGlobalIds;
	private DataObjectDAO dataObjectDAO;
	
	public NewObjectVisitor() {
		
	}
	
	public NewObjectVisitor(List<DataObject> dbObjectsList, DataObjectDAO dataObjectDAO) {
		this.dataObjectDAO = dataObjectDAO;
		
		dbObjectsGlobalIds = new HashSet<String>();
		
		for (DataObject obj : dbObjectsList) {
			dbObjectsGlobalIds.add(obj.getGlobalId());
		}
	}
	
    @Override
    public void visit(DataObject object) {
        CrmSession session = CrmSessionFactoryImpl.getCurrentSessionS();
        if (object.isTransient()) {
        	session.getRevision().registerNewObject(object);
        } 

        if (dataObjectDAO == null || dbObjectsGlobalIds == null) 
        	return;
        
        if (dataObjectDAO.isDetached(object) && !dbObjectsGlobalIds.contains(object.getGlobalId())) {
        	session.getRevision().registerNewObject(object);
        }
    }

}
