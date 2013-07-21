package pl.edu.icm.crmanager.model;

/**
 * Interfejs, który muszą implementować klasy @Embeddable.
 * 
 * Zakłada, że wartość persystuje się na pojedynczej kolumnie typu String,
 * klasa implementująca powinna posiadać konstruktor z argumentem (String databaseValue)
 * 
 * @author bart
 */
public interface CrmSimpleEmbeddable {
    /**
     * Wartość obiektu @Embeddable,
     * tak jak jest zapisywana w bazie danych
     */
    String getDatabaseValue();
       
}
