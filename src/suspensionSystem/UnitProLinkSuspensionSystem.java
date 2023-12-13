package suspensionSystem;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import GUI.Bar;
import javafx.scene.canvas.GraphicsContext;
import suspensionSystem.parameter.Parameter;
import suspensionSystem.parameter.ParameterGroup;

public class UnitProLinkSuspensionSystem extends SuspensionSystem {
	
	private int type;
	private ArrayList<ParameterGroup> parameterGroups;
	
	private double b;
	private double lowerLimit;
	private double upperLimit;
	
	//Variables intermedias
	private double sl;	//Longitud funcional del basculante
	private double sa;	//Ángulo interno del basculante
	
	
	public UnitProLinkSuspensionSystem () {
		type = SuspensionSystem.UNIT_PROLINK_TYPE;
		
		
		parameterGroups = new ArrayList<ParameterGroup>();
		parameterGroups.add(new ParameterGroup("Chassis"));
		parameterGroups.get(0).getChildren().add(new Parameter("O2x", 0));//, 0, 0, false));
		parameterGroups.get(0).getChildren().add(new Parameter("O2y", 399.87));//, 450, 650, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O4x", 18));//, -150, 150, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O4y", 281));//, 200, 400, true));
		
		parameterGroups.add(new ParameterGroup("Swing arm"));
		parameterGroups.get(1).getChildren().add(new Parameter("s", 520));//, 450, 600, true));
		parameterGroups.get(1).getChildren().add(new Parameter("s1x", 150));//, -150, 200, true));
		parameterGroups.get(1).getChildren().add(new Parameter("s1y", -110));//, -150, 150, true));
		parameterGroups.get(1).getChildren().add(new Parameter("s2x", 110));//, -150, 200, true));
		parameterGroups.get(1).getChildren().add(new Parameter("s2y", 80));//, -150, 150, true));
		
		parameterGroups.add(new ParameterGroup("Rocker"));
		parameterGroups.get(2).getChildren().add(new Parameter("r", 42));//, 20, 60, true));
		parameterGroups.get(2).getChildren().add(new Parameter("rx", 21));//, -30, 40, true));
		parameterGroups.get(2).getChildren().add(new Parameter("ry", -58.335));//, -60, 60, true));
		
		parameterGroups.add(new ParameterGroup("Link"));
		parameterGroups.get(3).getChildren().add(new Parameter("l", 130));//, 80, 180, true));
		
		parameterGroups.add(new ParameterGroup("Suspension Unit"));
		parameterGroups.get(4).getChildren().add(new Parameter("ln", 267));
		parameterGroups.get(4).getChildren().get(0).setToOptimize(false);
		parameterGroups.get(4).getChildren().add(new Parameter("lm", 217));
		parameterGroups.get(4).getChildren().get(1).setToOptimize(false);
		parameterGroups.get(4).getChildren().add(new Parameter("k", 90));
		parameterGroups.get(4).getChildren().get(2).setToOptimize(false);
		
		parameterGroups.add(new ParameterGroup("Rear wheel"));
		parameterGroups.get(5).getChildren().add(new Parameter("R", 300));
		parameterGroups.get(5).getChildren().get(0).setToOptimize(false);
		
		recalc();
	}
	
