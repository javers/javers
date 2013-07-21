package pl.edu.icm.sedno.common.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Useful in construction of message digest friendly {@link InputStream}s and
 * {@link OutputStream}s (handles the ugly write(int) case
 *
 * @author Marcin Jaskolski
 *
 */
public interface MessageDigestComputer {

    /**
     * Returns true iff computation of digest is finished
     *
     * @return
     */
    boolean finished();


    /**
     * Updates the digest with single integer (will be cast to byte). It is
     * required to be compatible with Input/Output Streams, which do have
     * operations reading/writing an integer.
     *
     * @param result
     */
    void update(int result);


    /**
     * Updates the digest with a byte array
     *
     * @param b
     * @param off
     * @param len
     */
    void update(byte[] b, int off, int len);


    /**
     * Finishes the digest computation and returns message digest as an array of
     * bytes. After this call it's not possible to update digest.
     *
     * @return
     */
    byte[] getDigest();


    /**
     * Finishes the digest computation and returns message digest as a String.
     * After this call it's not possible to update digest.
     *
     * @return
     */
    String getStringDigest();

}
