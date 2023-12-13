package suspensionSystem;

import java.awt.geom.Point2D;
//import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import GUI.Bar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import suspensionSystem.parameter.Parameter;
import suspensionSystem.parameter.ParameterGroup;

public class ProLinkSuspensionSystem extends SuspensionSystem {
	
	private int type;
	private ArrayList<ParameterGroup> parameterGroups;
	
	private double b;
	private double lowerLimit;
	private double upperLimit;
	
	//Variables intermedias
	private double sl;	//Longitud funcional del basculante
	private double sa;	//Ángulo interno del basculante
	
	
	
	
	public ProLinkSuspensionSystem () {
		//Estructura de parámetros suspensión ProLink
		type = SuspensionSystem.PROLINK_TYPE;
		
		lowerLimit = 0;
		upperLimit = 2;
		
		//Establecimiento de la estructura del sistema de suspensión y sus valores
		//iniciales por defecto.
		
		parameterGroups = new ArrayList<ParameterGroup>();
		parameterGroups.add(new ParameterGroup("Chassis"));
		parameterGroups.get(0).getChildren().add(new Parameter("O2x", 0, 0, 0, false));
		parameterGroups.get(0).getChildren().add(new Parameter("O2y", 399.87, 450, 650, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O4x", 18, -150, 150, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O4y", 281, 200, 400, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O5x", 80, -150, 150, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O5y", 499, 350, 550, true));
		
		parameterGroups.add(new ParameterGroup("Swing arm"));
		parameterGroups.get(1).getChildren().add(new Parameter("s", 520, 450, 600, true));
		parameterGroups.get(1).getChildren().add(new Parameter("sx", 150, -150, 200, true));
		parameterGroups.get(1).getChildren().add(new Parameter("sy", -110, -150, 150, true));
		
		parameterGroups.add(new ParameterGroup("Rocker"));
		parameterGroups.get(2).getChildren().add(new Parameter("r", 42, 20, 60, true));
		parameterGroups.get(2).getChildren().add(new Parameter("rx", 21, -30, 40, true));
		parameterGroups.get(2).getChildren().add(new Parameter("ry", -58.335, -60, 60, true));
		
		parameterGroups.add(new ParameterGroup("Link"));
		parameterGroups.get(3).getChildren().add(new Parameter("l", 130, 80, 180, true));
		
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
		
		parameterGroups.add(new ParameterGroup("Solution"));
		parameterGroups.get(6).getChildren().add(new Parameter("Sol.", 1.0));
		parameterGroups.get(6).getChildren().get(0).setToOptimize(false);
		
		recalc();

	
	}
	