	public UnitProLinkSuspensionSystem (UnitProLinkSuspensionSystem ref) {
		type = SuspensionSystem.UNIT_PROLINK_TYPE;
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
	public void recalc() {
		//Resolver problema de posiciones.
		sl = Math.sqrt(Math.pow(getS1x(), 2) + Math.pow(getS1y(), 2));
		sa = getAngleFromCats(getS1x(), getS1y());
		//calcLimits();
		lowerLimit = -Math.PI/2;
		upperLimit = Math.PI/2;
		
		calcLimits();
		
	}
	
	public double getBeta (double b) {
		double _a = getO4x() - getO2x() - sl*Math.cos(b);
		double _b = getO4y() - getO2y() - sl*Math.sin(b);
		double _c = Math.pow(getL(), 2) - Math.pow(getRocker(), 2) - Math.pow(_a, 2) - Math.pow(_b, 2);
		_a *= 2*getRocker();
		_b *= 2*getRocker();
		
		double A = -Math.pow(_b, 2)-Math.pow(_a, 2);
		double B = -2*_a*_c;
		double C = Math.pow(_b, 2)-Math.pow(_c, 2);
		
		
		double betha1 = (-B + Math.sqrt(Math.pow(B, 2) - 4*A*C))/(2*A);
		//System.out.print("betha1: " + betha1);
		betha1 = Math.acos(betha1);
		//System.out.println(", " + betha1);
		double l4 = Math.sqrt(	Math.pow(getO4x() - (getO2x() + sl*Math.cos(b) + getRocker()*Math.cos(betha1)), 2) + 
								Math.pow(getO4y() - (getO2y() + sl*Math.sin(b) + getRocker()*Math.sin(betha1)), 2));
		if (Math.abs(l4 - getL()) > 0.0001) {
			betha1 *= -1;
		}
		
		
		
		double betha2 = (-B - Math.sqrt(Math.pow(B, 2) - 4*A*C))/(2*A);
		//System.out.print("betha2: " + betha2);
		betha2 = Math.acos(betha2);
		//System.out.println(", " + betha2);
		l4 = Math.sqrt(	Math.pow(getO4x() - (getO2x() + sl*Math.cos(b) + getRocker()*Math.cos(betha2)), 2) + 
						Math.pow(getO4y() - (getO2y() + sl*Math.sin(b) + getRocker()*Math.sin(betha2)), 2));
		if (Math.abs(l4 - getL()) > 0.001) {
			betha2 *= -1;
		}
		
		//Hasta aquí tenemos dos soluciones perfectamente válidas para b. Ahora hay que decidir cuál de ellas escoger
		//para que los resultados en todos los valores de b sea coherente.
		
		
		//double ganma = getAngleFromCats();
		//System.out.println("b1: " + betha1 + ", b2: " + betha2);
		
		double vecProd = (getRocker()*Math.cos(betha1))*(getO4y() - (getO2y() + sl*Math.sin(b) + getRocker()*Math.sin(betha1)));
		vecProd -= (getRocker()*Math.sin(betha1))*(getO4x() - (getO2x() + sl*Math.cos(b) + getRocker()*Math.cos(betha1)));
		
		
		return vecProd*1 < 0? betha1: betha2;
		//return betha1;
	}
	
	public void calcLimits() {
		/*TODO
		 * Calcular límites y soluciones existentes. Elegir entre una de ellas*/
		ArrayList<Double> lowerLimits = new ArrayList<Double>();
		ArrayList<Double> upperLimits = new ArrayList<Double>();
		
		boolean prevIsValid = false;
		double previ = 0;
		boolean first = true;
		for (double i = -Math.PI/2; i < 3*Math.PI/2; i += Math.PI/(360*4)) {
			boolean isValid = (Double.isNaN(getBeta(i)) == false) && (getSuspensionUnitCompressionAt(i) >= 0 && getSuspensionUnitCompressionAt(i) <= getLN() - getLM());
				
				/*if (getSuspensionUnitCompressionAt(i) < 0 || getSuspensionUnitCompressionAt(i) > getLN() - getLM())
					isValid = false;*/
				
			if (!first) {
				if (prevIsValid == true && isValid == false)  {
					upperLimits.add(previ);
				}
				else if (prevIsValid == false && isValid == true) {
					lowerLimits.add(i);
				}
			}
			
			previ = i;
			prevIsValid = isValid;
			first = false;
			
			//System.out.println("b: " + i*180/Math.PI + " - " + isValid);
		}
		
		if (lowerLimits.size() > 0 && upperLimits.size() > 0) {
			lowerLimit = lowerLimits.get(0);
			int index;
			for (index = 0; index < upperLimits.size(); index++) {
				if (upperLimits.get(index) > lowerLimit) {
					upperLimit = upperLimits.get(index);
					break;
				}
			}
			if (index >= upperLimits.size()) {
				upperLimit = upperLimits.get(0) + 2*Math.PI;
			}
			
			//System.out.print("LL: ");
			for (int i = 0; i < lowerLimits.size(); i++)
				System.out.print(lowerLimits.get(i) + ", ");
			
			//System.out.print("\nUL: ");
			for (int i = 0; i < upperLimits.size(); i++)
				System.out.print(upperLimits.get(i) + ", ");
			//System.out.println();
			//double ll = mechanismXLowerLimit(lowerLimit, upperLimit);
			//double up = mechanismXUpperLimit(lowerLimit, upperLimit);
			
			//lowerLimit = ll;
			
		}
		else {
			
			if (Double.isNaN(getBeta(0.0))) {
				lowerLimit = 0;
				upperLimit = 0;
			}
			else {
				lowerLimit = -Math.PI/2;
				upperLimit = 3*Math.PI/2;
			}
		}
		
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.strokeOval(getCPointX(b) - getR(), getCPointY(b) - getR(), 2*getR(), 2*getR());
		
		Bar swingarm = new Bar (0, 0, getS1x(), getS1y(), getS(), 0, getS2x(), getS2y());
		swingarm.setX(getO2x());
		swingarm.setY(getO2y());
		swingarm.setAngle(this.getSwingarmAngleAt(b));
		swingarm.draw(gc);
		
		Bar rocker = new Bar (0, 0, getRocker(), 0, getRx(), getRy());
		rocker.setX(getO2x() + sl*Math.cos(b));
		rocker.setY(getO2y() + sl*Math.sin(b));
		rocker.setAngle(getBeta(b));
		rocker.draw(gc);
		
		Bar link = new Bar (0, 0, getL(), 0);
		Point2D.Double p = rocker.localToAbsoluteCoordinates(new Point2D.Double(getRocker()	, .0));
		link.setX(p.x);
		link.setY(p.y);
		link.setAngle(getAngleFromCats(getO4x()-p.x, getO4y()-p.y));
		link.draw(gc);
		
		p = rocker.localToAbsoluteCoordinates(new Point2D.Double(getRx(), getRy()));
		if (!(Double.isNaN(p.x) && Double.isNaN(p.y))) {
			Bar.drawSpring(gc, p.x,p.y, getEPointX(b), getEPointY(b));
		}
		
		
		Bar.drawFixPoint(gc, getO2x(), getO2y(), 0);
		Bar.drawFixPoint(gc, getO4x(), getO4y(), 0);
		
	}

	@Override
	public double getB() { return b; }

	@Override
	public void setB(double b) { this.b = b; }



	@Override
	public double getSwingarmAngleAt(double b) {
		return b - sa;
	}

	@Override
	public double getVerticalWheelDisplacementAt(double b) {
		return getO2y() + getS()*Math.sin(getSwingarmAngleAt(b)) - getR();
	}

	@Override
	public double getSuspensionUnitCompressionAt(double b) {
		return getLN() - Math.sqrt(Math.pow(getEPointX(b) - getDPointX(b), 2) + Math.pow(getEPointY(b) - getDPointY(b), 2));
	}

	@Override
	public double getVerticalForceAt(double b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEquivalentStiffnessAt(double b) {
		return 0;
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
	public int getType() {
		return type;
	}

	@Override
	public ArrayList<ParameterGroup> getParameterGroups() {
		return parameterGroups;
	}

	@Override
	public SuspensionSystem getCopy() {
		return (SuspensionSystem) new UnitProLinkSuspensionSystem(this);
	}
	
	
	
	public double getO2x() { return parameterGroups.get(0).getChildren().get(0).getValue(); }
	public double getO2y() { return parameterGroups.get(0).getChildren().get(1).getValue(); }
	public double getO4x() { return parameterGroups.get(0).getChildren().get(2).getValue(); }
	public double getO4y() { return parameterGroups.get(0).getChildren().get(3).getValue(); }
	
	public double getS() { return parameterGroups.get(1).getChildren().get(0).getValue(); }
	public double getS1x() { return parameterGroups.get(1).getChildren().get(1).getValue(); }
	public double getS1y() { return parameterGroups.get(1).getChildren().get(2).getValue(); }
	public double getS2x() { return parameterGroups.get(1).getChildren().get(3).getValue(); }
	public double getS2y() { return parameterGroups.get(1).getChildren().get(4).getValue(); }
	
	public double getRocker() { return parameterGroups.get(2).getChildren().get(0).getValue(); }
	public double getRx() { return parameterGroups.get(2).getChildren().get(1).getValue(); }
	public double getRy() { return parameterGroups.get(2).getChildren().get(2).getValue(); }
	
	public double getL() { return parameterGroups.get(3).getChildren().get(0).getValue(); }
	
	public double getLN() { return parameterGroups.get(4).getChildren().get(0).getValue(); }
	public double getLM() { return parameterGroups.get(4).getChildren().get(1).getValue(); }
	public double getK() { return parameterGroups.get(4).getChildren().get(2).getValue(); }
	
	public double getR() { return parameterGroups.get(4).getChildren().get(0).getValue(); }
	
	
	public double getCPointX (double b) { return getO2x() + getS()*Math.cos(getSwingarmAngleAt(b)); }
	public double getCPointY (double b) { return getO2y() + getS()*Math.sin(getSwingarmAngleAt(b)); }
	public double getEPointX (double b) { return getO2x() + getS2x()*Math.cos(getSwingarmAngleAt(b)) - getS2y()*Math.sin(getSwingarmAngleAt(b)); }
	public double getEPointY (double b) { return getO2y() + getS2x()*Math.sin(getSwingarmAngleAt(b)) + getS2y()*Math.cos(getSwingarmAngleAt(b)); }
	public double getDPointX (double b) { return getO2x() + sl*Math.cos(b) + getRx()*Math.cos(getBeta(b)) - getRy()*Math.sin(getBeta(b)); }
	public double getDPointY (double b) { return getO2y() + sl*Math.sin(b) + getRx()*Math.sin(getBeta(b)) + getRy()*Math.cos(getBeta(b)); }
	
	
	
}
