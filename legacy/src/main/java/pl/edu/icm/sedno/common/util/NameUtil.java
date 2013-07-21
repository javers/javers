package pl.edu.icm.sedno.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * Util do inicjałów imion i nazwisk
 * 
 * @author bart
 */
public class NameUtil {
	private static final String INITIALS_REGEXP = "\\s*(\\p{L}((\\.\\s*)|\\s+|$))+\\s*";
	
	/**
	 * John -> J
	 * J.   -> J
	 */
	public static String getInitial(String fromName) {
		if (fromName == null) {
			return null;			
		}
		
		String fromName_ = fromName.trim();
		
		if (StringUtils.isEmpty(fromName_)) {
			return null;
		}
		
		String firstChar = fromName_.substring(0,1);
		
		if (StringUtils.isAlpha(firstChar)) {
			return firstChar.toUpperCase();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Pawlikowska-Jasnorzewska -> Pawlikowska Jasnorzewska
	 * Pawlikowska Jasnorzewska -> Pawlikowska Jasnorzewska
	 * Pawlikowska              -> Pawlikowska
	 */
	public static String normalizeMultipartName(String name) {
		if (name == null) {
			return null;			
		}
		return name.replace('—', ' ')
				   .replace('-', ' ')
				   .trim();
	}
	
	/**
	 * J    -> true
	 * J.   -> true
	 * j j  -> true
	 * J. j -> true
	 * John -> false
	 * Bo   -> false
	 */
	public static boolean isInitial(String name) {		
		if (name == null) {
			return false;			
		}
		return name.matches(INITIALS_REGEXP);
	}
}
