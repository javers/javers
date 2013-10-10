package pl.edu.icm.crmanager.logic;

import pl.edu.icm.crmanager.model.ChangeAction;
import pl.edu.icm.crmanager.model.ChangeRequest;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.model.DataObject;

import java.util.List;

/**
 * <pre>
 * Change Request Manager to biblioteka narzędziowa, do zarządzania zmianami
 * wykonywanymi na persystentnych obiektach domenowych.
 * Realizuje dwie główne funkcjonalności:
 *   1. Rejestracja wykonanych zmian do celów audytowych (co zmieniono, kiedy, kto)
 *   2. Zarządzanie zmianami oczekującymi: 
 *      - nagrywanie zmian
 *      - aplikowanie zmian zatwierdzonych do modelu domenowego
 *      - wycofywanie zmian
 *      - listowanie zmian oczekujących dla danego obiektu domenowego
 *      - preview, czyli podgląd przyszłego stanu obiektu domenowego
 *      
 * CRM może działać w dwóch scenariuszach NoAccept i AutoAccept. 
 *
 * Założenia ogólne:
 *   1. Model domenowy przechowuje stan aktualny (zatwierdzony),
 *      czyli nie widać w nim zmian oczekujących. Wyjątkiem są nowe, niezatwierdzone
 *      obiekty, które trafiają do modelu ze statusem NEW
 *   2. Biblioteka jest niezależna od domenowego modelu danych
 *   3. Zmiany są grupowane w ponumerowane Revision,
 *      które są przetwarzane transakcyjnie 
 *   4. CRM persystuje nowe obiekty dołączane do sesji
 *   5. Użytkownik musi zadbać o poprawne zapięcie transakcji, która powinna obejmować całą sesję CRM
 *   
 * Założenia techniczne:
 *   1. Biblioteka używa do persystowania helpera DataObjectDAO,
 *      który może być implementowany na JPA lub Hibernate 
 *   2. Główna funkcjonalność nagrywania zmian realizowana jest przez technikę 
 *      Dynamic Proxy, podobnie jak robi to Hibernate
 *   3. Model domenowy jest 'prawie' nieświadomy istnienia CR Managera,
 *      wyjątki są opisane niżej 
 *   4. CR Manager operuje pojęciem sesji, która jest dowiązywana do wątku użytkownika.
 *      Sesja jest otwierana na czas nagrania jednego Revision   
 *           
 * Wyjątki od reguły: 'model domenowy jest nieświadomy istnienia CR Managera'
 * i inne ograniczenia
 *   1. Obiekty domenowe muszą implementować interfejs {@link DataObject}
 *   2. PK powinny być pojedynczymi intami
 *   3. Annotacje dajemy na poziomie getterów a nie pól 
 *   4. nie obsługujemy (jeszcze) tworów w rodzaju @CollectionOfElements ale @Embedded już tak (chociaż nie w 100%)  
 * 
 * </pre>
 * @author bart
 */
public interface ChangeRequestManager {
   
    /**
     * Wygodna (fasadowa) metoda do rejestrowania w CRM i zapisywania w bazie <b>nowych</b> obiektów w 
     * trybie auto-accept
     * 
     * @param domainObject transientowy obiekt
     */
    Revision addObjectAndAccept(DataObject domainObject, String login);
	
	/**
	 * Otwiera sesję CRM
	 *  
	 * @param login użytkownik wykonujący zmiany
	 * @return revisionId
	 */
     int openCrmSession(String login);
	     
 	/**
 	 * <pre>
 	 * Scenariusz NoAccept
 	 * 
 	 * Dla każdego obiektu w sesji:
     *   Jeśli jest nowy - zostanie zapisany do głównego modelu ze statusem NEW,
     *   jeśli jest persistent - evict, zmiany będą czekać w Revision na akceptację</pre>
     *   
     * Persist nagranych CR-ów
 	 * </pre>
 	 */
	 void closeCrmSessionWithNoAccept();
	 
	 <T extends DataObject> Revision doInCrmWithNoAccept(T domainObject, ChangeAction<T> action, String login);
	 
	 <T extends DataObject> Revision doInCrmWithAutoAccept(T domainObject, ChangeAction<T> action, String login);
	 
