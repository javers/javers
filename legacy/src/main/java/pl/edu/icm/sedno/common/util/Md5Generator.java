package pl.edu.icm.sedno.common.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author bart
 */
public class Md5Generator {

    private final static String MD_NAME = "MD5";
    private final static String ENCODING = "UTF-8";


    /**
     * Computes hash of passed byte array
     */
    public static String doMd5(byte[] data) {
        MessageDigest messageDigest = getMDInstance();
        messageDigest.update(data);
        return computeStringRepresentation(messageDigest.digest());
    }


    /**
     * @param orgText
     *            text to be hashed
     */
    public static String doMd5(String orgText) {
        return doSaltedMd5(orgText, null);
    }


    /**
     *
     * @param orgText
     *            text to be hashed
     * @param salt
     *            if null - creates unsalted md5
     */
    public static String doSaltedMd5(String orgText, String salt) {
        MessageDigest messageDigest = getMDInstance();
        byte[] encodedText = orgText.getBytes(Charset.forName(ENCODING));

        if (salt != null) {
            byte[] encodedSalt = salt.getBytes(Charset.forName(ENCODING));
            messageDigest.update(encodedSalt);
        }

        messageDigest.update(encodedText);

        return computeStringRepresentation(messageDigest.digest());
    }


    /**
     * Computes a string (hex) representation of the passed data
     */
    private static String computeStringRepresentation(byte[] digest) {
        String hashed = new BigInteger(1, digest).toString(16);
        while (hashed.length() < 32) {
            hashed = "0" + hashed;
        }
        return hashed;
    }


    /**
     * Returns a new instance of message digest algorithm. Converts
     * NoSuchAlgorithmException to RuntimeException.
     */
    private static final MessageDigest getMDInstance() {
        try {
            return MessageDigest.getInstance(MD_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
