package GUI;

import application.Session;
import application.SuspensionSystemFile;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import suspensionSystem.SuspensionSystem;

public class DescriptionPane extends AnchorPane {

	private SuspensionSystemFile suspensionSystemFile;
	
	public DescriptionPane(SuspensionSystemFile suspensionSystemFile) {
		this.suspensionSystemFile = suspensionSystemFile;
		
		reload();
		
		
		
		
		
	}
	
	public void reload () {
		if (suspensionSystemFile != null) {
			this.getChildren().clear();
			GridPane gridPane = new GridPane();
			gridPane.setPadding(new Insets(10));
			gridPane.setHgap(5);
			gridPane.setVgap(5);
			
			Label name = new Label ();
			name.setText(suspensionSystemFile.getName());
			//name.setWrapText(true);
			name.setFont(new Font(14));
			GridPane.setConstraints(name, 0, 0, 2, 1);
			
			Label typeLabel = new Label("Type:");
			GridPane.setConstraints(typeLabel, 0, 1);
			
			Label type = new Label(SuspensionSystem.typeNames[suspensionSystemFile.getType()]);
			type.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, 12));
			GridPane.setConstraints(type,  1, 1);
			
			gridPane.getChildren().addAll(name, typeLabel, type);
			this.getChildren().add(gridPane);
		}
	}

	public void setSuspensionSystemFile(SuspensionSystemFile suspensionSystemFile) {
		this.suspensionSystemFile = suspensionSystemFile;
		reload();
		
	}
	
}
