package pl.edu.icm.crmanager.diff;

import pl.edu.icm.crmanager.logic.ChangeRequestManager;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.patterns.Visitor;

/**
 * Nakładka na {@link ChangeRequestManager}, tworzy revision (zbiór ChangeRequestów) 
 * na podstawie różnicy pomiędzy nowym stanem obiektu a stanem zapisanym w bazie danych <br/><br/>
 * 
 * Wykorzystuje {@link Visitor} do przechodzenia po strukturze obiektów, stąd
 * poprawna implementacja {@link DataObject#accept(Visitor)} jest kluczowa dla działania diffa
 */
public interface CrmDiffService {
    
    /**
     * NoAccept revision
     * 
     * @param newObjectState zmodyfikowany stan obiektu, którego wcześniejsza wersja jest w bazie danych
     *                       powinien być w stanie DETACHED
     * @param login użytkownik wykonujący zmiany
     */
    public Revision generateRevision(DataObject newObjectState, String login);
    
    
    /**
     * AutoAccept revision, persystuje newObjectState
     * 
     * @param newObjectState zmodyfikowany stan obiektu, którego wcześniejsza wersja jest w bazie danych
     *                       powinien być w stanie DETACHED
     * @param login użytkownik wykonujący zmiany
     */
    public Revision generateRevisionAndAccept(DataObject newObjectState, String login);
    
    /**
     * proxy to {@link ChangeRequestManager#addObjectAndAccept()} 
     */
    public Revision addObjectAndAccept(DataObject domainObject, String login);

}
