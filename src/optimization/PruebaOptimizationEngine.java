package optimization;

import java.text.Format;
import java.util.ArrayList;
import java.util.Locale.Category;

import GUI.EvaluationWindow;
import application.SuspensionSystemFile;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import suspensionSystem.CantileverSuspensionSystem;
import suspensionSystem.SuspensionSystem;

public class PruebaOptimizationEngine extends BorderPane {
	
	private SuspensionSystem ref;
	
	private ArrayList<Solution>[] population;
	private int lastGeneration, currentGeneration;
	private final int HISTORY_SIZE = 6;
	private int populationSize;
	private double mutationRate;
	private TargetCurveDefinition tcd;
	private double maxFitness;
	private boolean runningOptimization;
	
	private ArrayList<Double> historyMaxFitness;
	private ArrayList<Double> historyMeanFitness;
	
	//Variables para la IU
	private final LineChart<Number, Number> currentPopulationChart;
	private final BarChart<String, Number> maxFitnessChart;
	private final XYChart.Series<String, Number> barChartSeries;
	private final LineChart<String, Number> meanFitnessChart;
	private final XYChart.Series<String, Number> lineChartSeries;
	private final Button previousGenerationButton;
	private final ListView<Solution> listView;
	private final Label generationLabelValue;
	private final Label maxFitnessLabelValue;
	


