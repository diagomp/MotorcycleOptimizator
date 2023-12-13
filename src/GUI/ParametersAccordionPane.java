package GUI;

import java.util.ArrayList;
import java.util.Collection;

import application.SuspensionSystemFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import suspensionSystem.SuspensionSystem;
import suspensionSystem.parameter.Parameter;
import suspensionSystem.parameter.ParameterGroup;

public class ParametersAccordionPane extends AnchorPane {
	
	SuspensionSystemFile suspensionSystemFile;
	EvaluationWindow evaluationWindow;
	
	
	private int i, j;
	private ArrayList<ParameterGroup> pg;
	private ArrayList<Parameter> params;
	
	public ParametersAccordionPane (SuspensionSystemFile suspensionSystemFile, EvaluationWindow ew) {
		super();
		this.suspensionSystemFile = suspensionSystemFile;
		this.evaluationWindow = ew;
		reload();
	}
	
	
	public void reload () {
		this.getChildren().clear();
		if (suspensionSystemFile != null && suspensionSystemFile.getSuspensionSystem() != null) {
			
			//Accordion accordion = new Accordion();
			VBox accordion = new VBox();
			
			ArrayList<TitledPane> titledPanes = new ArrayList<TitledPane>();
			//pg = suspensionSystemFile.getSuspensionSystem().getParameterGroups();
			//for (i = 0; i < pg.size(); i++) {
			for (ParameterGroup i: suspensionSystemFile.getSuspensionSystem().getParameterGroups()) {
				GridPane gridPane = new GridPane();
				gridPane.setAlignment(Pos.BASELINE_CENTER);
				gridPane.setPadding(new Insets(10, 10, 10, 10));
				gridPane.setVgap(5);
				gridPane.setHgap(5);
				
				int index = 0;
				//params = pg.get(i).getChildren();
				//for (j = 0; j < params.size(); j++) {
				for (Parameter j: i.getChildren()) {
					Label label = new Label(j.getName());
					GridPane.setConstraints(label, 0, index);
					
					TextField textField = new TextField();
					textField.setText("" + j.getValue());
					textField.setOnAction(new EventHandler<ActionEvent> () {

						@Override
						public void handle(ActionEvent event) {
							// TODO Auto-generated method stub
							suspensionSystemFile.modify();
							double value = Double.NaN;
							try {
								//Actualizar historial
								value = Double.parseDouble(textField.getText());
								System.out.println("Changing value of " + j.getName() /*+ " from " + oldValue*/ + " to " + value);
								
								//params.get(j).setValue(value);
								//suspensionSystemFile.getSuspensionSystem().getParameterGroups().get(i).getChildren().get(j).setValue(value);
								//System.out.println(j.getValue());
								evaluationWindow.reloadGraphics();
							}
							catch (Exception e) {
								//System.out.println(e.printStackTrace());
								e.printStackTrace();
							}
							
							if (!Double.isNaN(value))
								j.setValue(value);
							
							evaluationWindow.reloadGraphics();
						}
						
					});
					/*textField.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
							// TODO Auto-generated method stub
							try {
								suspensionSystemFile.modify();
								double value = Double.parseDouble(newValue);
								System.out.println("Changing value of " + j.getName() + " from " + oldValue + " to " + value);
								j.setValue(value);
								//params.get(j).setValue(value);
								//suspensionSystemFile.getSuspensionSystem().getParameterGroups().get(i).getChildren().get(j).setValue(value);
								//System.out.println(j.getValue());
								evaluationWindow.reloadGraphics();
							}
							catch (Exception e) {
								//System.out.println(e.printStackTrace());
								e.printStackTrace();
							}
						}
					});*/
					GridPane.setConstraints(textField, 1, index);
					
					gridPane.getChildren().addAll(label, textField);
					
					index++;
					
				}
				titledPanes.add(new TitledPane(i.getName(), gridPane));
			}
			
			
			//accordion.getPanes().addAll(titledPanes);
			accordion.getChildren().addAll(titledPanes);
			ScrollPane scroll = new ScrollPane();
			//scroll.setBorder(new Border());
			//scroll.setBorder(new Border(new BorderStroke()));
			scroll.setContent(accordion);
			AnchorPane.setTopAnchor(scroll, 0.0);
			AnchorPane.setRightAnchor(scroll, 0.0);
			AnchorPane.setBottomAnchor(scroll, 0.0);
			AnchorPane.setLeftAnchor(scroll, 0.0);
			this.getChildren().addAll(scroll);
		}
		else {
			Label label = new Label("Open a file to edit parameters here.");
			label.setWrapText(true);
			label.setAlignment(Pos.CENTER);
			label.setTextAlignment(TextAlignment.CENTER);
			AnchorPane.setTopAnchor(label, 0.0);
			AnchorPane.setRightAnchor(label, 0.0);
			AnchorPane.setBottomAnchor(label, 0.0);
			AnchorPane.setLeftAnchor(label, 0.0);
			this.getChildren().add(label);
		}
	}
	
	public void setSuspensionSystemFile(SuspensionSystemFile suspensionSystemFile) {
		this.suspensionSystemFile = suspensionSystemFile;
		reload();
	}

}
