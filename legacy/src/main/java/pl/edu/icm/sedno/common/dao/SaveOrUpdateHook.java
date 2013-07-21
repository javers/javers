package pl.edu.icm.sedno.common.dao;

import pl.edu.icm.crmanager.model.Change;
import pl.edu.icm.sedno.common.model.DataObject;

/**
 * @author bart
 */
public interface SaveOrUpdateHook {
	void afterSaveOrUpdate(DataObject dataObject, Change cr);
}
