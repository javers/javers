package pl.edu.icm.crmanager.model;

import pl.edu.icm.sedno.common.model.DataObject;

/**
 * wtyczka do klasyfikowania zmian jako istotne, obsługuje jeden modPoint getter 
 * 
 * T - modPoint type
 * V - getter return type
 * 
 * @author bart
 */
public interface ChangeVoter<T extends DataObject, V extends Object> {
	/**
	 * @return true jeśli zmiana jest istotna, false jeśli brak głosu
	 *        (voter nie może zagłosować, że zmiana jest nieistotna)
	 */
	boolean isChangeImportant(T modPoint, V oldValue, V newValue, ChangeRequest cr);
		
}
