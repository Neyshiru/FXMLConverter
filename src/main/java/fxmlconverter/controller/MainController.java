package fxmlconverter.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import fxmlconverter.model.ElementFXML;
import fxmlconverter.resources.Resources;

public class MainController implements Initializable {

	@FXML private ResourceBundle bundle;

	@FXML private VBox vboxMods;

	@FXML private Button btnImport;

	@FXML private Button btnConvert;

	@FXML private Button btnCopyDeclaration;

	@FXML private Button btnCopyFunction;
	
	@FXML private TextArea txtImport;
	
	@FXML private TextArea txtDeclaration;
	
	@FXML private TextArea txtFunction;
	
	@FXML private TextArea txtAction;
	
	@FXML private Button btnCopyAction;
	
	@FXML private Button btnCopyAll;

	private FileChooser fileChooser;
	
	private File selectedFile;

	private Stage stage;
	
	private StringBuilder sbAction;
	
	private String controllerName;
	
	private List<String> linesImport;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void handleDragOver(DragEvent event) {
		if (event.getDragboard().hasFiles())
			event.acceptTransferModes(TransferMode.ANY);
	}

	@FXML
	private void handleDragDrop(DragEvent event) {
		List<File> files = event.getDragboard().getFiles();
		selectedFile = files.get(0);
		System.out.println(selectedFile);
		if (selectedFile.getName().toUpperCase().endsWith(".FXML")) {
			readFile(selectedFile);
		}
	}

