package optimization;

import java.util.ArrayList;
import java.util.Locale.Category;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import GUI.EvaluationWindow;
import application.Session;
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
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import suspensionSystem.SuspensionSystem;
import suspensionSystem.parameter.Parameter;
import suspensionSystem.parameter.ParameterGroup;

public class OptimizationEngine {
	
	private TargetCurveDefinition tcd;
	private SuspensionSystem suspensionSystem;
	private EvaluationWindow ew;
	
	private int xVariable, yVariable;
	
	//Population chart
	private LineChart<Number,Number> lineChart;
	//private NumberAxis xAxis, yAxis;
	
	
	//Fitness chart
	private BarChart<String, Number> fitnessChart;
	private NumberAxis fitnessAxis;
	private CategoryAxis generationAxis;
	private XYChart.Series<String, Number> fitnessSeries;
	

	
	//Fitness mean chart
	private LineChart<String, Number> fitnessMeanChart;
	private NumberAxis fitnessMeanAxis;
	private CategoryAxis generationMeanAxis;
	private XYChart.Series<String, Number> fitnessMeanSeries;
	final private Label generationLabel;
	
	ListView<String> listView;
	
	
	private int generation;
	private double promFitness;
	private double maxFitness;
	
	
	private SuspensionSystem[][] population;
	private double[] populationFitness;
	private int populationSize = 50;
	private int populationHistorySize = 5;
	private int currentGeneration;
	
	private ArrayList<Double> historyMaxFitness;
	private ArrayList<Double> historyMeanFitness;

	
	private ScheduledExecutorService scheduledExecutorService;
	private boolean paused = true;
	
	
	public OptimizationEngine (SuspensionSystem suspensionSystem, TargetCurveDefinition tcd, EvaluationWindow ew) {
		this.ew = ew;
		this.suspensionSystem = suspensionSystem;
		this.tcd = tcd;
		
		
		
		
		xVariable = tcd.getXVariable();
		yVariable = tcd.getYVariable();
		
		
		generation = 0;
		currentGeneration = 0;
		population = new SuspensionSystem[populationHistorySize][populationSize];
		populationFitness = new double[populationSize];
		
		historyMaxFitness = new ArrayList<Double> ();
		historyMeanFitness = new ArrayList<Double> ();
		
		
		generationLabel = new Label("Generation: 0/0");
		
		
		/*SETTING UP POPULATION CHART*/
		NumberAxis xAxis, yAxis;
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		xAxis.setLabel(SuspensionSystem.symbols[xVariable] + " (" +  SuspensionSystem.units[xVariable] + ")");
		yAxis.setLabel(SuspensionSystem.symbols[yVariable] + " (" + SuspensionSystem.units[yVariable] + ")");
		xAxis.setAnimated(false);
		yAxis.setAnimated(false);
		
		lineChart = new LineChart <Number, Number> (xAxis, yAxis);
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.setCreateSymbols(false);
		lineChart.setLegendVisible(false);
		lineChart.setAnimated(false);
		
		
		
		
		/*SETTING UP FITNESS CHART*/
		fitnessAxis = new NumberAxis();
		generationAxis = new CategoryAxis();
		fitnessAxis.setLabel("Fitness");
		generationAxis.setLabel("Generation");
		fitnessAxis.setAnimated(false);
		generationAxis.setAnimated(false);
		
		fitnessChart = new BarChart<String, Number> (generationAxis, fitnessAxis);
		fitnessChart.setAnimated(false);
		fitnessChart.setLegendVisible(false);
		fitnessChart.setHorizontalGridLinesVisible(false);
		fitnessChart.setVerticalGridLinesVisible(false);
		
		
		
		fitnessSeries = new XYChart.Series<String, Number>();
		//fitnessSeries.getData().clear();
		fitnessChart.getData().add(fitnessSeries);
		
		fitnessChart.setBarGap(0);
		fitnessChart.setCategoryGap(0);
		
		
		/*SETTING UP FITNESS MEAN CHART*/
		fitnessMeanAxis = new NumberAxis();
		fitnessMeanAxis.setLabel("Fitness");
		fitnessMeanAxis.setAnimated(false);
		generationMeanAxis = new CategoryAxis();
		generationMeanAxis.setLabel("Generation");
		generationMeanAxis.setAnimated(false);
		
		
		fitnessMeanChart = new LineChart<String, Number> (generationMeanAxis, fitnessMeanAxis);
		fitnessMeanChart.setAnimated(false);;
		fitnessMeanChart.setLegendVisible(false);
		fitnessMeanChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
		fitnessMeanChart.setAxisSortingPolicy(SortingPolicy.NONE);
		
		fitnessMeanSeries = new XYChart.Series<String, Number> ();
		
		fitnessMeanChart.getData().add(fitnessMeanSeries);
		
		fitnessMeanChart.setHorizontalZeroLineVisible(false);		
		fitnessMeanChart.setCreateSymbols(false);
		fitnessMeanChart.setHorizontalGridLinesVisible(false);
		fitnessMeanChart.setVerticalGridLinesVisible(false);
		fitnessMeanAxis.setAutoRanging(false);
		
	}
	
