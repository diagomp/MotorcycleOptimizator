package GUI;

import java.io.File;
import java.util.ArrayList;

import application.SuspensionSystemFile;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import suspensionSystem.CantileverSuspensionSystem;
import suspensionSystem.SuspensionSystem;

public class DialogBox {
	static SuspensionSystemFile suspensionSystemFile;
	
	public static SuspensionSystemFile newSuspensionSystemDialogBox () {
		/*TODO Verificar la información antes de que se pueda aceptar el formulario.
		 * Adaptar el sistema de suspensión al tipo seleccionado en el ComboBox.
		 * Comprobar que no se va a crear un archivo con el mismo nombre que otro ya abierto.*/
		suspensionSystemFile = null;
		
		
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, 350, 150);
		
		
		GridPane form = new GridPane();
		form.setAlignment(Pos.CENTER);
		form.setPadding(new Insets(10, 10, 10, 10));
		form.setVgap(5);
		form.setHgap(5);
		
		Label fileNameLabel = new Label("Name");
		GridPane.setConstraints(fileNameLabel, 0, 0);
		TextField nameTextField = new TextField();
		GridPane.setConstraints(nameTextField, 1, 0);
		
		Label typeLabel = new Label ("Type");
		GridPane.setConstraints(typeLabel, 0, 1);
		ChoiceBox<String> typeChoiceBox = new ChoiceBox<String>();
		typeChoiceBox.setItems(FXCollections.observableArrayList(SuspensionSystem.typeNames));
		GridPane.setConstraints(typeChoiceBox, 1, 1);
		Label errorMessage = new Label(" \n ");
		errorMessage.setTextFill(Color.RED);
		//GridPane.setHalignment(errorMessage, HPos.RIGHT);
		GridPane.setConstraints(errorMessage, 0, 2, 2, 1);

		
		
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER_RIGHT);
		buttons.setSpacing(5);
		Button cancelButton = new Button("Cancel");
		Button okButton = new Button("OK");
		okButton.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				String name = nameTextField.getText();
				String typeName = typeChoiceBox.getValue();
				if (typeName != null && !name.isEmpty()) {
					int type = SuspensionSystem.getTypeIndexOf(typeChoiceBox.getValue());
					suspensionSystemFile = new SuspensionSystemFile(name, type);
					stage.close();
					
				}
				else {
					ArrayList<String> errors = new ArrayList<String>();
					errorMessage.setText("");
					
					if (name.isEmpty())
						errors.add("*Write a valid name.");
					if (typeName == null)
						errors.add("*Select a type.");;
					
					String error = "";
					for (int i = 0; i < errors.size(); i++) {
						error += errors.get(i);
						if (i != errors.size()-1)
							error += '\n';
					}
					errorMessage.setText(error);
				}
			}
		});
		buttons.getChildren().addAll(cancelButton, okButton);
		GridPane.setConstraints(buttons, 1, 3);
		
		form.getChildren().addAll(fileNameLabel, nameTextField, typeLabel, typeChoiceBox, buttons, errorMessage);
		AnchorPane.setTopAnchor(form, 0.0);
		AnchorPane.setRightAnchor(form, 0.0);
		AnchorPane.setLeftAnchor(form, 0.0);
		AnchorPane.setBottomAnchor(form, 0.0);
		
		
		
		
		
		root.getChildren().addAll(form);
		
		
		stage.setScene(scene);
		stage.setTitle("New suspension system");
		stage.showAndWait();
		
		
		return suspensionSystemFile;
	}
	
	
	public static SuspensionSystemFile openSuspensionSystemDialogBox () {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Chooser suspension system file");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Al Files", "*.*"));
		
		File selectedFile = fileChooser.showOpenDialog(new Stage());
		return new SuspensionSystemFile("", 0);
	}

}