	private void readFile(File selectedFile) {
		try {
			txtImport.setText(Resources.getFileContent(selectedFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void importFile() {
		System.out.println("importFile");
		System.getProperties().forEach((k, v) -> {System.out.println(k +"="+ v);});
		if (fileChooser == null) {
			fileChooser = new FileChooser();
			fileChooser.setInitialFileName(bundle.getString("btn.import"));
			fileChooser.getExtensionFilters().add(new ExtensionFilter(".fxml", "*.fxml", "*.FXML"));
		}
		selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			fileChooser.setInitialDirectory(selectedFile.getParentFile());
			readFile(selectedFile);
		}
	}
	
	private File getControllerFile(File fxmlFile, String controllerName) {
		File parent = selectedFile.getParentFile();
        System.out.println(parent);
		//String packageName = controllerName.substring(0, controllerName.indexOf('.'));
        while (!"main".equals(parent.getName())) {
        	parent = parent.getParentFile();
            System.out.println(parent.getName());
        }
    	//parent = parent.getParentFile().getParentFile();
        System.out.println(controllerName.replaceAll("\\.", "/")+".java");
        File file = new File(parent, "java/"+controllerName.replaceAll("\\.", "/")+".java");
        System.out.println(file.getPath());
        System.out.println(file.exists());
		return file;
	}
	
	@FXML
	private void convert() throws ParserConfigurationException, SAXException, IOException {
		System.out.println("convert");
		if (selectedFile == null)
			return;
		linesImport = new ArrayList<String>();
		List<ElementFXML> linesDeclaration = new ArrayList<ElementFXML>();
        List<ElementFXML> linesFunction = new ArrayList<ElementFXML>();
        List<ElementFXML> actionNames = new ArrayList<ElementFXML>();
        controllerName = Resources.getInternationalization(selectedFile, linesImport, linesDeclaration, linesFunction, actionNames);
        //System.out.println(controllerName);
        /*File file = getControllerFile(selectedFile, controllerName);
        String content = Resources.getFileContent(file);
        actionNames.forEach(s -> {
            Pattern pattern = Pattern.compile("@FXML\\s+\\w+\\s\\w+\\s+("+s+")\\("); //test if already exist
            Matcher matcher = pattern.matcher(content);
            boolean find = matcher.find();
            System.out.println(s+" : "+find);
            if (!find) {
                sbAction.append("	@FXML\n");
                sbAction.append("	private void "+s+"() {\n");
                sbAction.append("	}\n");
            }
        });*/
        sbAction = new StringBuilder();
        for (ElementFXML action : actionNames) {
			//System.out.println(action);
            sbAction.append("	@FXML\n");
            sbAction.append("	private void ").append(action.getAction()).append("() {\n");
            sbAction.append("	}\n");
		}
        //if (sbAction.toString().isEmpty()) {
            //txtAction.setText("all actions are declared");
        //} else {
            txtAction.setText(sbAction.toString());
        //}
        StringBuilder sbDecl = new StringBuilder();
        linesDeclaration.forEach(e -> sbDecl.append(String.format("	@FXML private %s %s;\n", e.getType(), e.getFxId())));
        sbDecl.append("	private Stage stage;\n");
        txtDeclaration.setText(sbDecl.toString());
        StringBuilder sbFunct = new StringBuilder();
        sbFunct.append("	private void switchLanguage(Locale locale) {\n");
        //sbFunct.append("		bundle = Resources.getBundle(\"strings\", locale);\n");
        sbFunct.append("		bundle = ResourceBundle.getBundle(\"bundles.strings\", locale);\n");
        sbFunct.append("\n");
        sbFunct.append("		stage.setTitle(bundle.getString(\"window.title\"));\n");
        sbFunct.append("\n");
        linesFunction.forEach(e -> {
        	if (e == null)
        		sbFunct.append('\n');
        	else
        		sbFunct.append(String.format("		%s.setText(bundle.getString(\"%s\"));\n", e.getFxId(), e.getText()));
        });
        sbFunct.append("	}\n");
        txtFunction.setText(sbFunct.toString());
	}
	
	private void copy(String... text) {
		ClipboardContent content = new ClipboardContent();
		StringBuilder sb = new StringBuilder();
		for (String txt : text) {
			sb.append(txt);
			sb.append("\n");
		}
		content.putString(sb.toString());
		Clipboard.getSystemClipboard().setContent(content);
	}
	
	@FXML
	private void copyDeclaration() {
		System.out.println("copyDeclaration");
		copy(txtDeclaration.getText());
	}

	@FXML
	private void copyFunction() {
		System.out.println("copyFunction");
		copy(txtFunction.getText());
	}
	
	@FXML
	private void copyAction() {
		System.out.println("copyAction");
		copy(txtAction.getText());
	}
	
	@FXML
	private void copyAll() {
		System.out.println("copyAll");
		int idxLastDotController = controllerName.lastIndexOf('.');
		StringBuilder classBegin = new StringBuilder();
		classBegin.append("package ").append(controllerName.substring(0, idxLastDotController)).append(";\n\n");
		classBegin.append("import java.net.URL;\n");
		classBegin.append("import java.util.Locale;\n");
		classBegin.append("import java.util.ResourceBundle;\n\n");
		classBegin.append(String.join("", linesImport));
		classBegin.append("import javafx.fxml.Initializable;\n");
		classBegin.append("import javafx.stage.Stage;\n");
		classBegin.append("import javafx.fxml.FXML;\n\n");
		classBegin.append("public class ").append(controllerName.substring(idxLastDotController+1)).append(" implements Initializable {\n");
		String classEnd = "	@Override\n"
			+ "	public void initialize(URL location, ResourceBundle resources) {\n"
			+ "		bundle = resources;\n"
			+ "	}\n"
			+ "}";
		copy(classBegin.toString(), txtDeclaration.getText(), txtAction.getText(), txtFunction.getText(), classEnd);
	}

	@FXML
	public void quit() {
		stage.close();
	}

	@FXML
	public void fullScreen() {
		stage.setFullScreen(!stage.isFullScreen());
	}

	@FXML
	public void switchLanguageEN() {
		switchLanguage(new Locale("en", "EN"));
	}

	@FXML
	public void switchLanguageFR() {
		switchLanguage(new Locale("fr", "FR"));
	}

	/**
	 * Change the text of all references items of FXML files with the good bundle.
	 * (If you want to add new item, you need to add the reference on the top file
	 * and add the "setText" in the method)
	 * 
	 * @param locale Locale
	 */
	private void switchLanguage(Locale locale) {
		bundle = Resources.getBundle("strings", locale);
		//bundle = ResourceBundle.getBundle("bundles.strings", locale);

		stage.setTitle(bundle.getString("window.title"));

		btnImport.setText(bundle.getString("btn.import"));
		btnConvert.setText(bundle.getString("btn.convert"));

		btnCopyDeclaration.setText(bundle.getString("btn.copy"));

		btnCopyFunction.setText(bundle.getString("btn.copy"));
		
		btnCopyAll.setText(bundle.getString("btn.copyAll"));
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		
		readFile(selectedFile=new File("C:\\Users\\lucie\\eclipse-workspace-18\\fxmlconverter\\src\\main\\resources\\fxmlconverter\\resources\\views\\MainView.fxml"));
	}
	
	

}
