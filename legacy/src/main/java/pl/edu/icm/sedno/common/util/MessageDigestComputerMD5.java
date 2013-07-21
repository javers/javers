package pl.edu.icm.sedno.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestComputerMD5 implements MessageDigestComputer {

    private final MessageDigest messageDigest;

    private byte[] myDigest;


    public MessageDigestComputerMD5() {
        myDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean finished() {
        return myDigest != null;
    }


    @Override
    public void update(int result) {
        if (finished()) {
            throw new RuntimeException("Message digest already closed");
        }
        messageDigest.update((byte) result);
    }


    @Override
    public void update(byte[] b, int off, int len) {
        if (finished()) {
            throw new RuntimeException("Message digest already closed");
        }
        messageDigest.update(b, off, len);
    }


    @Override
    public byte[] getDigest() {
        if (myDigest == null) {
            myDigest = messageDigest.digest();
        }
        return myDigest;
    }


    @Override
    public String getStringDigest() {
        String hashed = new BigInteger(1, getDigest()).toString(16);
        while (hashed.length() < 32) {
            hashed = "0" + hashed;
        }
        return hashed;

    }

}
