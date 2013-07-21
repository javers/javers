package pl.edu.icm.crmanager.logic;

import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.sedno.common.model.DataObject;

/**
 * @author bart
 */
public interface CrmProxyFactory {
    /**
     * Tworzy proxy, obiekt proxowany (instance) nie jest modyfikowany.
     * Proxy jest dowiązywane do bieżącej sesji
     */
    public CrmProxy createRedoLogProxy(DataObject instance);

}
