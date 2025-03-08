package fxmlconverter.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fxmlconverter.model.ElementFXML;
import fxmlconverter.model.Internationalization;

/**
 * Resources class which takes care of local files.
 * 
 * @author Lhomme Lucien
 */
public abstract class Resources {
	
	private static final String PACKAGE_NAME = Resources.class.getPackageName();
	
	/**
	 * Get the right bundle in the folder "resources/.../bundles" for a specific language.
	 * @param name String
	 * @param locale Locale
	 * @return ResourceBundle
	 */
	public static ResourceBundle getBundle(String name, Locale locale) {
		System.out.println(String.format("%s/bundles.%s", PACKAGE_NAME, name));
		return ResourceBundle.getBundle(String.format("%s/bundles.%s", PACKAGE_NAME, name), locale);
	}
	
	/**
	 * Get the right image in the folder "resources/.../images".
	 * @param name String
	 * @return Image
	 */
	public static InputStream getImage(String name) {
		try {
			return Resources.class.getResource("images/" + name).openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get the url of the path name.
	 * @param name String
	 * @return URL
	 */
	public static URL getURL(String name) {
		System.out.println(String.format("/%s/%s",PACKAGE_NAME, name));
		System.out.println(Resources.class.getResource(name).getPath());
		return Resources.class.getResource(name);
	}
	
	public static String getFileContent(String fileName) throws IOException {
		URL url = Resources.getURL(fileName);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
		String content = getFileContent(bufferedReader);
        bufferedReader.close();
	    return content;
	}
	
	public static String getFileContent(File file) throws IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String content = getFileContent(bufferedReader);
        bufferedReader.close();
        fileReader.close();
        return content;
	}
	
	private static String getFileContent(BufferedReader bufferedReader) throws IOException {
		StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            System.out.println(inputLine);
	    	content.append(inputLine);
	    	content.append(System.lineSeparator());
        }
        return content.toString();
	}
	
	public static String getInternationalization(File file, List<String> linesImport, List<ElementFXML> linesDeclaration, List<ElementFXML> linesFunction, List<ElementFXML> actionNames) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        Element docElement = document.getDocumentElement();
    	ElementFXML element = new ElementFXML();
    	element.setType("ResourceBundle");
    	element.setFxId("bundle");
    	linesDeclaration.add(element);
    	//linesDeclaration.add("	@FXML private ResourceBundle bundle;\n");
        Internationalization.getInternationalization(docElement, linesDeclaration, linesFunction, actionNames);
        getImport(document, linesImport, linesDeclaration);
		return getControllerName(docElement);
	}

	private static void getImport(Document document, List<String> linesImport, List<ElementFXML> linesDeclaration) {
		NodeList childs = document.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
        	Node child = childs.item(i);
			if ("import".equals(child.getNodeName())) {
				String path = child.getTextContent();
				int idxLastDotPath = path.lastIndexOf('.');
				if (isImportableType(path.substring(idxLastDotPath+1), linesDeclaration)) {
					linesImport.add(String.format("import %s;\n", path));
				}
			}
		}
	}
	
	private static boolean isImportableType(String type, List<ElementFXML> linesDeclaration) {
		for (ElementFXML elementFXML : linesDeclaration) {
			if (type.equals(elementFXML.getType())) {
				return true;
			}
		}
		return false;
	}
	
	private static String getControllerName(Element docElement) {
		NamedNodeMap namedNodeMap = docElement.getAttributes();
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
        	Node attribute = namedNodeMap.item(i);
            System.out.println(attribute.getNodeName()+"="+attribute.getTextContent());
            if ("fx:controller".equals(attribute.getNodeName())) {
            	return attribute.getTextContent();
            }
        }
		return null;
	}
	
}
