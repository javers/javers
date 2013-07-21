package pl.edu.icm.sedno.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author bart
 */
public class Suplement {
    public static Properties getPropertiesFromClasspath(String propFileName, ClassLoader classloader){
        // loading xmlProfileGen.properties from the classpath
        Properties props = new Properties();
        
        ClassLoader c = classloader;
        if (c == null)
            c = Thread.currentThread().getContextClassLoader();
        
        InputStream inputStream = classloader.getResourceAsStream(propFileName);

        
        if (inputStream == null) {
            throw new RuntimeException("property file '" + propFileName
                    + "' not found in the classpath");
        }

        try {
            props.load(inputStream);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
       

        return props;
    }
}
