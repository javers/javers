package pl.edu.icm.crmanager.model;

import pl.edu.icm.crmanager.logic.ChangeRequestManager;

/**
 * @author bart
 */
public enum RecType   {
	
	/**
	 * dodanie elementu do kolekcji referencji
	 */
	CHILD_ADD, 
	CHILD_REMOVE, 
	
	REFERENCE_CHANGE, 
	
	/**
	 * zmiana simplePoperty (ValueObject'u), lub zmiana wartości w mapie ValueObject'ów
	 */
	VALUE_CHANGE,
	
	NEW_OBJECT,
	
	/**
	 * dodanie elementu do kolekcji typu simplePoperty (kolekcji lub mapy ValueObject'ów)
	 */
	VALUE_ADD,
	VALUE_REMOVE,
		
	/**
	 * usunięcie elementu,
	 * status tworzony w operacji usunięcia drzewa komponentów {@link ChangeRequestManager#deleteTree()}
	 */
	DELETED	
}