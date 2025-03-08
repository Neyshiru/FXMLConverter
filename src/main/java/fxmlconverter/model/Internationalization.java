package fxmlconverter.model;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class Internationalization {

	public static boolean getInternationalization(Node root, List<ElementFXML> linesDeclaration, List<ElementFXML> linesFunction, List<ElementFXML> actionNames) {
        boolean hasPrint = false;
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
	        if (node.getNodeType() == Node.ELEMENT_NODE) {
	            Element elem = (Element) node;
	            NamedNodeMap namedNodeMap = elem.getAttributes();
	            boolean hasFxId = false;
	            boolean isInternationalized = false;
	            boolean isActionable = false;
            	ElementFXML element = new ElementFXML();
	            String fxId = "";
	            String text = "";
	            String actionName = "";
	            for (int j = 0; j < namedNodeMap.getLength(); j++) {
	            	Node attribute = namedNodeMap.item(j);
	            	if ("fx:id".equals(attribute.getNodeName())) {
	            		hasFxId = true;
	            		element.setFxId(attribute.getTextContent());
	            		fxId = attribute.getTextContent();
	            	} else if ("text".equals(attribute.getNodeName()) && attribute.getTextContent().charAt(0) == '%') {
	            		isInternationalized = true;
		            	element.setText(attribute.getTextContent().substring(1));
	            		text = attribute.getTextContent().substring(1);
	            	} else if ("onAction".equals(attribute.getNodeName()) && attribute.getTextContent().charAt(0) == '#') {
	            		isActionable = true;
		            	element.setAction(attribute.getTextContent().substring(1));
	            		actionName = attribute.getTextContent().substring(1);
	            	}
	            }
	            if (hasFxId) {
	            	element.setType(elem.getNodeName());
	            	linesDeclaration.add(element);
	            	//linesDeclaration.add(String.format("	@FXML private %s %s;\n", elem.getNodeName(), fxId));
		            if (isInternationalized) {
		            	hasPrint = true;
		            	linesFunction.add(element);
		            	//linesFunction.add(String.format("		%s.setText(bundle.getString(\"%s\"));", fxId, text));
		            }
	            }
	            if (isActionable) {
	            	actionNames.add(element);
	            	//actionNames.add(actionName);
	            }
		        if (getInternationalization(elem, linesDeclaration, linesFunction, actionNames) && lastIsEmpty(linesFunction)) {
		        	linesFunction.add(null);
		        }
	        }
	        
        }
        return hasPrint;
	}
	
	private static boolean lastIsEmpty(List<ElementFXML> list) {
		return list.get(list.size()-1) != null;
	}

}
