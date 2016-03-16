package org.javers.common.string;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author bartosz.walacik
 */
public class ShaDigest {
    public static int shortDigest(String text){
        byte[] hashBytes = digest(text);

        int result = 0;
        for (int i=0; i<hashBytes.length; i++){
            result += Math.abs(hashBytes[i]) * (i+1);
        }
        return result;
    }

    public static String longDigest(String text){
        byte[] hashBytes = digest(text);

        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<hashBytes.length; i+=2){
            String hex=Integer.toHexString(0xff & hashBytes[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] digest(String text){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(text.getBytes("UTF-8"));
            return digest.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
