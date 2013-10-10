package pl.edu.icm.sedno.common.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang.StringUtils;
import org.apache.xerces.util.XMLChar;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.common.opensearch.InputStreamStringReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Util do parsowania XML'a i walidacji z XSD
 * 
 * @author bart
 */
public class XmlHelper {
    private static final Logger logger = LoggerFactory.getLogger(XmlHelper.class);
    
    /**
     * Parsowanie pliku xml bez validacja ze schemą
     * 
     * @param resourcePath pełna ścieżka i nazwa zasobu na classpath,
     *                     ex. xml/import/some_file.xml
     */
    public static Document parseFromClasspathResource(String resourcePath) {    	
    	
    	InputStream inputStream = resourceAsStream(resourcePath);    	      
        return parse(inputStream);        
    }
   
    /**
     * @param resourcePath pełna ścieżka i nazwa zasobu na classpath,
     *                     ex. import/file.txt
     * @return resource as String, encoding UTF-8
     */
    public static String readFromClasspathResource(String resourcePath) {  
    	InputStream in = resourceAsStream(resourcePath);   
    	try {
    		return InputStreamStringReader.readFromStream(in);
    	}catch (IOException e) {
    		throw new RuntimeException(e);
		}
    }
    
	private static InputStream resourceAsStream(String resourcePath) {
		InputStream inputStream =  XmlHelper.class.getClassLoader().getResourceAsStream(resourcePath);    	   
        if (inputStream == null) {
        	   inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        }
        if (inputStream == null) {
        	throw new IllegalArgumentException("classpath resource ["+resourcePath+"] not found");
        }
		return inputStream;
	}
    
    public static String serializeToXml(Object obj) {
    	XStream xstream = new XStream(new DomDriver());
    	return xstream.toXML(obj);
    }
    
    public static Object deserializeFromXml(String xml) {
    	XStream xstream = new XStream(new DomDriver());
    	return xstream.fromXML(xml);
    }
    
    /**
     * Parsowanie pliku xml bez validacja ze schemą, <br/>
     * wejście: treść pliku
     * 
     * @param inFileContent Treść dokumentu do sparsowania 
     */
    public static Document parse(String inFileContent) {
        return parse_(new StringReader(inFileContent));
    }
    
