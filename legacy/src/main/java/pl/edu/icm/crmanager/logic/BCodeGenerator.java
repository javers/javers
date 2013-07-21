package pl.edu.icm.crmanager.logic;


/**
 * CrmProxy byte code generator
 * 
 * Tworzy klasy extendujące encje modelu i implementujące interfejs CrmProxy
 *  
 * @author bart
 */
public interface BCodeGenerator {

    /**
     * Uwaga, użytkownik generatora sam musi zadbać o threadSafe
     * <b>Cached</b>
     */
    public Class createCrmProxyClass(Class forClass, boolean showSource);

}