package optimization;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class SolutionCellFactory implements Callback<ListView<Solution>, ListCell<Solution>> {

	@Override
	public ListCell<Solution> call(ListView<Solution> lv) {
		
		
		
		return new ListCell<Solution> () {
			
			@Override
			public void updateItem (Solution solution, boolean empty) {
				if (!empty && solution != null) {
					setText(null);
					
					
					Label idLabel = new Label("#" + solution.getID());
					idLabel.setPrefSize(30.0, 30.0);
					idLabel.setBackground(new Background(new BackgroundFill(solution.getColor(), new CornerRadii(15), Insets.EMPTY)));
					idLabel.setAlignment(Pos.CENTER);
					
					Label fitnessLabel = new Label("" + solution.getFitness());
					fitnessLabel.setAlignment(Pos.CENTER);
					
					/*ContextMenu contextMenu = new ContextMenu();
					MenuItem saveMenuItem = new MenuItem("Open in main window");
					contextMenu.getItems().add(saveMenuItem);
					this.setContextMenu(contextMenu);*/
					
					HBox hbox = new HBox();
					hbox.setAlignment(Pos.CENTER_LEFT);
					hbox.getChildren().add(idLabel);
					hbox.getChildren().add(fitnessLabel);
					
					if (lv.getSelectionModel().isSelected(solution.getID() - 1)) {
						this.setBackground(new Background(new BackgroundFill(solution.getColor(), new CornerRadii(0), Insets.EMPTY)));
					}
					else {
						this.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY)));
					}
					
					
					hbox.setOnMouseClicked(new EventHandler <MouseEvent> () {

						@Override
						public void handle(MouseEvent me) {
							if (me.getButton() == MouseButton.PRIMARY) {
								if (!lv.getSelectionModel().isSelected(solution.getID() - 1)) {
									if (me.isControlDown()) {
										lv.getSelectionModel().select(solution.getID() - 1);
									}
									else {
										lv.getSelectionModel().clearAndSelect(solution.getID() - 1);
									}
									
								}
								else {
									if (me.isControlDown()) {
										lv.getSelectionModel().clearSelection(solution.getID() - 1);
									}
									else {
										lv.getSelectionModel().clearSelection();
									}
								}
							}
							
							updateItem(solution, empty);

							
						}
						
					});
					
					setGraphic(hbox);
				}
				else {
					setText(null);
					setGraphic(null);
				}
			}
			
			
		};
	}

}
