package GUI;

import application.SuspensionSystemFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import suspensionSystem.SuspensionSystem;

public class PlotsPane extends AnchorPane {
	private SuspensionSystemFile suspensionSystemFile;
	static public int xVariable = SuspensionSystem.VERTICAL_WHEEL_DISPLACEMENT_VARIABLE, yVariable = SuspensionSystem.VERTICAL_FORCE_ON_WHEEL_VARIABLE;
	
	LineChart<Number, Number> lineChart;
	
	
	public PlotsPane (SuspensionSystemFile suspensionSystemFile) {
		
		this.suspensionSystemFile = suspensionSystemFile;
		
		reload();
		
	}
	
	public void reload () {
		if (suspensionSystemFile != null) {
			NumberAxis xAxis = new NumberAxis();
			NumberAxis yAxis = new NumberAxis();
			
			xAxis.setLabel(SuspensionSystem.symbols[xVariable] + " (" +  SuspensionSystem.units[xVariable] + ")");
			yAxis.setLabel(SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")");
			
			/*ObservableList<String> options = FXCollections.observableArrayList(SuspensionSystem.typeNames);
			ComboBox xAxisCombo = new ComboBox(options);
			ComboBox yAxisCombo = new ComboBox(options);*/
			
			
			lineChart = new LineChart<Number, Number> (xAxis, yAxis);
			lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
			lineChart.setCreateSymbols(false);
			
			
			
			Series<Number, Number> series = new XYChart.Series<>();
			series.setName(suspensionSystemFile.getName());
			
			SuspensionSystem suspensionSystem = suspensionSystemFile.getSuspensionSystem();
			
			int numData = 500;
			for (int i = 0; i <= numData; i++) {
				double x = suspensionSystem.getLowerLimit() + i*(suspensionSystem.getUpperLimit() - suspensionSystem.getLowerLimit())/numData;
				double y = suspensionSystem.getVariableValueAt(yVariable, x);
				x = suspensionSystem.getVariableValueAt(xVariable, x);
				
				series.getData().add(new Data<Number, Number>(x, y));
			}
			lineChart.getData().clear();
			lineChart.getData().add(series);
			
			AnchorPane.setTopAnchor(lineChart, 0.0);
			AnchorPane.setRightAnchor(lineChart, 0.0);
			AnchorPane.setBottomAnchor(lineChart, 0.0);
			AnchorPane.setLeftAnchor(lineChart, 0.0);
			
			//AnchorPane.setTopAnchor(xAxisCombo, 100.0);
			
			this.getChildren().clear();
			this.getChildren().add(lineChart);
		}
	}
	
	public void setSuspensionSystemFile(SuspensionSystemFile suspensionSystemFile) {
		this.suspensionSystemFile = suspensionSystemFile;
		reload();
	}

}
