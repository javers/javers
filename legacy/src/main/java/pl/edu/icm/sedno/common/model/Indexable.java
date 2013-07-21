package pl.edu.icm.sedno.common.model;

import java.util.Date;

/**
 * Encja realizująca ten interfejs ma dodatkowe kolumy:
 * - mod_date z datą ostatniej zmiany
 * - oraz indexed_date z datą ostatniej aktualizacji indeksu (solr)
 *
 * @author bart
 */
public interface Indexable {

    /**
     * Ustawia modDate na now
     */
    public void resetModDate();

    public Date getModDate();

    public Date getIndexedDate() ;
}