	public ProLinkSuspensionSystem (ProLinkSuspensionSystem ref) {
		type = SuspensionSystem.PROLINK_TYPE;
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
		sl = Math.sqrt(Math.pow(getSx(), 2) + Math.pow(getSy(), 2));
		sa = getAngleFromCats(getSx(), getSy());
		calcLimits();
		
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
			boolean isValid = (Double.isNaN(getBetha(i)) == false) && (getSuspensionUnitCompressionAt(i) >= 0 && getSuspensionUnitCompressionAt(i) <= getLN() - getLM());
				
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
			
			if (Double.isNaN(getBetha(0.0))) {
				lowerLimit = 0;
				upperLimit = 0;
			}
			else {
				lowerLimit = -Math.PI/2;
				upperLimit = 3*Math.PI/2;
			}
		}
		
	}
	
	private double mechanismXLowerLimit(double x1, double x2) {
		//final int iteratorCount;
		
		double xm = (x1 + x2)/2;
		double e = Math.abs(x1 - x2);
		
		if (e > 0.000001) {
			if (Double.isNaN(getBetha(xm)) || getSuspensionUnitCompressionAt(xm) > getLN() - getLM()) {
				x1 = xm;
			}
			else {
				
				x2 = xm;
			}
			x2 = mechanismXLowerLimit(x1, x2);
		}
		
		return x2;
	}
	
	public double getBetha (double b) {
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
		
		
		return vecProd*getSol() < 0? betha1: betha2;
	}


	@Override
	public ArrayList<ParameterGroup> getParameterGroups() {
		return parameterGroups;
	}


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
		double betha = getBetha(b);
		
		
		//System.out.println("BETHA: " + betha);
		double currentLen = Math.sqrt(	Math.pow(getO5x() - (getO2x() + sl*Math.cos(b) + getRx()*Math.cos(betha) - getRy()*Math.sin(betha)), 2) + 
										Math.pow(getO5y() - (getO2y() + sl*Math.sin(b) + getRx()*Math.sin(betha) + getRy()*Math.cos(betha)), 2));
		//System.out.println("Length: " + currentLen);
		
		double suc = getLN() - currentLen;
		return suc;
	}


	@Override
	public double getVerticalForceAt(double b) {
		double F_e = getSuspensionUnitCompressionAt(b)*getK();
		double l = Math.sqrt(Math.pow(getO5x() - getDPointX(b), 2) + Math.pow(getO5y() - getDPointY(b), 2));
		double F_ex = -F_e*(getO5x() - getDPointX(b))/l;
		double F_ey = -F_e*(getO5y() - getDPointY(b))/l;
		
		double c31 = (getAPointY(b) - getO2y())*(getBPointX(b)-getO4x()) - (getAPointX(b) - getO2x())*(getBPointY(b) - getO4y());
		c31 /= (getBPointX(b) - getAPointX(b))*(getBPointY(b) - getO4y()) - (getBPointY(b) - getAPointY(b))*(getBPointX(b) - getO4x());
		
		double F = (getAPointY(b) - getO2y() + (getDPointY(b) - getAPointY(b))*c31)*F_ex;
		F -= (getAPointX(b) - getO2x() + (getDPointX(b) - getAPointX(b))*c31)*F_ey;
		F /= (getCPointX(b) - getO2x());
		return F;
	}


	@Override
	public double getEquivalentStiffnessAt(double b) {
		double k = (getVerticalForceAt(b+0.001)-getVerticalForceAt(b-0.001))/(getVerticalWheelDisplacementAt(b+0.001)-getVerticalWheelDisplacementAt(b-0.001));
		return Double.isNaN(k)? 0: k;
	}


	@Override
	public double getLowerLimit() {
		return lowerLimit;
	}


	@Override
	public double getUpperLimit() {
		return upperLimit;
	}
	
	
	
	//Estos métodos están destinados unicamente a hacer más sencillos los cálculos
	private double getO2x () { return parameterGroups.get(0).getParameter(0).getValue(); }
	private double getO2y () { return parameterGroups.get(0).getParameter(1).getValue(); }
	private double getO4x () { return parameterGroups.get(0).getParameter(2).getValue(); }
	private double getO4y () { return parameterGroups.get(0).getParameter(3).getValue(); }
	private double getO5x () { return parameterGroups.get(0).getParameter(4).getValue(); }
	private double getO5y () { return parameterGroups.get(0).getParameter(5).getValue(); }
	
	private double getS () { return parameterGroups.get(1).getParameter(0).getValue(); }
	private double getSx () { return parameterGroups.get(1).getParameter(1).getValue(); }
	private double getSy () { return parameterGroups.get(1).getParameter(2).getValue(); }

	private double getRocker () { return parameterGroups.get(2).getParameter(0).getValue(); }
	private double getRx () { return parameterGroups.get(2).getParameter(1).getValue(); }
	private double getRy () { return parameterGroups.get(2).getParameter(2).getValue(); }
	
	private double getL () { return parameterGroups.get(3).getParameter(0).getValue(); }
	
	private double getLN () { return parameterGroups.get(4).getParameter(0).getValue(); }
	private double getLM () { return parameterGroups.get(4).getParameter(1).getValue(); }
	private double getK () { return parameterGroups.get(4).getParameter(2).getValue(); }
	
	private double getR () { return parameterGroups.get(5).getParameter(0).getValue(); }
	
	private double getSol () { return parameterGroups.get(6).getParameter(0).getValue(); }


	@Override
	public void draw(GraphicsContext gc) {
		
		
		gc.strokeOval(getCPointX(b) - getR(), getCPointY(b) - getR(), 2*getR(), 2*getR());
		
		//gc.strokeOval(getAPointX(b) - 10, getAPointY(b) - 10, 20, 20);
		//gc.strokeOval(getCPointX(b) - 10, getCPointY(b) - 10, 20, 20);
		
		Bar swingarm = new Bar (0, 0, getS(), 0, getSx(), getSy());
		swingarm.setX(getO2x());
		swingarm.setY(getO2y());
		swingarm.setAngle(this.getSwingarmAngleAt(b));
		swingarm.draw(gc);
		
		
		Bar rocker = new Bar (0, 0, getRocker(), 0, getRx(), getRy());
		rocker.setX(getO2x() + sl*Math.cos(b));
		rocker.setY(getO2y() + sl*Math.sin(b)); 
		rocker.setAngle(getBetha(b));
		rocker.draw(gc);
		
		Bar link = new Bar (0, 0, getL(), 0);
		Point2D.Double p = rocker.localToAbsoluteCoordinates(new Point2D.Double(getRocker()	, .0));
		link.setX(p.x);
		link.setY(p.y);
		link.setAngle(getAngleFromCats(getO4x()-p.x, getO4y()-p.y));
		link.draw(gc);
		
		p = rocker.localToAbsoluteCoordinates(new Point2D.Double(getRx(), getRy()));
		if (!(Double.isNaN(p.x) && Double.isNaN(p.y))) {
			Bar.drawSpring(gc, p.x,p.y, getO5x(), getO5y());
		}
		//System.out.println("x: " + p.x + ", y: " + p.y);
		
		
		
		
		Bar.drawFixPoint(gc, getO2x(), getO2y(), 0);
		Bar.drawFixPoint(gc, getO4x(), getO4y(), 0);
		Bar.drawFixPoint(gc, getO5x(), getO5y(), 0);
		
	}


	@Override
	public double getB() {
		return b;
	}

	@Override
	public void setB(double b) {
		this.b = b;
	}





	@Override
	public SuspensionSystem getCopy() {
		return (SuspensionSystem) new ProLinkSuspensionSystem(this);
	}


	@Override
	public int getType() {
		return this.type;
	}
	
	public double[] getAPoint (double b) {
		double[] A = new double[2];
		A[0] = getO2x() + sl*Math.cos(b);
		A[1] = getO2y() + sl*Math.sin(b);
		return A;
	}
	public double getAPointX (double b) { return getO2x() + sl*Math.cos(b); }
	public double getAPointY (double b) { return getO2y() + sl*Math.sin(b); }
	
	public double[] getBPoint (double b) {
		double[] B = new double[2];
		double betha = getBetha(b);
		B[0] = getAPoint(b)[0] + getRocker()*Math.cos(betha);
		B[1] = getAPoint(b)[1] + getRocker()*Math.sin(betha);
		return B;
	}
	public double getBPointX (double b) { return getAPointX(b) + getRocker()*Math.cos(getBetha(b)); }
	public double getBPointY (double b) { return getAPointY(b) + getRocker()*Math.sin(getBetha(b)); }
	
	public double[] getDPoint (double b) {
		double[] D = new double[2];
		double betha = getBetha(b);
		D[0] = getAPoint(b)[0] + getRx()*Math.cos(betha) - getRy()*Math.sin(betha);
		D[1] = getAPoint(b)[1] + getRx()*Math.sin(betha) + getRy()*Math.cos(betha);
		return D;
	}
	public double getDPointX (double b) { 
		double betha = getBetha(b);
		return getAPointX(b) + getRx()*Math.cos(betha) - getRy()*Math.sin(betha);
	}
	public double getDPointY (double b) { 
		double betha = getBetha(b);
		return getAPointY(b) + getRx()*Math.sin(betha) + getRy()*Math.cos(betha);
	}
	
	public double[] getCPoint (double b) {
		double[] C = new double[2];
		C[0] = getO2x() + getB()*Math.cos(getSwingarmAngleAt(b));
		C[1] = getO2y() + getB()*Math.sin(getSwingarmAngleAt(b));
		return C;
	}
	public double getCPointX (double b) { return getO2x() + getS()*Math.cos(getSwingarmAngleAt(b)); }
	public double getCPointY (double b) { return getO2y() + getS()*Math.sin(getSwingarmAngleAt(b)); }
	
	
	
}