	Thread t;
	
	
	public PruebaOptimizationEngine (TargetCurveDefinition tcd, SuspensionSystem ref, EvaluationWindow ew) {
		super();
		//population = new ArrayList<Solution[]>();	//ArrayList de vectores.
		population = new ArrayList[HISTORY_SIZE];
		for (int i = 0; i < HISTORY_SIZE; i++) {
			population[i] = new ArrayList<Solution>();
		}
		lastGeneration = -1;
		currentGeneration = -1;
		populationSize = 20;
		mutationRate = 0.02;
		maxFitness = 0;
		this.tcd = tcd;
		this.ref = ref;
		
		historyMaxFitness = new ArrayList<Double> ();
		historyMeanFitness = new ArrayList<Double> ();
		
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					nextGeneration();

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (!runningOptimization) {
						t.suspend();
					}
				}
				
			}
		});
		runningOptimization = false;
		t.setDaemon(true);
		
		
		
		//Interfaz de usuario.
		//------Gráfica principal
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel(SuspensionSystem.variables[tcd.getXVariable()] + ", " + SuspensionSystem.units[tcd.getXVariable()]);
		xAxis.setAnimated(false);
		
		NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(SuspensionSystem.variables[tcd.getYVariable()] + ", " + SuspensionSystem.units[tcd.getYVariable()]);
		yAxis.setAnimated(false);
		
		currentPopulationChart = new LineChart<Number, Number>(xAxis, yAxis);
		currentPopulationChart.setTitle("Current generation");
		currentPopulationChart.setAnimated(false);
		currentPopulationChart.setCreateSymbols(false);
		currentPopulationChart.setLegendVisible(false);
		
		//updateMainChart();
		//currentPopulationChart.setPrefHeight(Double.MAX_VALUE);
		
		
		
		//-----Gráfico de barras para mayor fitness
		NumberAxis fitnessAxis = new NumberAxis();
		fitnessAxis.setLabel("Fitness");
		fitnessAxis.setAnimated(false);
		fitnessAxis.setAutoRanging(false);
		
		CategoryAxis generationAxis = new CategoryAxis();
		generationAxis.setLabel("Generation");
		generationAxis.setAnimated(false);
		
		maxFitnessChart = new BarChart<String, Number> (generationAxis, fitnessAxis);
		maxFitnessChart.setTitle("");
		maxFitnessChart.setAnimated(false);
		maxFitnessChart.setLegendSide(Side.BOTTOM);
		maxFitnessChart.setHorizontalGridLinesVisible(false);
		maxFitnessChart.setVerticalGridLinesVisible(false);
		maxFitnessChart.lookup(".chart-legend").setStyle("-fx-translate-x: -50;");
		//maxFitnessChart.setPrefHeight(Double.MAX_VALUE);
		
		barChartSeries = new XYChart.Series<>();
		barChartSeries.setName("Max fitness");
		maxFitnessChart.getData().add(barChartSeries);
		
		
		/*barChartSeries.getData().addAll(	new XYChart.Data<String, Number>("1", 10.0),
											new XYChart.Data<String, Number>("2", 8.0),
											new XYChart.Data<String, Number>("3", 15.0));
		maxFitnessChart.getData().add(barChartSeries);*/
		
		
		//-----Gráfico de líneas para la fitness promedio
		/*fitnessMeanAxis = new NumberAxis();
		fitnessMeanAxis.setLabel("Fitness");
		fitnessMeanAxis.setAnimated(false);
		fitnessMeanAxis.setAutoRanging(false);*/
				
		meanFitnessChart = new LineChart<String, Number> (generationAxis, fitnessAxis);
		meanFitnessChart.setTitle("");
		meanFitnessChart.setAnimated(false);
		meanFitnessChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
		meanFitnessChart.setLegendSide(Side.BOTTOM);
		meanFitnessChart.lookup(".chart-legend").setStyle("-fx-translate-x: 50;");
		
		lineChartSeries = new XYChart.Series<>();
		lineChartSeries.setName("Mean fitness");
		meanFitnessChart.getData().add(lineChartSeries);
		meanFitnessChart.setHorizontalGridLinesVisible(false);
		meanFitnessChart.setVerticalGridLinesVisible(false);
		/*lineChartSeries.getData().addAll(	new XYChart.Data<String, Number>("1", 8.0),
											new XYChart.Data<String, Number>("2", 16.0),
											new XYChart.Data<String, Number>("3", 5.0));
		meanFitnessChart.getData().add(lineChartSeries);*/
		
		
		AnchorPane fitnessChartAnchor = new AnchorPane(maxFitnessChart, meanFitnessChart);
		AnchorPane.setTopAnchor(maxFitnessChart, 0.0);
		AnchorPane.setTopAnchor(meanFitnessChart, 0.0);
		AnchorPane.setRightAnchor(maxFitnessChart, 0.0);
		AnchorPane.setRightAnchor(meanFitnessChart, 0.0);
		AnchorPane.setBottomAnchor(maxFitnessChart, 0.0);
		AnchorPane.setBottomAnchor(meanFitnessChart, 0.0);
		AnchorPane.setLeftAnchor(maxFitnessChart, 0.0);
		AnchorPane.setLeftAnchor(meanFitnessChart, 0.0);
		

		VBox graphsVBox = new VBox();
		graphsVBox.setStyle("-fx-backgroud-color: black;");
		graphsVBox.setAlignment(Pos.CENTER);
		graphsVBox.setPrefHeight(Double.MAX_VALUE);
		graphsVBox.getChildren().addAll(currentPopulationChart, fitnessChartAnchor);
		
	
		this.setCenter(graphsVBox);
		
	
		
		//CONTROL PANEL
		GridPane controlPanel = new GridPane();
		controlPanel.setVgap(5.0);
		controlPanel.setVgap(5.0);
		controlPanel.setPadding(new Insets (10.0));
		
		Label generationLabel = new Label("Generation:");
		GridPane.setConstraints(generationLabel, 0, 0, 2, 1);
		controlPanel.getChildren().add(generationLabel);
		
		generationLabelValue = new Label ("");
		GridPane.setConstraints(generationLabelValue, 2, 0);
		controlPanel.getChildren().add(generationLabelValue);
		
		Label populationSizeLabel = new Label ("Population size: ");
		GridPane.setConstraints(populationSizeLabel, 0, 1, 2, 1);
		controlPanel.getChildren().add(populationSizeLabel);
		
		TextField populationSizeTextField = new TextField("" + populationSize);
		GridPane.setConstraints(populationSizeTextField, 2, 1);
		controlPanel.getChildren().add(populationSizeTextField);
		populationSizeTextField.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					int popSize = Integer.parseInt(populationSizeTextField.getText());
					if (popSize > 0) {
						populationSize = popSize;
						populationSizeTextField.setText("" + popSize);
					}
					else {
						throw(new Exception());
					}
				}
				catch (Exception e) {
					populationSizeTextField.setText("" + populationSize);
				}
				
			}
		});
		
		Label mutationRateLabel = new Label ("Mutation rate:");
		GridPane.setConstraints(mutationRateLabel, 0, 2, 2, 1);
		controlPanel.getChildren().add(mutationRateLabel);
		
		TextField mutationRateTextField = new TextField("" + mutationRate*100 + "%");
		GridPane.setConstraints(mutationRateTextField, 2, 2);
		controlPanel.getChildren().add(mutationRateTextField);
		mutationRateTextField.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent event) {
				try {
					//mutationRateTextField.focus
					
					double mutRate = Double.parseDouble(mutationRateTextField.getText())/100;
					if (mutRate >= 0 && mutRate <= 1) {
						mutationRateTextField.setText(mutRate*100 + " %");
						mutationRate = mutRate;
					}
					else {
						throw(new Exception());
					}
				}
				catch (Exception e) {
					mutationRateTextField.setText("" + mutationRate*100 + " %");
				}
				
			}
		});
		
		
		Label maxFitnessLabel = new Label("Maximul fitness:");
		GridPane.setConstraints(maxFitnessLabel, 0, 3, 2, 1);
		controlPanel.getChildren().add(maxFitnessLabel);
		
		maxFitnessLabelValue = new Label ("0.0");
		GridPane.setConstraints(maxFitnessLabelValue, 2, 3);
		controlPanel.getChildren().add(maxFitnessLabelValue);
		
		
		listView = new ListView<Solution>();
		listView.setCellFactory(new SolutionCellFactory());
		
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listView.setSelectionModel(new MultipleSelectionModel<Solution>() {
			ObservableList<Integer> selections = FXCollections.observableArrayList();
			@Override
			public ObservableList<Integer> getSelectedIndices() {
				return selections;
			}

			@Override
			public ObservableList<Solution> getSelectedItems() {
				ObservableList<Solution> items = FXCollections.observableArrayList();
				//ArrayList<Solution> items = new ArrayList<Solution>();
				for (int i = 0; i < selections.size(); i++) {
					items.add(population[lastGeneration - currentGeneration].get(selections.get(i)));
				}
				return items;
			}

			@Override
			public void selectAll() {
				/*for (Solution s: population[lastGeneration - currentGeneration]) {
					s.select(true);
				}*/
				selections.clear();
				for (int i = 0; i < population[lastGeneration - currentGeneration].size(); i++) {
					selections.add(i);
				}
				updateMainChart();
			}

			@Override
			public void selectFirst() {
				selections.clear();
				selections.add(0);
				updateMainChart();
			}

			@Override
			public void selectIndices(int index, int... indices) {
				selections.clear();
				updateMainChart();
			}

			@Override
			public void selectLast() {
				selections.clear();
				selections.add(population[lastGeneration - currentGeneration].size() - 1);
				updateMainChart();
			}

			@Override
			public void clearAndSelect(int index) {
				selections.clear();
				selections.add(index);
				updateMainChart();
			}

			@Override
			public void clearSelection() {
				selections.clear();
				updateMainChart();
			}

			@Override
			public void clearSelection(int index) {
				System.out.println("Selection found: removing...");
				for (int i = 0; i < selections.size(); i++) {
					if (((int)selections.get(i)) == index) {
						
						selections.remove(i);
						break;
					}
				}
				updateMainChart();
			}

			@Override
			public boolean isEmpty() {
				return selections.isEmpty();
			}

			@Override
			public boolean isSelected(int index) {
				for (int i = 0; i < selections.size(); i++) {
					if (selections.get(i) == index) {
						return true;
					}
				}
				return false;
			}
			@Override
			public void select(int index) {
				//population[lastGeneration - currentGeneration].get(index).select(true);
				selections.add(index);
				updateMainChart();
			}
			@Override
			public void select(Solution obj) {
				for (int i = 0; i < population[lastGeneration - currentGeneration].size(); i++) {
					if (population[lastGeneration - currentGeneration].get(i) == obj) {
						selections.add(i);
						break;
					}
				}
				updateMainChart();	
			}
			@Override
			public void selectNext() {
				// TODO Auto-generated method stub
			}
			@Override
			public void selectPrevious() {
				// TODO Auto-generated method stub
			}
			
		});
		
		ContextMenu contextMenu = new ContextMenu ();
		MenuItem loadInEWMenuItem = new MenuItem("Load to main window.");
		loadInEWMenuItem.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				for (int i = 0; i < listView.getSelectionModel().getSelectedIndices().size(); i++) {
					Solution s = population[lastGeneration - currentGeneration].get(listView.getSelectionModel().getSelectedIndices().get(i));
					ew.getSession().addFile(new SuspensionSystemFile("Optimization results: G" + currentGeneration + "I" + s.getID() + " F=" + String.format("%.2f", s.getFitness()), s.getSuspensionSystem()));
					ew.reload();
					listView.getSelectionModel().clearSelection();
				}
				
			}
		});
		
		contextMenu.getItems().add(loadInEWMenuItem);
		listView.setContextMenu(contextMenu);
		

		GridPane.setConstraints(listView, 0, 4, 3, 1);
		controlPanel.getChildren().add(listView);
		
		previousGenerationButton = new Button ("<<");
		GridPane.setConstraints(previousGenerationButton, 0, 5);
		previousGenerationButton.setAlignment(Pos.CENTER_LEFT);
		controlPanel.getChildren().add(previousGenerationButton);
		previousGenerationButton.setDisable(true);
		previousGenerationButton.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				previousGeneration();
				
			}
		});
		
		Button playButton = new Button("Start");
		GridPane.setConstraints(playButton, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, Insets.EMPTY);
		playButton.setAlignment(Pos.CENTER);
		controlPanel.getChildren().add(playButton);
		playButton.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				if (t.isAlive()) {
					if (runningOptimization) {
						//t.suspend();
						runningOptimization = false;
						playButton.setText("Start");
					}
					else {
						t.resume();
						runningOptimization = true;
						playButton.setText("Stop");
					}
				}
				else {
					playButton.setText("Stop");
					t.start();
					runningOptimization = true;
				}
				
			}
		});
		
		Button nextGenerationButton = new Button (">>");
		GridPane.setConstraints(nextGenerationButton, 2, 5);
		nextGenerationButton.setAlignment(Pos.CENTER_RIGHT);
		controlPanel.getChildren().add(nextGenerationButton);
		nextGenerationButton.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				nextGeneration();
			}
		});
		
		this.setRight(controlPanel);
		
	}
	
	private void nextGeneration () {
		
		if (lastGeneration < 0) {
			lastGeneration = 0;
			currentGeneration = 0;
			
			populateRandomly();
			calculateFitness();
			sortPopulation();
			fillPopulationData();
		}
		else if (lastGeneration == currentGeneration) {
			lastGeneration++;
			currentGeneration = lastGeneration;
			
			for (int i = HISTORY_SIZE-1; i > 0; i--) {
				population[i] = population[i-1];
			}
			loadNewGeneration();
			//populateRandomly();
			calculateFitness();
			sortPopulation();
			fillPopulationData();
		}
		else {
			currentGeneration++;
		}
		Platform.runLater(() -> {
			updateUI();
			previousGenerationButton.setDisable(false);
		});
		
		
	}
	
	private void previousGeneration () {
		
		//if (lastGeneration - currentGeneration + 1 < HISTORY_SIZE) {
			currentGeneration--;
			
			Platform.runLater(() -> {
				if (lastGeneration - (currentGeneration - 1) > HISTORY_SIZE - 1 || currentGeneration - 1 < 0)
					previousGenerationButton.setDisable(true);
				updateUI();
			});
		//}
		
	}
	
	public void updateUI () {
		generationLabelValue.setText(currentGeneration + "/" + lastGeneration);
		maxFitnessLabelValue.setText(String.format("%.4f", maxFitness));
		updateMainChart();
		updateFitnessChart();
		listView.getSelectionModel().clearSelection();
		listView.getItems().clear();
		//listView.
		listView.getItems().addAll(population[lastGeneration - currentGeneration]);
	}
	
	public void updateMainChart () {
		//if (currentPopulationChart.getData() != null)
			currentPopulationChart.getData().clear();
		
		//if (population != null) {
			//System.out.println(currentGeneration + "/" + lastGeneration);
			//System.out.println(population.get(0));
			for (int ind = population[lastGeneration - currentGeneration].size() - 1; ind >= 0; ind--) {
				//System.out.println("Comenzamos a iterar las soluciones no seleccionadas.");
				
				if (!listView.getSelectionModel().isSelected(ind)) {
					Solution s = population[lastGeneration - currentGeneration].get(ind);
					
					if (s.getSuspensionSystem() == null)
						break;
					
					XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
					for (int i = 0; i < 150; i++) {
						double x = s.getSuspensionSystem().getLowerLimit() + i*(s.getSuspensionSystem().getUpperLimit() - s.getSuspensionSystem().getLowerLimit())/150;
						double y = s.getSuspensionSystem().getVariableValueAt(tcd.getYVariable(), x);
						x = s.getSuspensionSystem().getVariableValueAt(tcd.getXVariable(), x);
						
						series.getData().add(new XYChart.Data<Number, Number>(x, y));
					}
					
					currentPopulationChart.getData().add(series);
					if (s.getClass() != null) {
						series.nodeProperty().get().setStyle("-fx-stroke: rgb(" + 
								(int) 256*s.getColor().getRed() + ", " + 
								(int) 256*s.getColor().getGreen() + ", " + 
								(int) 256*s.getColor().getBlue() + ");");
					}
				}
			}
			
			double maxX = 0, minX = 0;
			double maxY = 0, minY = 0;
			
			//Graficando la curva objetivo
			XYChart.Series<Number, Number> targetCurve = new XYChart.Series<Number, Number> ();
			for (TargetPoint p: tcd.getPoints()) {
				double x = p.getX();
				double y = p.getY();
				targetCurve.getData().add(new XYChart.Data<Number, Number>(x, y));
				
				if (x > maxX)
					maxX = x;
				if (x < minX)
					minX = x;
				if (y > maxY)
					maxY = y;
				if (y < minY)
					minY = y;
			}
			
			currentPopulationChart.getData().add(targetCurve);
			targetCurve.nodeProperty().get().setStyle("-fx-stroke: rgb(" + "0, 74, 124);");
			//System.out.println("Target curve added to chart.");
			
			//Graficando después las curvas de aquellas soluciones que estén seleccionadas.
			for (Solution s: listView.getSelectionModel().getSelectedItems()) {
				if (s.getSuspensionSystem() == null)
					break;
				XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
				for (int i = 0; i < 150; i++) {
					double x = s.getSuspensionSystem().getLowerLimit() + i*(s.getSuspensionSystem().getUpperLimit() - s.getSuspensionSystem().getLowerLimit())/150;
					double y = s.getSuspensionSystem().getVariableValueAt(tcd.getYVariable(), x);
					x = s.getSuspensionSystem().getVariableValueAt(tcd.getXVariable(), x);
					series.getData().add(new XYChart.Data<Number, Number>(x, y));
					
					if (x > maxX)
						maxX = x;
					if (x < minX)
						minX = x;
					if (y > maxY)
						maxY = y;
					if (y < minY)
						minY = y;
				}
				
				
				currentPopulationChart.getData().add(series);
				
				if (s.getClass() != null) {
					series.nodeProperty().get().setStyle("-fx-stroke: rgb(" + "0, 0, 0);");
							/*(int) 256*s.getColor().getRed() + ", " + 
							(int) 256*s.getColor().getGreen() + ", " + 
							(int) 256*s.getColor().getBlue() + ");");*/
				}
			}
			
			NumberAxis xAxis = (NumberAxis) currentPopulationChart.getXAxis();
			NumberAxis yAxis = (NumberAxis) currentPopulationChart.getYAxis();
			if (listView.getSelectionModel().getSelectedIndices().size() > 0) {
				//NumberAxis xAxis = (NumberAxis) currentPopulationChart.getXAxis();
				//currentPopulationChart.setAnimated(true);
				//xAxis.setAnimated(true);
				xAxis.setAutoRanging(false);
				xAxis.setUpperBound(maxX + 0.15*(maxX - minX));
				xAxis.setLowerBound(minX - 0.15*(maxX - minX));
				
				//yAxis.setAnimated(true);
				yAxis.setAutoRanging(false);
				yAxis.setUpperBound(maxY + 0.15*(maxY - minY)/10);
				yAxis.setLowerBound(minY - 0.15*(maxY - minY)/10);
				
			}
			else {
				//currentPopulationChart.setAnimated(false);
				xAxis.setAutoRanging(true);
				//xAxis.setAnimated(false);
				yAxis.setAutoRanging(true);
				//yAxis.setAnimated(false);
			}
			
			
		//}
	}
	
	
	public void populateRandomly () {
		population[lastGeneration - currentGeneration] = new ArrayList<Solution>();
		
		for (int i = 0; i < populationSize; i++) {
			population[lastGeneration - currentGeneration].add(new Solution(ref, /*i == 0? false:*/ true));
			
			Color c = Color.hsb(((int) (/*(i%2==0?*/ i/*: populationSize-i-1*/)*180/(populationSize - 1)), 0.5, i%2==0? 1.0: 0.5);
			population[lastGeneration - currentGeneration].get(i).setColor(c);
		}
		System.out.println(population[0].get(0));
	}
	
	public void loadNewGeneration () {
		population[lastGeneration - currentGeneration] = new ArrayList<Solution>();
		
		int i = 0;
		/*int index = 0;
		double f = 0;
		//Calculamos la solución con mayor fitness.
		for (i = 0; i <  population[lastGeneration - currentGeneration + 1].size(); i++) {
			if (f < population[lastGeneration - currentGeneration + 1].get(i).getFitness()) {
				f = population[lastGeneration - currentGeneration + 1].get(i).getFitness();
				index = i;
			}
		}*/
		
		//Siempre añadimos a la nueva generación la mejor de las soluciones anteriores - Elitism
		population[lastGeneration - currentGeneration].add(population[lastGeneration - currentGeneration + 1].get(0));
		population[lastGeneration - currentGeneration].add(population[lastGeneration - currentGeneration + 1].get(1));
		
		
		for (i = 0; i < populationSize - 2; i++) {
			/*Solution newIndividual = new Solution(mutationRate, 
					population[lastGeneration - currentGeneration + 1].get(selectParent()),
					population[lastGeneration - currentGeneration + 1].get(selectParent()));
			/*population[lastGeneration - currentGeneration].add(new Solution(mutationRate, 
					population[lastGeneration - currentGeneration + 1].get(selectParent()),
					population[lastGeneration - currentGeneration + 1].get(selectParent())));*/
				
			Solution newIndividual;
			do {
				double r = Math.random();
				if (r > mutationRate) {
					newIndividual = new Solution(mutationRate, 
							population[lastGeneration - currentGeneration + 1].get(selectParent()),
							population[lastGeneration - currentGeneration + 1].get(selectParent()));
					}
				else {
					//Parte de la población se crea al azar
					newIndividual = new Solution(this.ref, true);
				}
			}
			while (newIndividual.getSuspensionSystem() == null);
			
			/*Color c = Color.hsb(((int) ((i%2==0? i: populationSize-i-1)*200/(populationSize - 1))), 0.5, (i%3)*(0.3/3) + 0.7);
			//population[lastGeneration - currentGeneration].get(i).setColor(c);
			newIndividual.setColor(c);
			newIndividual.setID(i + 1);*/
			population[lastGeneration - currentGeneration].add(newIndividual);
			
		}
		
	}
	
	private int selectParent () {
		int parentIndex = 0;
		double sumFitness = 0;
		double promFitness = 0;
		
		
		for (Solution s: population[lastGeneration - currentGeneration + 1])
			sumFitness += s.getFitness();
		
		double r = 2*sumFitness*Math.random();
		promFitness = sumFitness/population[lastGeneration - currentGeneration + 1].size();
		sumFitness = 0;
		
		for(parentIndex = 0; parentIndex < population[lastGeneration - currentGeneration + 1].size() - 1; parentIndex++) {
			sumFitness += population[lastGeneration - currentGeneration + 1].get(parentIndex).getFitness() + promFitness;
			if (r <= sumFitness)
				break;
		}
		System.out.println("Parent index: " + parentIndex);
		return parentIndex;
	}
	
	private void sortPopulation () {
		ArrayList<Solution> currentPop = population[lastGeneration - currentGeneration];
		for (int i = 0; i < currentPop.size() - 1; i++) {
			for (int j = i + 1; j < currentPop.size(); j++) {
				if (currentPop.get(i).getFitness() < currentPop.get(j).getFitness()) {
					Solution aux = currentPop.get(i);
					currentPop.set(i, currentPop.get(j));
					currentPop.set(j, aux);
				}
			}
		}
	}
	
	public void updateFitnessChart () {
		barChartSeries.getData().clear();
		lineChartSeries.getData().clear();
		
		NumberAxis na = (NumberAxis) meanFitnessChart.getYAxis();
		na.setUpperBound(maxFitness);
		int historyLength = historyMaxFitness.size();
		for (int i = historyLength > 50? historyLength - 50: 0; i < historyLength; i++) {
			barChartSeries.getData().add(new XYChart.Data<String, Number>("" + i, historyMaxFitness.get(i)));
			lineChartSeries.getData().add(new XYChart.Data<String, Number>("" + i, historyMeanFitness.get(i)));
		}
	}
	
	public void calculateFitness () {
		double mean = 0, max = 0;
		for (Solution s: population[lastGeneration - currentGeneration]) {
			s.calculateFitness(tcd);
			if (max < s.getFitness())
				max = s.getFitness();
			mean += s.getFitness();
		}
		mean /= population[lastGeneration - currentGeneration].size();
		
		historyMaxFitness.add(max);
		historyMeanFitness.add(mean);
		/*barChartSeries.getData().add(new XYChart.Data<String, Number> ("" + currentGeneration, max));
		lineChartSeries.getData().add(new XYChart.Data<String, Number> ("" + currentGeneration, mean));*/
		
		/*NumberAxis na1 = (NumberAxis) meanFitnessChart.getYAxis();
		NumberAxis na2 = (NumberAxis) maxFitnessChart.getYAxis();*/
		
		if (max > maxFitness)
			maxFitness = max;
		
		//na1.setUpperBound(maxFitness);
		//na2.setUpperBound(maxFitness);
		
		
		/*maxFitness.add(max);
		meanFitness.add(mean);*/
	}
	
	public void fillPopulationData () {
		ArrayList<Solution> pop = population[lastGeneration - currentGeneration];
		for (int i = 0; i < pop.size(); i++) {
			pop.get(i).setID(i+1);
			//Color c = Color.hsb(((int) ((i%2==0? i: (populationSize-i-1)*200/(populationSize - 1))), 0.5, (i%3)*(0.3/3) + 0.7);
			Color c = Color.hsb(((int) (i*270/(populationSize - 1))), 0.5, 1.0);
			pop.get(i).setColor(c);
		}
	}
}
