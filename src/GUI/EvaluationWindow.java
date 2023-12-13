package GUI;

import application.Session;
import application.SuspensionSystemFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EvaluationWindow {

	Session session;
	Stage stage;
	Scene scene;
	
	EvaluationMenu evMenu;
	DescriptionPane descriptionPane;
	ParametersAccordionPane parametersPane;
	PlotsPane plotsPane;
	GraphicPane graphicPane;
	
	public EvaluationWindow(Session session, Stage stage) {
		this.stage = stage;
		this.session = session;
		
		
		stage.setTitle("Sevilla Racing");
		BorderPane root = new BorderPane();
		scene = new Scene(root, session.getWidth(), session.getHeight());
		
		
		
		/*MENU*/
		evMenu = new EvaluationMenu(session, this);
		root.setTop(evMenu);
		
		/*SPLITPANE*/
		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(Orientation.HORIZONTAL);
		
		descriptionPane = new DescriptionPane(session.getCurrentFile());
		parametersPane = new ParametersAccordionPane(session.getCurrentFile(), this);
		
		TabPane tabPane = new TabPane ();
		tabPane.setSide(Side.TOP);
		Tab graphicTab = new Tab("Scheme");
		graphicPane = new GraphicPane(session.getCurrentFile(), this);
		graphicTab.setContent(graphicPane);
		
		Tab plotTab = new Tab("Plots");
		plotsPane = new PlotsPane(session.getCurrentFile());
		plotTab.setContent(plotsPane);
		
		tabPane.getTabs().addAll(graphicTab, plotTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		
		splitPane.getItems().addAll(new VBox(descriptionPane, parametersPane), tabPane);
		splitPane.setDividerPosition(0, .25);
		//splitPane.getDividers().get(0);
		root.setCenter(splitPane);
		
		
		
		reload();
		
		stage.setScene(scene);
		stage.show();
			
		
	}
	
	public void reload () {
		
		evMenu.reload();
		descriptionPane.setSuspensionSystemFile(session.getCurrentFile());
		parametersPane.setSuspensionSystemFile(session.getCurrentFile());
		reloadGraphics();
		
		
		
	}
	
	public void reloadGraphics () {
		graphicPane.setSuspensionSystemFile(session.getCurrentFile());
		plotsPane.setSuspensionSystemFile(session.getCurrentFile());
		
	}
	
	public void reloadPlots () {
		plotsPane.reload();
		//plotsPane.setSuspensionSystemFile(session.getCurrentFile());
		
	}
	
	public Scene getScene() {
		return scene;
	}

	
	public void setCursor(Cursor cursorType) {
		scene.setCursor(cursorType);
	}
	
	public Session getSession () { return this.session; }
}
