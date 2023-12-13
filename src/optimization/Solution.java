package optimization;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import suspensionSystem.SuspensionSystem;
import suspensionSystem.parameter.Parameter;

public class Solution {
	private SuspensionSystem suspensionSystem;
	//private SuspensionSystem twinSuspensionSystem;
	
	private double fitness;
	
	private Color color;
	private int id;
	
	private int tries;
	
	
	public Solution (SuspensionSystem ref, boolean randomReseting) {
		//this.suspensionSystem = suspensionSystem;
		tries = 0;
		id = 0;
		//this.suspensionSystem = SuspensionSystem.getSuspensionSystemOfType(ref.getType());
		this.suspensionSystem = ref.getCopy();
		if (randomReseting)
			setRandomParams(ref);
		
		fitness = -1;
	}
	
	public Solution (SuspensionSystem suspensionSystem) {
		this(suspensionSystem, true);
	}
	
	public Solution (double mutationRate, SuspensionSystem parent1, SuspensionSystem parent2) {
		this.suspensionSystem = parent1.getCopy();
		
		for (int pIndex = 0; pIndex < parent1.getLastLevelParameters().size(); pIndex++) {
			if (parent1.getLastLevelParameters().get(pIndex).isToOptimize()) {
				//Blend crossover
				double r = 2.0*Math.random() - 0.5;
				double newValue = parent1.getLastLevelParameters().get(pIndex).getValue() + (parent2.getLastLevelParameters().get(pIndex).getValue() - parent1.getLastLevelParameters().get(pIndex).getValue())*r;
				
				this.suspensionSystem.getLastLevelParameters().get(pIndex).setValue(newValue);
				
				r = Math.random();
				if (r < mutationRate)
					this.suspensionSystem.getLastLevelParameters().get(pIndex).setValue(this.suspensionSystem.getLastLevelParameters().get(pIndex).getLowerLimit() + Math.random()*(this.suspensionSystem.getLastLevelParameters().get(pIndex).getUpperLimit() - this.suspensionSystem.getLastLevelParameters().get(pIndex).getLowerLimit()));
				
			}
		}
		try {
			suspensionSystem.recalc();
		}
		catch (Exception e) {
			suspensionSystem = null;
		}
	}
	
	public Solution (double mutationRate, Solution sol1, Solution sol2) {
		this(mutationRate, sol1.getSuspensionSystem(), sol2.getSuspensionSystem());
	}
	
	//Inicializa todos los parámetros del sistema en un número aleatorio entre los
	//rangos establecidos según un sistema de suspensión de referencia.
	public void setRandomParams (SuspensionSystem ref) {
		for (int i = 0; i < ref.getLastLevelParameters().size(); i++) {
			Parameter refParam = ref.getLastLevelParameters().get(i);
			Parameter p = suspensionSystem.getLastLevelParameters().get(i);
			
			if (p.isToOptimize())
				p.setValue(refParam.getLowerLimit() + Math.random()*(refParam.getUpperLimit() - refParam.getLowerLimit()));
			p.setLowerLimit(ref.getLowerLimit());
			p.setUpperLimit(ref.getUpperLimit());
		}
		
		try {
			if (tries < 50)
				this.suspensionSystem.recalc();
		}
		catch (Exception e) {
			tries++;
			setRandomParams (ref);
		}
	}
	
	public void calculateFitness (TargetCurveDefinition tcd) {
		int numData = 150;
		double sentido = 0;
		double prevX = 0;
		
		
		ArrayList<Double> fitnesses = new ArrayList<Double>();
		double fitness = 0;
		double maxX = 0, minX = 0;
		
		if (suspensionSystem == null) {
			fitness = 0;
			return;
		}
		
		for (int i = 0; i <= numData; i++) {
			//Obtiene en cada iteración un punto de control
			double x = suspensionSystem.getLowerLimit() + i*(suspensionSystem.getUpperLimit() - suspensionSystem.getLowerLimit())/numData; //En términos de la variable básica
			double y = suspensionSystem.getVariableValueAt(tcd.getYVariable(), x); //Calcula Y respecto de la variable básica
			x = suspensionSystem.getVariableValueAt(tcd.getXVariable(), x);	//Posteriormente se calcula X en las unidades que se hayan indicado
			
			if (Double.isNaN(x)) {
				this.fitness = 0;
				return;
			}
			
			if (i == 0 || maxX < x)
				maxX = x;
			if (i == 0 || minX > x)
				minX = x;
			
			if (i == 0) {
				//fitness.add(new Double());
			}
			else if (i == 1) {
				sentido = x - prevX;
			}
			else {
				if (sentido * (x - prevX) < 0) { //Ha cambiado el sentido
					fitnesses.add(new Double(fitness)); //Guardamos los resultados del 'bucle' anterior y comenzamos a calcular el nuevo.
					fitness = 0;
					//break;
				}
				sentido = x - prevX;
			}
				
			prevX = x;
			
				
			//Calcular suma fitness del punto
			if (x >= tcd.getLowerXValue() && x <= tcd.getUpperXValue()) {		//Esto solo compara valores dentro del rango X objetivo
				//Sumamos a la variable fitness
				//fitness += 1/Math.pow(y - tcd.getValueAt(x), 2);
				fitness += Math.exp(-(Math.abs(y - tcd.getValueAt(x))/(tcd.getUpperYValue() != tcd.getLowerYValue()? tcd.getUpperYValue() - tcd.getLowerYValue(): 1)));
			}
		}
		
		fitnesses.add(fitness);
			
		fitness = 0;
		for (Double d: fitnesses) {
			if (d > fitness)
				fitness = d;
		}
		
		double dx = maxX - minX;	//Dominio de la curva de la solución
		double targetDx = tcd.getUpperXValue() - tcd.getLowerXValue();	//Dominio de la curva objetivo
		
		//Coeficiente de 'peso' para contemplar cuánto abarca la solución
		//el dominio de X de la función objetivo.
		double mult = dx >= targetDx? 1: dx/targetDx;
		
		double distOrigen = Math.sqrt(	Math.pow(suspensionSystem.getVariableValueAt(SuspensionSystem.VERTICAL_FORCE_ON_WHEEL_VARIABLE, suspensionSystem.getLowerLimit()), 2) +
										Math.pow(suspensionSystem.getVariableValueAt(SuspensionSystem.VERTICAL_WHEEL_DISPLACEMENT_VARIABLE, suspensionSystem.getLowerLimit()), 2) );
				
		mult *= dx > 0? Math.pow(2, -(distOrigen)/(dx)): 1;
		//mult *= Math.pow(2, -(distOrigen)/(100));
		
		this.fitness = fitness*mult;
		//this.fitness = 10;	
	}
	
	public void setID (int id) {
		this.id = id;
	}
	
	public double getFitness () { return fitness; }

	public int getID() {
		return id;
	}

	
	public void setColor (Color c) { this.color = c; }
	public Color getColor () { return color; }
	public SuspensionSystem getSuspensionSystem () { return suspensionSystem; }
	
	
}
