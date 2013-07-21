package pl.edu.icm.sedno.common.model;

import pl.edu.icm.sedno.patterns.Visitable;
import pl.edu.icm.sedno.patterns.Visitor;

/**
 * Podstawowy interfejs dla encji modelu danych
 * 
 * @author bart
 */
public interface DataObject extends Visitable<DataObject> {
    public enum DataObjectStatus {NEW, ACTIVE, DELETED}    
    
    /**
     * Globalny identyfikator obiektu w formacie className#id,
     * ex. my.package.Person#1
     */
    public String getGlobalId();
    
    /**
     * PK obiektu
     */
    public int getId();
    
    /**
     * Oryginalna klasa obiektu (bez persistence proxy)
     */
    public Class getWrappedClass();
    
    /**
     * true jeśli obiekt nie był persystowany
     */
    public boolean isTransient();
    
    public boolean isNew();
    
    /**
     * CRM status: NEW (not accepted), ACTIVE (accepted) and DELETED
     */
    public DataObjectStatus getDataObjectStatus();
    
    public void setDataObjectStatus(DataObjectStatus status);
    
    /**
     * metoda powinna przenawigować po strukturze pod-obiektów, czyli obiektów będących logicznie komponentami this,
     * nie powinna wchodzić do referencji (np słownikowych)
     */
    public void accept(Visitor<DataObject> visitor);
    
    /**
     * metoda powinna dociągnąć (z bazy) dane obiektów powiązanych (np referencji słownikowych), 
     * które nie są dotykane przez standardowy visitor (czyli metodę accept())
     */
    public void initialize();
}
