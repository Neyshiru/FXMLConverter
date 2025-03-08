package fxmlconverter.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fxmlconverter.model.ElementFXML;
import fxmlconverter.resources.Resources;

public class Main {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		//File file = new File("C:\\Users\\lucie\\eclipse-workspace-18\\pong\\src\\main\\resources\\pong\\pong\\resources\\views\\MainView.fxml");
		//File file = new File("C:\\Users\\lucie\\eclipse-workspace-18\\skyclinstallerjfx\\src\\main\\resources\\resources\\views\\MainView.fxml");
		File file = new File("C:\\Users\\lucie\\eclipse-workspace-18\\fxmlconverter\\src\\main\\resources\\resources\\views\\MainView.fxml");
		Resources.getFileContent(file);

		List<String> linesImport = new ArrayList<String>();
		List<ElementFXML> linesDeclaration = new ArrayList<ElementFXML>();
        List<ElementFXML> linesFunction = new ArrayList<ElementFXML>();
        List<ElementFXML> actionNames = new ArrayList<ElementFXML>();
        Resources.getInternationalization(file, linesImport, linesDeclaration, linesFunction, actionNames);
        linesDeclaration.forEach(s -> System.out.println(s));
        System.out.println("""
        	
        	private void switchLanguage(Locale locale) {
        		bundle = Resources.getBundle("strings", locale);
        		
        		stage.setTitle(bundle.getString("window.title"));
		""");
        linesFunction.forEach(s -> System.out.println(s));
        System.out.println("	}");
	}

}