	void updateGenerationLabel () {
		generationLabel.setText("Generation: " + currentGeneration + "/" + generation );
	}
	
	
	
	
	public Pane getUI () {
		
		BorderPane root = new BorderPane();
		AnchorPane doubleGraph = new AnchorPane();
		GridPane grid = new GridPane();
		VBox vbox = new VBox();
		listView = new ListView<String> ();
		
		
		ContextMenu contextMenu = new ContextMenu();
		MenuItem exportSuspensionMenu = new MenuItem("Add to main program.");
		contextMenu.getItems().add(exportSuspensionMenu);
		listView.setContextMenu(contextMenu);
		exportSuspensionMenu.setOnAction(new EventHandler<ActionEvent> () {

			@Override
			public void handle(ActionEvent event) {
				for (int i: listView.getSelectionModel().getSelectedIndices()) {
					loadSuspensionToMainProgram(i);
				}
				
			}
			
		});
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String> () {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				updateGraph();
				System.out.println("Selection changed.");
			}
			
		});
		

		
		AnchorPane.setTopAnchor(fitnessChart, .0);
		AnchorPane.setBottomAnchor(fitnessChart, .0);
		AnchorPane.setRightAnchor(fitnessChart, .0);
		AnchorPane.setLeftAnchor(fitnessChart, .0);
		
		
		AnchorPane.setTopAnchor(fitnessMeanChart, .0);
		AnchorPane.setBottomAnchor(fitnessMeanChart, .0);
		AnchorPane.setRightAnchor(fitnessMeanChart, .0);
		AnchorPane.setLeftAnchor(fitnessMeanChart, .0);
		
		
		doubleGraph.getChildren().addAll(fitnessChart, fitnessMeanChart);
		
		vbox.getChildren().addAll(lineChart, doubleGraph);
		vbox.setPrefHeight(Double.POSITIVE_INFINITY);
		lineChart.prefHeight(Double.POSITIVE_INFINITY);
		
		
		
		
		GridPane.setConstraints(generationLabel, 0, 0, 2, 1);
		GridPane.setHalignment(generationLabel, HPos.CENTER);
		GridPane.setMargin(generationLabel, new Insets(5.0));
		
		Button pauseButton = new Button ("Start");
		GridPane.setConstraints(pauseButton, 0, 1, 2, 1);
		GridPane.setHalignment(pauseButton, HPos.CENTER);
		GridPane.setMargin(pauseButton, new Insets(5.0));
		pauseButton.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
					run();
					pauseButton.setText("Stop");
					paused = false;
				}
				else {
					if (pauseButton.getText() == "Stop") {
						pauseButton.setText("Resume");
						paused = true;
						
						
						
					}
					else {
						pauseButton.setText("Stop");
						paused = false;
						//scheduledExecutorService.notify();
					}
				}
				
				
			}
		});
		
		GridPane.setConstraints(listView, 0, 2, 2, 1);
		GridPane.setHalignment(listView, HPos.CENTER);
		
		
		
		Button previousGenerationButton = new Button("<<");
		GridPane.setConstraints(previousGenerationButton, 0, 3, 1, 1);
		GridPane.setHalignment(previousGenerationButton, HPos.RIGHT);
		GridPane.setMargin(previousGenerationButton, new Insets(5.0));
		previousGenerationButton.setOnAction(new EventHandler<ActionEvent> () {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("<<");
				System.out.println("Generation: " + generation + "\nCurrent generation: " + currentGeneration);
				if ((generation - currentGeneration < populationHistorySize - 1) && (generation - currentGeneration >= 0)) {
					
					currentGeneration--;
					calculateFitness();
					updateGraph();
					//updateFitnessGraph();
					updateList();
					updateGenerationLabel();
					
					/*for (int i = 0; i < populationHistorySize; i++) {
						System.out.println(population[i][0]);
					}*/
					
					if (currentGeneration == 0 || generation - (currentGeneration - 1) > populationHistorySize - 1) {
						previousGenerationButton.setDisable(true);
					}
				}
				
			}
			
		});
		
		Button nextGenerationButton = new Button(">>");
		GridPane.setConstraints(nextGenerationButton, 1, 3, 1, 1);
		GridPane.setHalignment(nextGenerationButton, HPos.LEFT);
		GridPane.setMargin(nextGenerationButton, new Insets(5.0));
		nextGenerationButton.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				System.out.println(">>");
				System.out.println("Generation: " + generation + "\nCurrent generation: " + currentGeneration);
				nextGeneration();
				updateGenerationLabel();
				
				if (previousGenerationButton.isDisable()) {
					previousGenerationButton.setDisable(false);
				}
			}
		});
		
		grid.setPadding(new Insets(10));
		grid.setAlignment(Pos.TOP_CENTER);
		grid.getChildren().addAll(generationLabel, pauseButton, listView, previousGenerationButton, nextGenerationButton);
		
		
		
		root.setCenter(vbox);
		root.setRight(grid);
		
		
		initialize();
		
		return root;
	}
	
	private SuspensionSystem getRandomIndividual () {
		SuspensionSystem newIndividual = suspensionSystem.getCopy();
		
		for (int i = 0; i < suspensionSystem.getParameterGroups().size(); i++) {
			ParameterGroup pg = suspensionSystem.getParameterGroups().get(i);
			ParameterGroup _pg = newIndividual.getParameterGroups().get(i);
			for (int j = 0; j < pg.getChildren().size(); j++) {
				Parameter p = pg.getChildren().get(j);
				Parameter _p = _pg.getChildren().get(j);
				if (p.isToOptimize())
					_p.setValue(p.getLowerLimit() +  Math.random()*(p.getUpperLimit() - p.getLowerLimit()));
				
				
			}
		}
		
		
		
		try {
			newIndividual.recalc();
		}
		catch (Exception e) {
			//e.printStackTrace();
			newIndividual = getRandomIndividual();
			//newIndividual = null;
		}
		
		
		return newIndividual;
	}
	
	public void initialize () {
		//Población aleatoria
		for (int i = 0; i < populationSize; i++)
			population[0][i] = getRandomIndividual();
		
		
		calculateFitness();
		updateGraph();
		updateFitnessGraph();
		updateList();
		fitnessMeanAxis.setUpperBound(fitnessAxis.getUpperBound());
		fitnessMeanAxis.setLowerBound(fitnessAxis.getLowerBound());
	}
	
	void loadSuspensionToMainProgram (int ind) {
		SuspensionSystemFile sFile = new SuspensionSystemFile ("Optimization results: G" + currentGeneration + "I" + ind, population[generation - currentGeneration][ind]);
		//sFile.setSuspensionSystem(suspensionSystem);
		ew.getSession().addFile(sFile);
		Platform.runLater(() -> ew.reload());
	}

	
	public void run () {
		
		//thread.start();
		//Platform.runLater(thread);
		
		
		//Inicializar población con individuos aleatorios
			//initialize();

				scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
				
				scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

					@Override
					public void run() {
						
						if (!paused) {
							
							nextGeneration();
							calculateFitness();
							
							//if (Math.random() < 0.25) {
							/*	Platform.runLater(() -> {
									updateGraph();
									updateFitnessGraph();
									updateList();
									fitnessMeanAxis.setUpperBound(fitnessAxis.getUpperBound());
									fitnessMeanAxis.setLowerBound(fitnessAxis.getLowerBound());
									
								});*/
							//}
						}
						
					}
					
				}, 0, 1000, TimeUnit.MILLISECONDS);
			
				
				
		
		
		
		
	}
	
	
	public void nextGeneration () {
		if (currentGeneration < generation) {
			//Me encuentro en una generación anterior a la última calculada.
			//Avanzo una generación hacia adelante.
			currentGeneration++;
		}
		/*else if (generation < populationHistorySize) {
			//Me encuentro en la última generación, y además aún no he llenado
			//el historial de generaciones.
			generation++;
			currentGeneration = generation;
			updatePopulation();
		}*/
		else {
			//Me encuentro en la última generación pero sí he llenado el historial
			//de generaciones ya.
			generation++;
			currentGeneration = generation;
			
			//Tengo que correr hacia adelante el historial para dejar la posición
			//0 libre.
			for (int i = populationHistorySize - 1; i > 0; i--) {
				for (int j = 0; j < populationSize; j++) {
					population[i][j] = population[i-1][j]/*.getCopy()*/;
				}
				//population[i] = population[i-1];
			}
			
			updatePopulation();
		}
		calculateFitness();
		Platform.runLater(() -> {
			updateGraph();
			updateFitnessGraph();
			updateList();
			updateGenerationLabel();
			fitnessMeanAxis.setUpperBound(fitnessAxis.getUpperBound());
			fitnessMeanAxis.setLowerBound(fitnessAxis.getLowerBound());
			
			
		});
	}
	
	
	public void updatePopulation () {
		
		SuspensionSystem[] offspring = new SuspensionSystem[populationSize];
		
		for (int childIndex = 0; childIndex < populationSize; childIndex += 2) { //De cada pareja salen dos hijos
			SuspensionSystem parent1 = population[generation-currentGeneration+1][selectParent()];	//Me baso en la generación anterior.
			SuspensionSystem parent2 = population[generation-currentGeneration+1][selectParent()];
			
			SuspensionSystem child1 = parent1.getCopy();
			SuspensionSystem child2 = parent1.getCopy();
			
			for (int pIndex = 0; pIndex < parent1.getLastLevelParameters().size(); pIndex++) {
				//Solo modifico los parámetros que no son para optimizar
				if (parent1.getLastLevelParameters().get(pIndex).isToOptimize()) {
					double r = Math.random();
					
					double newValue = parent1.getLastLevelParameters().get(pIndex).getValue()*r + parent2.getLastLevelParameters().get(pIndex).getValue()*(1-r);
					child1.getLastLevelParameters().get(pIndex).setValue(newValue);
					
					newValue = parent1.getLastLevelParameters().get(pIndex).getValue()*(1-r) + parent2.getLastLevelParameters().get(pIndex).getValue()*r;
					child2.getLastLevelParameters().get(pIndex).setValue(newValue);
					
				
					
					//Mutation
					double mutationRate = 0.01;
					r = Math.random();
					if ( r < mutationRate) {
						child1.getLastLevelParameters().get(pIndex).setValue(child1.getLastLevelParameters().get(pIndex).getLowerLimit() + Math.random()*(child1.getLastLevelParameters().get(pIndex).getUpperLimit() - child1.getLastLevelParameters().get(pIndex).getLowerLimit()));
					}
					r = Math.random();
					if ( r < mutationRate) {
						child2.getLastLevelParameters().get(pIndex).setValue(child1.getLastLevelParameters().get(pIndex).getLowerLimit() + Math.random()*(child1.getLastLevelParameters().get(pIndex).getUpperLimit() - child1.getLastLevelParameters().get(pIndex).getLowerLimit()));
					}
				}
			}
			
			offspring[childIndex] = child1;
			offspring[childIndex + 1] = child2;
			
			
		}
		
		//
		for (int i = 0; i < populationSize; i++) {
			population[generation-currentGeneration][i] = offspring[i];
		}
	}
	
	/*TODO: Modificar el cálculo del fitness para que no beneficia a las
	 * soluciones con escaso dominio. Premiar que los límites coincidan
	 * con la curva objetivo.*/
	public void updateGraph () {
		lineChart.getData().clear();
		
		//Target curve
		XYChart.Series<Number,Number> targetCurve = new XYChart.Series<>();
		for (TargetPoint i: tcd.getPoints()) {
			double x = i.getX();
			double y = i.getY();
			targetCurve.getData().add(new Data<Number, Number>(x, y));
		}
		lineChart.getData().add(targetCurve);
		targetCurve.nodeProperty().get().setStyle("-fx-stroke-dash-array: 0.1 5.0;");
		
		
		int numData = 150;
		int selectedInd = -1;
		for (int ind = 0; ind < populationSize; ind++) {
			XYChart.Series<Number,Number> series = new XYChart.Series<>();
			
			if (ind == listView.getSelectionModel().getSelectedIndex()) {
				selectedInd = ind;
				continue;
			}
			
			for (int i = 0; i <= numData; i++) {
				//Obtiene en cada iteración un punto de control
				//dx = (population[ind].getUpperLimit()-population[ind].getLowerLimit())/(numData-1);
				double x = population[generation-currentGeneration][ind].getLowerLimit() + i*(population[generation-currentGeneration][ind].getUpperLimit() - population[generation-currentGeneration][ind].getLowerLimit())/numData; //En términos de la variable básica
				double y = population[generation-currentGeneration][ind].getVariableValueAt(yVariable, x); //Calcula Y respecto de la variable básica
				x = population[generation-currentGeneration][ind].getVariableValueAt(xVariable, x);	//Posteriormente se calcula X en las unidades que se hayan indicado
				
				series.getData().add(new Data<Number, Number>(x, y));
			}
			
			lineChart.getData().add(series);
			//series.nodeProperty().get().setStyle("-fx-stroke-width: 1px;");
			series.nodeProperty().get().setStyle(	"-fx-stroke-width: 1px;" + 
													"-fx-stroke: hsb(" + ((int) ((ind%2==0? ind: populationSize-ind-1)*180/(populationSize - 1))) +  ", 100%, 80%);");
			
			//series.nodeProperty().get().setStyle("-fx-stroke: hsb(" + ((int) (ind*180/(populationSize - 1))) +  ", 100%, 80%);");
		}
		if (selectedInd > -1 ) {
			XYChart.Series<Number,Number> series = new XYChart.Series<>();
			
			for (int i = 0; i <= numData; i++) {
				//Obtiene en cada iteración un punto de control
				//dx = (population[ind].getUpperLimit()-population[ind].getLowerLimit())/(numData-1);
				double x = population[generation-currentGeneration][selectedInd].getLowerLimit() + i*(population[generation-currentGeneration][selectedInd].getUpperLimit() - population[generation-currentGeneration][selectedInd].getLowerLimit())/numData; //En términos de la variable básica
				double y = population[generation-currentGeneration][selectedInd].getVariableValueAt(yVariable, x); //Calcula Y respecto de la variable básica
				x = population[generation-currentGeneration][selectedInd].getVariableValueAt(xVariable, x);	//Posteriormente se calcula X en las unidades que se hayan indicado
				
				series.getData().add(new Data<Number, Number>(x, y));
				
			}
			
			lineChart.getData().add(series);
			series.nodeProperty().get().setStyle("-fx-stroke-width: 3px;");
			//series.nodeProperty().get().setStyle("-fx-stroke: hsb(" + ((int) (selectedInd*180/(populationSize - 1))) +  ", 100%, 80%);");
			series.nodeProperty().get().setStyle(	"-fx-stroke: hsb(0, 0%, 0%);" + 
													"-fx-stroke-width: 3px;");
		}
		
	}
	
	public void calculateFitness () {
		promFitness = 0;
		maxFitness = 0;
		
		
		
		int numData = 150;
		
		double dx = 0;
		double targetDx = tcd.getUpperXValue() - tcd.getLowerXValue();
		targetDx /= numData - 1;
		
			for (int ind = 0; ind < populationSize; ind++) {
				double sentido = 0;		//Sentido en el que van los valores de X en el lazo actual
				double prevX = 0;		//Anterior valor de X
				
				 
					
				ArrayList<Double> fitnesses = new ArrayList<Double>();	//Almacena el valor de adecuación de cada lazo (si hay varios).
				double fitness = 0;
				double maxX = 0, minX = 0;
				
								
				for (int i = 0; i <= numData; i++) {
					//Obtiene en cada iteración un punto de control
					double x = population[generation-currentGeneration][ind].getLowerLimit() + i*(population[generation-currentGeneration][ind].getUpperLimit() - population[generation-currentGeneration][ind].getLowerLimit())/numData; //En términos de la variable básica
					double y = population[generation-currentGeneration][ind].getVariableValueAt(yVariable, x); //Calcula Y respecto de la variable básica
					x = population[generation-currentGeneration][ind].getVariableValueAt(xVariable, x);	//Posteriormente se calcula X en las unidades que se hayan indicado
					
					if (i == 0 || maxX < x)
						maxX = x;
					if (i == 0 || minX > x)
						minX = x;
					
					
						
						
					//Puede que un sistema de suspensión tenga varios lazos. En ese caso, 
					//nos quedaremos únicamente con el lazo que tenga mayor fitness.
					if (i == 0) {
						//fitness.add(new Double());
					}
					else if (i == 1) {
						sentido = x - prevX;
					}
					else {
						if (sentido * (x - prevX) < 0) { //Ha cambiado el sentido
							sentido *= -1;
							fitnesses.add(new Double(fitness)); //Guardamos los resultados del 'bucle' anterior y comenzamos a calcular el nuevo.
							fitness = 0;
							//break;
						}
					}
						
					prevX = x;
					
						
					//Calcular suma fitness del punto
					if (x >= tcd.getLowerXValue() && x <= tcd.getUpperXValue()) {		//Esto solo compara valores dentro del rango X objetivo
						//Sumamos a la variable fitness
						//fitness += 1/Math.pow(y - tcd.getValueAt(x), 2);
						fitness += Math.exp(-Math.pow(y - tcd.getValueAt(x), 2));
						
					}
						
				}
				
				dx = maxX - minX;
				dx /= numData - 1;
				
				//fitness *= dx;
				
				
				fitnesses.add(fitness);
					
				fitness = 0;
				for (Double d: fitnesses) {
					if (d > fitness)
						fitness = d;
				}
				
				dx = (maxX - minX)/(numData - 1);
				dx = dx > targetDx? targetDx: dx;
				
				
				//fitness *= dx/targetDx;
				
				//Ahora la variable fitness tiene el mayor valor de todos los lazos
				
					
					
				populationFitness[ind] = fitness;
				promFitness += fitness;
				if (maxFitness < fitness)
					maxFitness = fitness;
			}
		
			promFitness /= populationSize;
			
			if (currentGeneration > historyMeanFitness.size() - 1) {
				//Es la primera vez que calculamos el fitness de esta población.
				historyMeanFitness.add(promFitness);
				historyMaxFitness.add(maxFitness);
			
				//Ordenar población según fitness
				for (int i = 0; i < populationSize - 1; i++) {
					for (int j = i + 1; j < populationSize; j++) {
						if (populationFitness[i] < populationFitness[j]) {
							SuspensionSystem auxSS = population[generation-currentGeneration][i];
							double auxFitness = populationFitness[i];
							
							populationFitness[i] = populationFitness[j];
							populationFitness[j] = auxFitness;
							
							population[generation-currentGeneration][i] = population[generation-currentGeneration][j];
							population[generation-currentGeneration][j] = auxSS;
						}
					}
				}
			}
		
		
		
	}
	
	public void killThread () {
		if (scheduledExecutorService == null)
			return;
		if (!(scheduledExecutorService.isShutdown() || scheduledExecutorService.isTerminated()))
			scheduledExecutorService.shutdownNow();
	}
	
	private int selectParent () {
		int parentIndex = 0;
		double sumFitness = 0;
		
		for (double f: populationFitness)
			sumFitness += f;
		
		double r = sumFitness*Math.random();
		sumFitness = 0;
		
		for(parentIndex = 0; parentIndex < populationSize; parentIndex++) {
			sumFitness += populationFitness[parentIndex];
			if (r <= sumFitness)
				break;
		}
		
		return parentIndex;
	}
	
	
	/*TODO: Calcular datos promedio y máximo durante el proceso de cálculo
	 * del fitness.*/
	private void updateFitnessGraph () {
		//Corrige posibles faltas de sincronía entre el hilo de la optimización y el de la IU
		if (generation == 0) {
			fitnessSeries.getData().add(new XYChart.Data<String, Number>("" + 0, historyMaxFitness.get(0)));
			fitnessMeanSeries.getData().add(new XYChart.Data<String, Number>("" + 0, historyMeanFitness.get(0)));
		}
		for (int i = fitnessSeries.getData().size(); i <= generation; i++) {
			fitnessSeries.getData().add(new XYChart.Data<String, Number>("" + i, historyMaxFitness.get(i)));
			fitnessMeanSeries.getData().add(new XYChart.Data<String, Number>("" + i, historyMeanFitness.get(i)));
		}
		
		/*for (int i = fitnessSeries.getData().size() - 150; i > 0; i-- ) {
			fitnessSeries.getData().remove(0);
		}*/
		/*while (150 - fitnessSeries.getData().size() < 0)
			fitnessSeries.getData().remove(0);
		
		while (150 - fitnessMeanSeries.getData().size() < 0)
			fitnessMeanSeries.getData().remove(0);*/
		
		
		
		
		//fitnessChart.autosize();
		//Reestablecer límites de los ejes
		/*fitnessMeanAxis.setUpperBound(fitnessAxis.getUpperBound());
		fitnessMeanAxis.setLowerBound(fitnessAxis.getLowerBound());
		fitnessMeanAxis.setTickUnit(fitnessAxis.getTickUnit());	
		fitnessMeanAxis.setMinorTickCount(fitnessAxis.getMinorTickCount());
		fitnessMeanAxis.setMinorTickLength(fitnessAxis.getMinorTickLength());*/
		//fitnessMeanChart.lookup(".axis").setStyle("-fx-tick-label-fill: transparent;");
	}
	
	private void updateList () {
		ObservableList<String> list = FXCollections.observableArrayList();
		for (int i = 0; i < populationSize; i++) {
			list.add("#" + (i+1) + "\t" + populationFitness[i]);
		}
		listView.setItems(list);
	}
	
	
}
