package pl.edu.icm.crmanager.exception;

/**
 * Wyjątek rzucany jeśli mapping ORM'owy w model danych użytkownika ma konstrukcje
 * nie wspierane przez CRM
 *  
 * @author bart
 */
public class UnsupportedMapping extends RuntimeException
{
    
    public UnsupportedMapping(String message) {
        super (message);
    }
}
