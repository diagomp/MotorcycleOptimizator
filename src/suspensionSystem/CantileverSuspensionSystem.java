package suspensionSystem;

import java.awt.geom.Point2D;
//import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import GUI.Bar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import suspensionSystem.parameter.Parameter;
import suspensionSystem.parameter.ParameterGroup;

public class CantileverSuspensionSystem extends SuspensionSystem {
	
	private int type;
	private ArrayList<ParameterGroup> parameterGroups;
	
	private double lowerLimit;
	private double upperLimit;
	private int numSolutions;
	private int solution;

	private double b;	//Variable base
	private double dO3x_O2x;
	private double dO3y_O2y;
	private double bO;
	private double bs;
	private double O2O3_2;
	private double O2A_2;
	
	
	public CantileverSuspensionSystem () {
		type = SuspensionSystem.CANTILEVER_TYPE;
		parameterGroups = new ArrayList<ParameterGroup>();
		
		
		parameterGroups.add(new ParameterGroup("Chassis"));
		parameterGroups.get(0).getChildren().add(new Parameter("O2x", 0));
		parameterGroups.get(0).getChildren().add(new Parameter("O2y", 400));
		parameterGroups.get(0).getChildren().add(new Parameter("O3x", -110));
		parameterGroups.get(0).getChildren().add(new Parameter("O3y", 550));
		
		parameterGroups.add(new ParameterGroup("Swing arm"));
		parameterGroups.get(1).getChildren().add(new Parameter("s", 520));
		parameterGroups.get(1).getChildren().add(new Parameter("sx", 130));
		parameterGroups.get(1).getChildren().add(new Parameter("sy", 95));
		
		parameterGroups.add(new ParameterGroup("Suspension Unit"));
		parameterGroups.get(2).getChildren().add(new Parameter("ln", 267));
		parameterGroups.get(2).getChildren().add(new Parameter("lm", 210));
		parameterGroups.get(2).getChildren().add(new Parameter("k", 300));
		
		parameterGroups.add(new ParameterGroup("Rear wheel"));
		parameterGroups.get(3).getChildren().add(new Parameter("R", 300.6));
		parameterGroups.get(3).getChildren().get(0).setToOptimize(false);
		
		parameterGroups.add(new ParameterGroup("Solutions"));
		parameterGroups.get(4).getChildren().add(new Parameter("Sol.", 1));
		
		recalc();
		
		
	}
	
	public CantileverSuspensionSystem (CantileverSuspensionSystem ref) {
		type = SuspensionSystem.CANTILEVER_TYPE;
		parameterGroups = new ArrayList<ParameterGroup>();
		
		for (ParameterGroup pg: ref.getParameterGroups()) {
			ParameterGroup newPg = new ParameterGroup(pg.getName());
			for (Parameter p: pg.getChildren()) {
				Parameter newP = p.getCopy();
				newPg.getChildren().add(newP);
			}
			parameterGroups.add(newPg);
		}
		
		recalc();
	}
	
	@Override
	public void recalc () {
		dO3x_O2x = getO3x() - getO2x();
		dO3y_O2y = getO3y() - getO2y();
		
		//Calculate bO
		if (dO3x_O2x != 0) {
			bO = Math.atan(dO3y_O2y/dO3x_O2x);
			bO += dO3x_O2x < 0? Math.PI: 0;
		}
		else {
			bO = Math.PI/2;
			bO *= dO3y_O2y > 0? 1: -1;
		}
		
		//Calculate bs
		if (getSx() != 0) {
			bs = Math.atan(getSy()/getSx());
			bs += getSx() < 0? Math.PI: 0;
		}
		else {
			bs = Math.PI/2;
			bs *= getSy() > 0? 1: -1;
		}
		
		O2O3_2 = Math.pow(dO3y_O2y, 2) + Math.pow(dO3x_O2x, 2); //O2O3^2
		O2A_2 = Math.pow(getSx(), 2) + Math.pow(getSy(), 2); //OA^2
		
		
		calcLimits();
		
		/*this.upperLimit = 3*Math.PI/2;
		this.lowerLimit = -Math.PI/2;*/
	}
	
