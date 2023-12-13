package optimization;

import java.util.ArrayList;

import GUI.PlotsPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import suspensionSystem.SuspensionSystem;

public class TargetCurveDefinition {

	private int xVariable, yVariable;
	
	private ArrayList<TargetPoint> points;
	private double xLower, xUpper;
	private double yLower, yUpper;
	
	
	
	TargetCurveDefinition () {
		points = new ArrayList<TargetPoint>();
		this.xVariable = PlotsPane.xVariable;
		this.yVariable = PlotsPane.yVariable;
		/*for (int i = 0; i < 50; i++) {
			points.add(new TargetPoint((float)Math.random(), (float)Math.random()));
		}*/
		
		
		points.add(new TargetPoint(0, 18.4f));
		points.add(new TargetPoint(10, 18.3f));
		points.add(new TargetPoint(20, 18.3f));
		points.add(new TargetPoint(30, 18.5f));
		points.add(new TargetPoint(40, 18.75f));
		points.add(new TargetPoint(50, 19.1f));
		points.add(new TargetPoint(60, 19.8f));
		points.add(new TargetPoint(70, 21f));
		points.add(new TargetPoint(80, 23f));
		points.add(new TargetPoint(90, 26f));
		points.add(new TargetPoint(100, 30f));
		/*points.add(new TargetPoint(0, 0));
		points.add(new TargetPoint(10, 175f));
		points.add(new TargetPoint(20, 375f));
		points.add(new TargetPoint(30, 575f));
		points.add(new TargetPoint(40, 725f));
		points.add(new TargetPoint(50, 850f));
		points.add(new TargetPoint(60, 1125f));
		points.add(new TargetPoint(70, 1350f));
		points.add(new TargetPoint(80, 1512.5f));
		points.add(new TargetPoint(90, 1725f));
		points.add(new TargetPoint(100, 2150f));*/
		
		xLower = 0;
		xUpper = 100;
		yLower = 18.3;
		yUpper = 30;
		
	}
	
