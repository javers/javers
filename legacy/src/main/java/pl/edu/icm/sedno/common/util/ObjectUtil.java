package pl.edu.icm.sedno.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author bart
 * @see http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
 */
public class ObjectUtil {
	/**
	 * Utility for making deep copies (vs. clone()'s shallow copies) of
     * objects. Objects are first serialized and then deserialized.
     * 
     * Uses standard (slow) serialization
     */
	public static Serializable deepCopy(Serializable source) {
		Object copy = null;
		
		  try {
	            // Write the object out to a byte array
	            ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            ObjectOutputStream out = new ObjectOutputStream(bos);
	            out.writeObject(source);
	            out.flush();
	            out.close();

	            // Make an input stream from the byte array and read
	            // a copy of the object back in.
	            ObjectInputStream in = new ObjectInputStream(
	                new ByteArrayInputStream(bos.toByteArray()));
	            copy = in.readObject();
	        }
	        catch(Exception e) {
	        	throw new RuntimeException("error at deepCopy()",e);
	        }
	        return (Serializable)copy;
	}
	
	/**
	 * Utility for making deep copies (vs. clone()'s shallow copies) of
     * objects. Objects are first serialized and then deserialized.
     * 
     * Uses fast FastByteArrayOutputStream and FastByteArrayInputStream
     */
    public static Serializable fastDeepCopy(Serializable source) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            FastByteArrayOutputStream fbos =
                    new FastByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(source);
            out.flush();
            out.close();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in =
                new ObjectInputStream(fbos.getInputStream());
            obj = in.readObject();
        }
        catch(Exception e) {
        	throw new RuntimeException("error at fastDeepCopy()",e);
        }
        return (Serializable)obj;
    }
	
}