	private void calcLimits () {
		
		ArrayList<Double> lnLimits = new ArrayList<Double>();
		ArrayList<Double> lmLimits = new ArrayList<Double>();
		
		double d = 2*Math.sqrt(O2O3_2)*Math.sqrt(O2A_2);
		if (d != 0) {
			int numSolutions = 0;
			solution = (int) Math.abs(getSol());
			
			//Límite LN
			double n = O2O3_2 + O2A_2 - Math.pow(getLN(), 2);
			if (Math.abs(n/d) <= 1) {
				//Existen dos soluciones para el límite LN
				lnLimits.add(new Double(bO - bs - (+Math.acos(n/d))));
				lnLimits.add(new Double(bO - bs - (-Math.acos(n/d))));
				numSolutions += 1;
			}
			
			n = O2O3_2 + O2A_2 - Math.pow(getLM(), 2);
			if (Math.abs(n/d) <= 1) {
				//Existen dos soluciones para el límite LM
				lmLimits.add(new Double(bO - bs - (+Math.acos(n/d))));
				lmLimits.add(new Double(bO - bs - (-Math.acos(n/d))));
				numSolutions += 1;
			}
			
			solution = solution % numSolutions;
			if (numSolutions == 2) {
				lowerLimit = lnLimits.get(solution) <= lmLimits.get(solution)? lnLimits.get(solution): lmLimits.get(solution);
				upperLimit = lnLimits.get(solution) > lmLimits.get(solution)? lnLimits.get(solution): lmLimits.get(solution);
			}
			else if (numSolutions == 1) {
				if (lnLimits.size() == 2) {
					double l_prom = getSuspensionUnitCompressionAt((lnLimits.get(0)+lnLimits.get(1))/2);
					if (l_prom > getLN() - getLM() || l_prom < 0) {
						lnLimits.set(0, new Double (lnLimits.get(0) - 2*Math.PI));
					}
					lowerLimit = lnLimits.get(0) <= lnLimits.get(1)? lnLimits.get(0): lnLimits.get(1);
					upperLimit = lnLimits.get(0) > lnLimits.get(1)? lnLimits.get(0): lnLimits.get(1);
					
				}
				else {
					//System.out.println("Angulo de prueba: " + (lmLimits.get(0) + lmLimits.get(1))/2);
					double l_prom = getSuspensionUnitCompressionAt((lmLimits.get(0) + lmLimits.get(1))/2);
					if (l_prom > getLN() - getLM() || l_prom < 0) {
						lmLimits.set(0, new Double (lmLimits.get(0)+2*Math.PI));
					}
					lowerLimit = lmLimits.get(0) <= lmLimits.get(1)? lmLimits.get(0): lmLimits.get(1);
					upperLimit = lmLimits.get(0) > lmLimits.get(1)? lmLimits.get(0): lmLimits.get(1);
				}
				
				if (upperLimit == lowerLimit) {
					//upperLimit += 2*Math.PI;
					lowerLimit = -Math.PI/2;
					upperLimit = 3*Math.PI/2;
				}
			}
			
			
		}
		else {
			//Si llegamos aquí, no existirá ninguna solución para ninguno de los límites ln y lm
			//Todos los ángulos del basculante son posibles
			lowerLimit = -Math.PI/2;
			upperLimit = 3*Math.PI/2;
			
		}
		
	}
	

	@Override
	public double getB () {
		return b;
	}
	
	@Override
	public void setB (double b) {
		this.b = b;
	}
	
	@Override
	public double getSwingarmAngleAt(double b) {
		return b;
	}

	@Override
	public double getVerticalWheelDisplacementAt(double b) {
		return getO2y() + getS()*Math.sin(b) - getR();
	}