    /**
     * analogicznie do {@link #parse(String)}
     */
    public static Document parse(InputStream in) {    	
    	try {
			return parse_(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }
    
    private static Document parse_(Reader reader) {    	
        Document doc = null; 
        try {
            SAXBuilder builder = prepareBuilder();
            doc = builder.build(reader);
        }
        catch (JDOMException j) {         
            logger.error("Error parsing document");
            logger.error("JDOMException: " + j.getMessage());           
            throw new RuntimeException("error parsing xml",j);
        }   
        catch (IOException e) {
            throw new RuntimeException(e.getClass().getName() + " - " + e.getMessage(),e);
        }    
        
        logger.debug("Document parsed successfully");
        
        return doc;    	
    }
    
    /**
     * Parsowanie pliku xml i validacja zgodności ze schemą, <b>wejście: String</b>,
     * w przypadku błędu skłądni lub schemy, rzuca JDOMException.
     * 
     * @param inFileContent Treść dokumentu do sparsowania
     * @param namespace pełna nazwa namespace, ex http://yadda.icm.edu.pl/bwmeta-2.1.0.xsd
     * @param schemaPath Lokalizacja schemy: classpath resource lub URL
     */
    public static Document parse(byte[] inFileContent, String namespace, String schemaPath) throws JDOMException {
        
        Document doc = null;        
        
        try {           
            SAXBuilder builder = prepareBuilder(namespace, schemaPath);
            doc = builder.build(new ByteArrayInputStream(inFileContent));
        } catch (JDOMException j) {         
            logger.error("JDOMException: " + j.getMessage());           
            throw j;
        }   
        catch (IOException e) {
            throw new RuntimeException(e.getClass().getName() + " - " + e.getMessage(),e);
        }           
        
        logger.debug("Document parsed successfully, it's valid according to Schema: '"+schemaPath+"'");
        
        return doc;
    } 
    
    /**
     * Uogolnia adres schem'y : jesli podany url ma prefix http - funkcja zwraca go w niezmienionej formie.
     * Wpp sprawdza czy dany zasob jest na classpath'ie i zwrca url,
     * jeśli nie ma - zwraca go w niezmienionej formie
     */
    private static String getFullSchemaURL(String schemaURL){
        if (schemaURL.startsWith("http://")) {
            return schemaURL;
        } else {
            URL resourceURL = XmlHelper.toResourceURL(schemaURL);
            if (resourceURL != null) {
                // jest resource
                return resourceURL.toExternalForm();
            }            
        }
        
        return schemaURL;
    }
    
    /**
     * Znajduje w classpath plik o podanej scieżce i nazwie 
     */
    public static URL toResourceURL(String resourcePath) {
        URL schemaURL = XmlHelper.class.getClassLoader().getResource(resourcePath);
        if (schemaURL == null) {
            schemaURL = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        }
        return schemaURL;
    }
    
    /**
     * SAXBuilder z wyłączoną walidacją ze schemą
     */
    public static SAXBuilder prepareBuilder() {
        return new SAXBuilder("org.apache.xerces.parsers.SAXParser", false);
    }
    
    /**
     * SAXBuilder z włączoną walidacją ze schemą
     * 
     * @param namespace pełna nazwa namespace, ex http://yadda.icm.edu.pl/bwmeta-2.1.0.xsd
     *                  lub emptyString dla noNamespaceSchemaLocation
     * @param schemaPath Lokalizacja schemy: classpath resource lub URL
     */
    public static SAXBuilder prepareBuilder(String namespace, String schemaPath) {
        String schemaUrl = getFullSchemaURL(schemaPath);
        
        //sprawdzenie czy plik schemy istnieje
        if (schemaUrl.length() > 4 && schemaUrl.substring(0,4).equals("http") ) {
                int respCode;
                
                try {
                    URL url = new URL(schemaUrl);
                    respCode = ((HttpURLConnection) url.openConnection()).getResponseCode();
                } catch (MalformedURLException e) {
                    throw new RuntimeException("XMLHelper: Can't read schema, MalformedURLException for ["+schemaUrl+"]",e);
                } catch (IOException e) {
                    throw new RuntimeException("XMLHelper: Can't read schema, IOException for ["+schemaUrl+"]",e);
                }   
                
                if (respCode != 200) {
                    throw new RuntimeException("XMLHelper: Can't read schema, responseCode "+respCode+" from "+schemaUrl);
                }
        }
    
        //parsowanie SAX'em (implementacja Xerces) z włączoną walidacją ze Schemą 
        SAXBuilder builder =
            new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
     
       
        //logger.debug("SAXBuilder.getDriverClass: "+builder.getDriverClass());       
        //logger.debug("Xerces ver.: "+org.apache.xerces.impl.Version.getVersion());
    
        
        if (StringUtils.isEmpty(namespace)) {
            builder.setProperty(
                    "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                    schemaUrl);            
        }else{
            builder.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
                    namespace +" " + schemaUrl);
        }
        //ex   //"http://yadda.icm.edu.pl/bwmeta-2.1.0.xsd jar:file:/C:/Users/bart/.m2/repository/pl/edu/icm/yadda/bwmeta-core/2.1.0/bwmeta-core-2.1.0.jar!/pl/edu/icm/yadda/bwmeta/xsd/bwmeta-2.1.0.xsd");
        
        //logger.debug("prepareBuilder() - using schema: "+namespace +" " + schemaUrl);
        
        builder.setFeature("http://apache.org/xml/features/validation/dynamic", true);
        builder.setFeature("http://apache.org/xml/features/validation/schema", true);
        builder.setValidation(true);
              
        return builder;
    }
    
    
    /**
     * Drukowanie z formatowaniem pretty, encoding UTF-8
     */
    public static String prettyFormat(Element element){
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat( Format.getPrettyFormat() );   
        return outputter.outputString(element);
    }
    
    /**
     * Replaces any invalid xml character with replaceChar. Uses {@link XMLChar#isValid(int)} to check the characters.
     */
    public static String replaceInvalidXMLCharacters(String text, char replaceChar) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (XMLChar.isValid(c)) {
                sb.append(c);
            } else {
                sb.append(replaceChar);
            }
        }
        return sb.toString();
    }
}
