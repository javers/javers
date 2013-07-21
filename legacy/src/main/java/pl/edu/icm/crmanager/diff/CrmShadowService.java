package pl.edu.icm.crmanager.diff;

import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.model.DataObject;

/**
 * Browser historycznych wersji obiektów domenowych.
 * Działa na zasadzie odwijania znian z wersji aktualnej.
 * 
 * @author bart
 */
public interface CrmShadowService {
	
	/**
	 * Tworzy cień dla podanej wersji obiektu domenowego.
	 * 
	 * Cienie powinny być używane read-only,
	 * w szczególności, nie należy ich persystować.
	 * 
	 * @return cień obiektu {@link Revision#getRootModPoint()}
	 * @throws runtime exception jeśli revision nie ma root'a
	 */
	<T extends DataObject> T getShadow(Revision revision);
}