	@Override
	public double getSuspensionUnitCompressionAt(double b) {
		//recalc();
		
		double l = Math.sqrt(O2O3_2 + O2A_2 - 2*Math.sqrt(O2O3_2)*Math.sqrt(O2A_2)*Math.cos(bO - bs - b));
		return getLN() - l;
	}

	
	
	@Override
	public double getVerticalForceAt(double b) {
		double dx = getO3x() - Math.sqrt(O2A_2)*Math.cos(b + bs) - getO2x();
		double dy = getO3y() - Math.sqrt(O2A_2)*Math.sin(b + bs) - getO2y();
		double b3 = 0;
		if (dx != 0) {
			b3 = Math.atan(dy/dx);
			b3 += dx < 0? Math.PI: 0;
		}
		else {
			b3 = Math.PI/2;
			b3 *= dy > 0? 1: -1;
		}
		
		double f = getK()*getSuspensionUnitCompressionAt(b);
		f *= Math.sqrt(O2A_2)/getS();
		f *= Math.sin(b3 - b - bs)/Math.cos(b);
		return f;
	}
	
	//TODO: Evitar conflictos en los extremos al calcular la derivada.
	@Override
	public double getEquivalentStiffnessAt(double b) {
		double k = (getVerticalForceAt(b+0.001)-getVerticalForceAt(b-0.001))/(getVerticalWheelDisplacementAt(b+0.001)-getVerticalWheelDisplacementAt(b-0.001));
		return k;
	}

	@Override
	public double getLowerLimit() {
		return lowerLimit;
	}

	@Override
	public double getUpperLimit() {
		return upperLimit;
	}

	@Override
	public ArrayList<ParameterGroup> getParameterGroups() {
		return parameterGroups;
	}
	
	
	//Estos métodos están destinados unicamente a hacer más sencillos los cálculos
	private double getO2x () { return parameterGroups.get(0).getParameter(0).getValue(); }
	private double getO2y () { return parameterGroups.get(0).getParameter(1).getValue(); }
	private double getO3x () { return parameterGroups.get(0).getParameter(2).getValue(); }
	private double getO3y () { return parameterGroups.get(0).getParameter(3).getValue(); }
		
	private double getS () { return parameterGroups.get(1).getParameter(0).getValue(); }
	private double getSx () { return parameterGroups.get(1).getParameter(1).getValue(); }
	private double getSy () { return parameterGroups.get(1).getParameter(2).getValue(); }
		
	private double getLN () { return parameterGroups.get(2).getParameter(0).getValue(); }
	private double getLM () { return parameterGroups.get(2).getParameter(1).getValue(); }
	private double getK () { return parameterGroups.get(2).getParameter(2).getValue(); }
		
	private double getR () { return parameterGroups.get(3).getParameter(0).getValue(); }
	
	private double getSol () { return parameterGroups.get(4).getParameter(0).getValue(); }

	@Override
	public void draw(GraphicsContext gc) {
		/*gc.setStroke(Color.CORAL);
		gc.strokeOval(-50, -50, 100, 100);*/
		
		
		gc.strokeOval(getO2x() + getS()*Math.cos(b) - getR(), getO2y() + getS()*Math.sin(b) - getR(), 2*getR(), 2*getR());
		
		Bar swingarm = new Bar (0, 0, getS(), 0, getSx(), getSy());
		swingarm.setX(getO2x());
		swingarm.setY(getO2y());
		swingarm.setAngle(this.getSwingarmAngleAt(b));
		swingarm.draw(gc);
		
		Point2D.Double point = swingarm.localToAbsoluteCoordinates( new Point2D.Double(getSx(), getSy()));
		Bar.drawSpring(gc, getO3x(), getO3y(), point.getX(), point.getY());
		
		Bar.drawFixPoint(gc, getO2x(), getO2y(), 3);
		Bar.drawFixPoint(gc, getO3x(), getO3y(), 3);
		
		
		
	}
	
	@Override
	public SuspensionSystem getCopy () {
		return (SuspensionSystem) new CantileverSuspensionSystem(this);
	}

	@Override
	public int getType() {
		return type;
	}
	

}
