package optimization;

import GUI.EvaluationWindow;
import application.Session;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import suspensionSystem.SuspensionSystem;

public class OptimizationConfig {
	//Graphic properties
	private BorderPane rootPane;
	private SuspensionSystem suspensionSystem;
	private TargetCurveDefinition tcd;
	private EvaluationWindow ew;
	
	private Stage primaryStage;
	private Scene scene;
	
	OptimizationEngine oe;
	
	public OptimizationConfig (Stage primaryStage, EvaluationWindow ew) {
		this.ew = ew;
		this.suspensionSystem = ew.getSession().getCurrentFile().getSuspensionSystem().getCopy();
		Image img = new Image ("/images/hSwmPB8M_400x400.jpg");
		primaryStage.getIcons().add(img);
		this.primaryStage = primaryStage;
		try {
			rootPane = new BorderPane();
			scene = new Scene(rootPane,640,420);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add("application.css");
			createGUI();
			primaryStage.setTitle("Optimization configuration");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
				if (oe != null)
					oe.killThread();
			});
			
		}
		catch (Exception e) {
			System.out.println("Imposible to open Optimization configuration window.");
			e.printStackTrace();
		}
		
	}
	
	private void createGUI() {
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		//Create tab for defining the target curve.
		tcd = new TargetCurveDefinition();
		Tab targetTab = new Tab("Target performance");
		targetTab.setContent(tcd.getGUI());
		
		//Create tab for defining the parameters of the optimization.
		Tab parametersTab = new Tab("Parameters");
		ParameterSelection ps = new ParameterSelection(suspensionSystem.getParameterGroups(), this);
		parametersTab.setContent(ps.getGUI());
		
		
		
		tabPane.getTabs().addAll(targetTab, parametersTab);
		rootPane.setCenter(tabPane);
	}
	
	public void runOptimization () {
		//primaryStage.close();
		/*oe = new OptimizationEngine(suspensionSystem, tcd, ew);
		primaryStage.setScene(new Scene(oe.getUI(), scene.getWidth(), scene.getHeight()));
		primaryStage.getScene().getStylesheets().add("application.css");*/
		
		PruebaOptimizationEngine prueba = new PruebaOptimizationEngine(tcd, suspensionSystem, ew);
		primaryStage.setScene(new Scene(prueba, scene.getWidth(), scene.getHeight()));
		primaryStage.getScene().getStylesheets().add("application.css");
		
		
		
		
	}

}
