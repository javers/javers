package pl.edu.icm.sedno.common.model;

import org.hibernate.proxy.HibernateProxy;

/**
 * @author jaskomar
 */
public class ADataObjectUtil {
    /**
     * Rozpakowanie globalnego identyfikatora na czesci skladowe
     */
    private static String[] unpackGlobalId(String globalId) {
        if (globalId == null)
            throw new RuntimeException("Cannot unpack null global id!");
        String[] unpacked = globalId.split("[\\#]");
        int len = unpacked.length;
        if (len != 2 && len != 3)
            throw new RuntimeException("GlobalId \"" + globalId
                    + "\" is not correct: it should contain 2 or 3 parts delimited by #");
        if (len == 3 && (!"Transient".equals(unpacked[1])))
            throw new RuntimeException("GlobalId \"" + globalId
                    + "\" is not correct: it contains 3 elements, but the second one is not \"Transient\"");
        String actualId = unpacked[len - 1];
        try {
            Integer.parseInt(actualId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("GlobalId \"" + globalId
                    + "\" is not correct: could not parse the actual id to number: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
        return unpacked;
    }


    /**
     * Rozpakowanie globalnego identyfikatora na czesci skladowe - pobranie
     * informacji czy obiekt jest Transient
     */
    public static boolean unpackGlobalId_IsTransient(String globalId) {
        return unpackGlobalId(globalId).length == 3;
    }


    /**
     * Rozpakowanie globalnego identyfikatora na czesci skladowe - pobranie
     * nazwy klasy obiektu
     */
    public static String unpackGlobalId_Class(String globalId) {
        return unpackGlobalId(globalId)[0];
    }
    
    /**
     * Rozpakowanie globalnego identyfikatora na czesci skladowe - pobranie
     * wlasciwego identyfikatora obiektu
     */
    public static int unpackGlobalId_Id(String globalId) {
        String[] unpacked = unpackGlobalId(globalId);
        return Integer.parseInt(unpacked[unpacked.length - 1]);
    }

    /**
     * @author bart
     */
    public static boolean isHibernateProxy(DataObject obj) {
        return (obj instanceof HibernateProxy);
    }
    
    /**
     * @author bart
     */    
    public static DataObject unproxyH(DataObject obj) {
        if (isHibernateProxy(obj)) {
            return (ADataObject)((HibernateProxy)obj).getHibernateLazyInitializer().getImplementation();
        }
        
        return  obj;
    }
}
