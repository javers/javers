package pl.edu.icm.crmanager.utils;

import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.util.ReflectionUtil;

/**
 * @author bart
 */
public class CrmReflectionUtil {
    
    public static boolean isCrmProxy(Object obj) {
        return (obj instanceof CrmProxy);
    }
    
    /**
     * Jeśli obj jest CrmProxy zwraca proxowany obiekt, wpp obj
     */
    public static <T extends DataObject> T unproxyC(T obj) {
    	if (obj == null) {
    		return null;
    	}
    	
        if (isCrmProxy(obj)) {
            return (T)((CrmProxy)obj).getInstance();
        }
        
        return  obj;
    }
    
    /**
     * @return unproxyH(unproxyR(obj));
     */
    public static <T extends DataObject> T unproxyCH(T obj) {
        return ReflectionUtil.unproxyH(unproxyC(obj));
    }
}
