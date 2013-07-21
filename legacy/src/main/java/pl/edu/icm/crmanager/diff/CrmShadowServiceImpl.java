package pl.edu.icm.crmanager.diff;

import static pl.edu.icm.sedno.common.model.ADataObject.formatGlobalId;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.logic.ChangeRequestManager;
import pl.edu.icm.crmanager.logic.RevisionService;
import pl.edu.icm.crmanager.logic.RevisionService.OpType;
import pl.edu.icm.crmanager.model.ChangeRequest;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;
import pl.edu.icm.sedno.common.model.DataObject;

/**
 * @author bart
 * @author mpol@icm.edu.pl
 */
@Service("crmShadowService")
public class CrmShadowServiceImpl implements CrmShadowService {
	Logger logger = LoggerFactory.getLogger(CrmShadowServiceImpl.class);
	
	@Autowired private DataObjectDAO dataObjectDAO;
	@Autowired private RevisionService revisionService;
	@Autowired private ChangeRequestManager crm;
	
	@Override
	public <T extends DataObject> T getShadow(Revision target) {
		//

		T rootVersion = (T)revisionService.loadReference(target.getRootModPointId(), target.getRootModPointClass(), false);
		

		logger.info("rootVersion: "+ dataObjectDAO.getObjectShortDesc(rootVersion) );
		dataObjectDAO.initializeAndEvict(rootVersion);

        ExtractPersistentComponents visitor = new ExtractPersistentComponents();
        rootVersion.accept(visitor);
        // TODO refactor toMap to visitor
        Map<String, DataObject> modPointMap = CrmDiffServiceImpl.toMap(visitor.getResult());

		List<Revision> revs = crm.getRootRevisions(rootVersion);
		ListIterator<Revision> it = revs.listIterator(revs.size());
		for (Revision rev = it.previous(); !rev.equals(target); rev = it.previous()) {
            rollback(rev, modPointMap);
        }

		return rootVersion;
	}

    private void rollback(Revision rev, Map<String, DataObject> modPointMap) {
        logger.debug("Rolling back {}", rev);
        List<ChangeRequest> requests = rev.getChangeRequests();
        ListIterator<ChangeRequest> it = requests.listIterator(requests.size());
        while (it.hasPrevious()) {
            ChangeRequest cr = it.previous();
            DataObject modPoint = modPointMap.get(cr.getNodeGlobalId());
            switch (cr.getRecType()) {
            case VALUE_CHANGE: 
            	cr.applyValueChange(modPoint, cr.getOldValue());
                break;
            case REFERENCE_CHANGE:
                cr.applyValueChange(
                        modPoint,
                        findNode(modPointMap, cr.getOldReferenceClass(), cr.getOldReferenceId()));
                break;
            case NEW_OBJECT:
                findNode(modPointMap, cr.getNodeClass(), cr.getNodeId());
                break;
            case CHILD_ADD:
                revisionService.dbSafeOperationOnCollection(
                        findNode(modPointMap, cr.getNodeClass(), cr.getNodeId()),
                        cr,
                        OpType.REMOVE,
                        findNode(modPointMap, cr.getNewReferenceClass(), cr.getNewReferenceId()));
                break;
            case CHILD_REMOVE:
                revisionService.dbSafeOperationOnCollection(
                        findNode(modPointMap, cr.getNodeClass(), cr.getNodeId()),
                        cr,
                        OpType.ADD,
                        findNode(modPointMap, cr.getOldReferenceClass(), cr.getOldReferenceId()));
                break;
            default:
                throw new CrmRuntimeException("Unsupported request type "+cr.getRecType());
            }
        }
    }

    private DataObject findNode(Map<String, DataObject> modPointMap, String clazz, int id) {
        DataObject o = modPointMap.get(formatGlobalId(clazz, id));
        if (o == null) {
            o = revisionService.loadReference(id, clazz, false);
            dataObjectDAO.evict(o);
            modPointMap.put(o.getGlobalId(), o);
        }
        return o;
    }
}
