package pl.edu.icm.crmanager.model;

import pl.edu.icm.sedno.common.model.DataObject;

/**
 * @author bart
 * @param <T> type of dataObject to be changed
 */
public interface ChangeAction<T extends DataObject> {

	void execute(T domainObjectProxy);
}