	 /**
	  * Przestawia status danego obiektu i wszystkich jego komponentów na DELETED,
	  * zmiana jest automatycznie akceptowana
	  */
	 <T extends DataObject> Revision deleteTree(T domainObject, String login);
	 
	 /**
	  * <pre>
	  * Scenariusz AutoAccept
	  * 
	  * Tylko nagrywanie zmian, CRM nie rusza obiektów dołączanych do sesji, 
	  * wykonane zmiany od razu trafią do głównego modelu
      * 
      * Persist nagranych CR-ów
      * </pre>
      */
	 void closeCrmSessionWithAutoAccept();
	 
	 /**
	  * <pre>
	  * Dołącza główny obiekt (rootModPoint) do sesji CRM, zmiany na nim powinny być wykonywane via CrmProxy.
	  * 
	  * Tabela akcji wykonywanych w zal. od stanu obiektu i scenariusza pracy:
	  * 
      *                      AutoAccept                    NoAccept  
      *  ObjectState         attach,  close s.             attach,  close s.
      *  -----------         ----------------              ----------------
      *  Transient           NEW = 1, NEW = 0 + persist    NEW = 1 , persist
      *  Persistent                 ,                              , throw ex.
      *  Detached                   , reattach                     ,
	  * 
	  * Dodtkowo, ustawia dany obiekt jako rootModPoint w currentRevision
	  * 
	  * @return CrmProxy <br/>
	  * Uwaga: proxy ma funkcjonalność (leniwą) dynamicznego dołączania do sesji
	  * obiektów powiązanych przez referencje. <br/>
	  * Oznacza to, że mozna dołączyć explicite do sesji 
	  * np tylko jeden obiekt i nagrywać zmiany na grafie obiektów
	  * (pod warunkiem, że przenawigujemy do nich po referencjach CrmProxy)
	  * 
	  */
	 <T extends DataObject> T attach(T domainObject);
	 
	 /**
	  * To samo co attach(), ale dla zwykłych obiektów (innych niż rootModPoint)
	  */
	 <T extends DataObject> T attachComponent(T domainObject);
	 
	 /**
	  * Rejestruje obiekt jako NEW z pominięciem domyślnej heurystyki wyliczania flagi NEW,
	  * ma sens tylko w scenariuszu NoAccept. <br/>
	  * 
	  * Wykonuje attach()
	  */
	 <T extends DataObject> T attachForceNew(T domainObject);
	  
	 /**
	  * Zatwierdza zmiany oczekujące (przestawia status w rekordach ChangeRequest)
	  * i aplikuje je do modelu domenowego
	  * 
	  * @param acceptedBy login akceptanta
	  */
	 void acceptRevision(int revisionId, String acceptedBy);
	 
	 /**
	  * Anulowanie zmian oczekujących, przestawia status w rekordach ChangeRequest,
	  * usuwa obiekty, które mają status NEW
	  * 
	  * @param cancelledBy login kancelanta
	  */
	 void cancelRevision(int revisionId, String cancelledBy);
	 
	 /**
	  * Podgląd stanu obiektu z zaaplikowanymi zmianami oczekującymi 
	  */
	 <T extends DataObject> T previewChanges(T domainObject);	 	 
	 
	 List<ChangeRequest> getPendingRequests(DataObject domainObject);
	 	 	 
	 /**
      * Rzuci wyjątek jeśli nie ma aktywnej sesji CRM
      */
     Revision getCurrentRevision();
         
     /**
      * true jeśli jest aktywna sesja CRM
      */
     boolean isActiveSession();     
     
     Revision loadRevision(int revisionId);
     
     /**
      * <pre>
      * initializes changeRequests and all transient references:
      * - {@link Revision#getRootModPoint()}
      * - {@link ChangeRequest#getNode__()}
      * - {@link ChangeRequest#getOldReference__()}
      * - {@link ChangeRequest#getNewReference__()}
      * 
      * could be time consuming
      * </pre>
      */
     Revision loadRevisionWithReferences(int revisionId);
     
     /**
      * prosty search po Revision.getRootModPoint, posortowane chronologicznie
      */
     List<Revision> getRootRevisions(DataObject domainObject);
     
     
     /**
      * prosty search po Revision.getRootModPoint, daje najnowsze revision lub null jeśli brak
      */
     Revision getLastRootRevision(DataObject domainObject);
            
}
