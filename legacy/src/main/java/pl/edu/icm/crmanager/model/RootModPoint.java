package pl.edu.icm.crmanager.model;

import java.util.Date;


/**
 * Obiekt modelu danych, który jest głównym punktem większej struktury (ex. drzewa).
 * 
 * Dostaje referencję do ostatniego revision, oraz metody do wyciągania metadanych z
 * ostatniego revision
 * 
 * @author bart
 */
public interface RootModPoint {
	
	/**
     * Obiekt jest zamrożony i nie może być edytowany jeśli ma otwarte Revision 
     */
    public boolean isFrozen();
    
    public Revision getLastRevision();
    
    /**
     * numer kolejny wersji w CRM
     */
    public Integer getCrmVersionNo();
    
    
    /**
     * userId z ostatniego revision
     */
    public String getLastChangeAuthor();
    
    
    /**
     * createDate z ostatniego revision
     */
    public Date getRevisionDate();
    
    public void setLastRevision(Revision lastRevision);
    
    public void incrementCrmVersionNo();

}
