package GUI;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import application.SuspensionSystemFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import suspensionSystem.SuspensionSystem;

public class GraphicPane extends StackPane implements ChangeListener<Number> {
	
	SuspensionSystemFile suspensionSystemFile;
	EvaluationWindow evaluationWindow;
	ResizableCanvas canvas;
	VBox tools;
	Slider slider;
	Label lowerLimit;
	Label upperLimit;
	GridPane data;
	
	DecimalFormat decimalFormatter;
	
	
	private boolean dragging = false;
	
	public GraphicPane (SuspensionSystemFile suspensionSystemFile, EvaluationWindow ew) {
		this.suspensionSystemFile = suspensionSystemFile;
		this.evaluationWindow = ew;
		
		this.widthProperty().addListener(this);
		this.heightProperty().addListener(this);
		
		canvas = new ResizableCanvas();
		this.getChildren().add(canvas);
		
		decimalFormatter = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
		decimalFormatter.applyPattern("#,###.##");
		decimalFormatter.setRoundingMode(RoundingMode.FLOOR);
		
		AnchorPane control = new AnchorPane();
		
		slider = new Slider ();
		
		
		//slider.setShowTickLabels(true);
		lowerLimit = new Label ();
		upperLimit = new Label ();
		
		HBox action = new HBox(lowerLimit, slider, upperLimit);
		action.setAlignment(Pos.BASELINE_CENTER);
		action.setSpacing(5);
		action.setPadding(new Insets(10));
		
		
		Button button = new Button ();
		button.setGraphic(new ImageView(new Image("images/caret-up-solid.png", 15, 15, true, true)));
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add("desplegable");
		
		data = new GridPane();
		data.setAlignment(Pos.CENTER);
		data.setPadding(new Insets(10, 10, 10, 10));
		data.setVgap(5);
		data.setHgap(5);
		
		
		
		/*Label info = new Label ("Some info here");*/
		tools = new VBox();
		tools.getChildren().addAll(action, button);
		tools.setAlignment(Pos.CENTER);
		tools.setSpacing(5);
		tools.setPadding(new Insets(10));
		AnchorPane.setBottomAnchor(tools, 0.0);
		AnchorPane.setLeftAnchor(tools, 0.0);
		AnchorPane.setRightAnchor(tools, 0.0);
		
		button.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent ae) {
				if (tools.getChildren().size() >= 3) {
					tools.getChildren().clear();
					tools.getChildren().addAll(action, button);
					button.setRotate(0);
				}
				else {
					tools.getChildren().clear();
					tools.getChildren().addAll(data, action, button);
					button.setRotate(180);
				}
				
			}
		});
		
		control.getChildren().addAll(tools);
		this.getChildren().add(control);

		
		control.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			double preMouseX = -1;
			double preMouseY = -1;
			@Override
			public void handle(MouseEvent e) {
				if (e.isMiddleButtonDown()) {
					evaluationWindow.setCursor(Cursor.MOVE);
					if (dragging) {
						canvas.translate(e.getX() - preMouseX,- (e.getY() - preMouseY));
					}
					preMouseX = e.getX();
					preMouseY = e.getY();
					dragging = true;
				}
			}
		});
		control.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				dragging = false;
				evaluationWindow.setCursor(Cursor.DEFAULT);
			}
		});
		
		
		reload();
		
	}
	
	public void reload () {
		//slider.set
		suspensionSystemFile.getSuspensionSystem().recalc();
		slider.setMin(suspensionSystemFile.getSuspensionSystem().getLowerLimit());
		slider.setMax(suspensionSystemFile.getSuspensionSystem().getUpperLimit());
		slider.setValue(suspensionSystemFile.getSuspensionSystem().getB());
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				suspensionSystemFile.getSuspensionSystem().setB(slider.getValue());
				reloadData();
				canvas.draw();
				
			}
		});
		
		lowerLimit.setText(decimalFormatter.format(suspensionSystemFile.getSuspensionSystem().getLowerLimit()));
		upperLimit.setText(decimalFormatter.format(suspensionSystemFile.getSuspensionSystem().getUpperLimit()));
		
		reloadData();
		
	}
	
	public void reloadData() {
		data.getChildren().clear();
		for (int i = 0; i < SuspensionSystem.variables.length; i++) {
			Label variableName = new Label (SuspensionSystem.variables[i]);
			GridPane.setConstraints(variableName, 0, i);
			data.getChildren().add(variableName);
			Label variableValue = new Label (decimalFormatter.format(suspensionSystemFile.getSuspensionSystem().getVariableValueAt(i)));
			GridPane.setHalignment(variableValue, HPos.RIGHT);
			GridPane.setConstraints(variableValue, 1, i);
			
			data.getChildren().add(variableValue);
			
		}
	}
	
	
	public void setSuspensionSystemFile (SuspensionSystemFile suspensionSystemFile) {
		this.suspensionSystemFile = suspensionSystemFile;
		canvas.getChildren().clear();
		canvas.getChildren().add(suspensionSystemFile.getSuspensionSystem());
		canvas.draw();
		reload();
	}

	@Override
	public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
		canvas.resize(this.getWidth(), this.getHeight());
		canvas.draw();
		
		
	}

}