	public Pane getGUI () {
		//AnchorPane anchor = new AnchorPane();
        
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20.0));
        
        
		
		//Line chart
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel(SuspensionSystem.variables[xVariable] + ", " + SuspensionSystem.symbols[xVariable] + " (" + SuspensionSystem.units[xVariable] + ")");
		xAxis.setAnimated(false);
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel(SuspensionSystem.variables[yVariable] + ", " + SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")");
		yAxis.setAnimated(false);
		LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
		chart.setLegendVisible(false);
		chart.setAnimated(false);
		updateLineChart(chart);
		
		//Data options
		GridPane dataOptionsPane = new GridPane();
		dataOptionsPane.setPrefWidth(250);
		dataOptionsPane.setHgap(5);
		dataOptionsPane.setVgap(5);
		
		ComboBox<String> dataTypeXComboBox = new ComboBox<String>(FXCollections.observableArrayList(SuspensionSystem.variables));
		dataTypeXComboBox.setValue(SuspensionSystem.variables[xVariable]);
		HBox auxHBox1 = new HBox (new Label("X Axis: "), dataTypeXComboBox);
		auxHBox1.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(auxHBox1, HPos.RIGHT);
		dataOptionsPane.add(auxHBox1, 0, 0, 2, 1);
		
		ComboBox<String> dataTypeYComboBox = new ComboBox<String>(FXCollections.observableArrayList(SuspensionSystem.variables));
		dataTypeYComboBox.setValue(SuspensionSystem.variables[yVariable]);
		//dataOptionsPane.add(new Label("Y Axis"), 0, 1, 1, 1);
		HBox auxHBox2 = new HBox (new Label("Y Axis: "), dataTypeYComboBox);
		auxHBox2.setAlignment(Pos.BASELINE_RIGHT);
		GridPane.setHalignment(auxHBox2, HPos.RIGHT);
		dataOptionsPane.add(auxHBox2, 0, 1, 2, 1);
		//GridPane.setHalignment(dataTypeYComboBox, HPos.RIGHT);
		
		TableView<TargetPoint> tableView = new TableView<TargetPoint> ();
		tableView.setEditable(true);
		TableColumn<TargetPoint, String> xColumn = new TableColumn<>(SuspensionSystem.symbols[xVariable] + " (" + SuspensionSystem.units[xVariable] + ")" );
		xColumn.setSortType(TableColumn.SortType.ASCENDING);
		//xColumn.setCellValueFactory(new PropertyValueFactory<>(SuspensionSystem.symbols[xVariable]));
		xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
		xColumn.prefWidthProperty().bind(tableView.widthProperty().divide(2).subtract(0));
		xColumn.setResizable(true);
		TableColumn<TargetPoint, String> yColumn = new TableColumn<>(SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")" );
		yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
		yColumn.prefWidthProperty().bind(tableView.widthProperty().divide(2).subtract(0));
		yColumn.setResizable(true);
		tableView.getColumns().addAll(xColumn, yColumn);
		
		
		dataOptionsPane.add(tableView, 0, 2, 2 , 1);
		
		TableViewSelectionModel<TargetPoint> selectionModel = tableView.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		
		for (TargetPoint i: points) {
			tableView.getItems().add(i);
		}
		
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteControlPoint = new MenuItem("Delete");
		deleteControlPoint.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
			
					
				for (TargetPoint p: tableView.getSelectionModel().getSelectedItems()) {
					points.remove(p);
				}
				updateDataTable(tableView);
				updateLineChart(chart);
				selectionModel.clearSelection();
				
			}
			
		});
		contextMenu.getItems().add(deleteControlPoint);
		tableView.setContextMenu(contextMenu);
		
		
		
		
		
		TextField xTextField = new TextField();
		xTextField.setPromptText(SuspensionSystem.symbols[xVariable] + " (" + SuspensionSystem.units[xVariable] + ")");
		dataOptionsPane.add(xTextField, 0, 3, 1 , 1);
		TextField yTextField = new TextField();
		yTextField.setPromptText(SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")");
		dataOptionsPane.add(yTextField, 1, 3, 1 , 1);
		Button addButton = new Button("Add");
		dataOptionsPane.add(addButton, 1, 4, 1 , 1);
		GridPane.setHalignment(addButton, HPos.RIGHT);
		
		
		//chart.setPrefWidth(10000);
		//dataOptionsPane.setPrefHeight(10000);
		
		//hbox.getChildren().addAll(chart, dataOptionsPane);
		borderPane.setCenter(chart);
		borderPane.setRight(dataOptionsPane);
		
		
		/*Functionality*/
		dataTypeXComboBox.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> ov, String arg1, String newValue) {
				System.out.print("Changed value of combobox -> ");
				/*if (ov.getValue()==curveTypeOptions.get(WHEELRATE_CURVE_TYPE)) {
					System.out.println(ov.getValue());
					curveType = WHEELRATE_CURVE_TYPE;
				}
				else {
					System.out.println(ov.getValue());
					curveType = VERTICALFORCE_CURVE_TYPE;
				}*/
				int i = 0;
				for (i = 0; i < SuspensionSystem.variables.length; i++)
					if (newValue.equals(SuspensionSystem.variables[i])) break;
				
				//PlotsPane.xVariable = i;
				xVariable = i;
				
				//chart.setTitle(curveType==WHEELRATE_CURVE_TYPE? curveTypeOptions.get(WHEELRATE_CURVE_TYPE): curveTypeOptions.get(VERTICALFORCE_CURVE_TYPE));
				xColumn.setText(SuspensionSystem.symbols[xVariable] + " (" + SuspensionSystem.units[xVariable] + ")" );
				xAxis.setLabel(SuspensionSystem.variables[xVariable] + ", " + SuspensionSystem.symbols[xVariable] + " (" + SuspensionSystem.units[xVariable] + ")");
				xTextField.setPromptText(SuspensionSystem.symbols[xVariable] + " (" + SuspensionSystem.units[xVariable] + ")" );
			}
			
		});
		
		dataTypeYComboBox.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> ov, String arg1, String newValue) {
				System.out.print("Changed value of combobox -> ");
				/*if (ov.getValue()==curveTypeOptions.get(WHEELRATE_CURVE_TYPE)) {
					System.out.println(ov.getValue());
					curveType = WHEELRATE_CURVE_TYPE;
				}
				else {
					System.out.println(ov.getValue());
					curveType = VERTICALFORCE_CURVE_TYPE;
				}*/
				int i = 0;
				for (i = 0; i < SuspensionSystem.variables.length; i++)
					if (newValue.equals(SuspensionSystem.variables[i])) break;
				
				//PlotsPane.yVariable = i;
				yVariable = i;
				
				//chart.setTitle(curveType==WHEELRATE_CURVE_TYPE? curveTypeOptions.get(WHEELRATE_CURVE_TYPE): curveTypeOptions.get(VERTICALFORCE_CURVE_TYPE));
				yColumn.setText(SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")" );
				yAxis.setLabel(SuspensionSystem.variables[yVariable] + ", " + SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")");
				yTextField.setPromptText(SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")" );
			}
			
		});
		
		addButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				TargetPoint newPoint = new TargetPoint(Float.parseFloat(xTextField.getText()),Float.parseFloat(yTextField.getText()));
				points.add(newPoint);
				xTextField.setText("");
				yTextField.setText("");
				
				updateDataTable(tableView);
				updateLineChart(chart);
				
				selectionModel.clearSelection();
				selectionModel.select(points.size()-1);
				
				if (newPoint.getX() > xUpper)
					xUpper = newPoint.getX();
				if (newPoint.getX() < xLower)
					xLower = newPoint.getY();
				
				if (newPoint.getY() > yUpper)
					yUpper = newPoint.getY();
				if (newPoint.getY() < yLower)
					yLower = newPoint.getY();
				
				System.out.println("xUpper: " + xUpper + "\nyUpper: " + yUpper);
				
			}
			
		});
		
		
		
		
		
		
		return borderPane;
		//return anchor;
	}
	
	
	private void updateDataTable(TableView<TargetPoint> tableView) {
		tableView.getItems().clear();
		if (points.size() > 0) {
			xUpper = points.get(0).getX();
			xLower = xUpper;
			for (TargetPoint i: points) {
				tableView.getItems().add(i);
				if (i.getX() > xUpper)
					xUpper = i.getX();
				if (i.getX() < xLower)
					xLower = i.getX();
			}
		}
	}
	
	private void updateLineChart(LineChart<Number, Number> chart) {
		chart.getData().clear();
				
		XYChart.Series<Number, Number> serie = new XYChart.Series<>();
		for (TargetPoint i: points) {
			serie.getData().add(new Data<Number, Number>(i.getX(), i.getY()));
		}
		
		chart.getData().add(serie);
	}
	
	
	public int getXVariable () { return xVariable; }
	public int getYVariable () { return yVariable; }
	public double getLowerXValue () { return xLower; }
	public double getUpperXValue () { return xUpper; }
	public double getLowerYValue () { return yLower; }
	public double getUpperYValue () { return yUpper; }
	public ArrayList<TargetPoint> getPoints () { return points; }
	
	public double getValueAt (double _x) {
		double value = 0;
		int i = 0;
		//Suponemos que todos los valores están ordenados.
		for (i = 0; i < points.size() - 1; i++) {
			if (/*points.get(i).getX() >= _x &&*/ _x < points.get(i+1).getX())
				break;
		}
		
		if (i == points.size() - 1) //_x es mayor o igual que el límite superior. Devolvemos la y en el extremo superior.
			return points.get(i).getY();
		
		//Ponderación
		value += points.get(i).getY()*(points.get(i+1).getX()-_x);
		value += points.get(i + 1).getY()*(_x - points.get(i).getX());
		value /= points.get(i+1).getX() - points.get(i).getX();
		
		return value;
	}
}
