package suspensionSystem;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import GUI.Bar;
import javafx.scene.canvas.GraphicsContext;
import suspensionSystem.parameter.Parameter;
import suspensionSystem.parameter.ParameterGroup;

public class UniTrackSuspensionSystem extends SuspensionSystem {
	
	private int type;
	private ArrayList<ParameterGroup> parameterGroups;
	
	private double b;
	private double lowerLimit;
	private double upperLimit;
	
	//Variables intermedias
	private double sl;	//Longitud funcional del basculante
	private double sa;	//Ángulo interno del basculante
	
	public UniTrackSuspensionSystem() {
type = SuspensionSystem.PROLINK_TYPE;
		
		lowerLimit = -Math.PI/2;
		upperLimit = 3*Math.PI/2;
		
		//Establecimiento de la estructura del sistema de suspensión y sus valores
		//iniciales por defecto.
		
		parameterGroups = new ArrayList<ParameterGroup>();
		parameterGroups.add(new ParameterGroup("Chassis"));
		parameterGroups.get(0).getChildren().add(new Parameter("O2x", 0));//, 0, 0, false));
		parameterGroups.get(0).getChildren().add(new Parameter("O2y", 399.87));//, 450, 650, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O4x", 18));//, -150, 150, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O4y", 281));//, 200, 400, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O5x", 80));//, -150, 150, true));
		parameterGroups.get(0).getChildren().add(new Parameter("O5y", 499));//, 350, 550, true));
		
		parameterGroups.add(new ParameterGroup("Swing arm"));
		parameterGroups.get(1).getChildren().add(new Parameter("s", 520));//, 450, 600, true));
		parameterGroups.get(1).getChildren().add(new Parameter("sx", 150));//, -150, 200, true));
		parameterGroups.get(1).getChildren().add(new Parameter("sy", -110));//, -150, 150, true));
		
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
		
		parameterGroups.add(new ParameterGroup("Solution"));
		parameterGroups.get(6).getChildren().add(new Parameter("Sol.", 1.0));
		parameterGroups.get(6).getChildren().get(0).setToOptimize(false);
		
		recalc();
	}

	public UniTrackSuspensionSystem (UniTrackSuspensionSystem ref) {
		type = SuspensionSystem.UNITRACK_TYPE;
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
	
	public double getBeta (double b) {
		double _a = getO4x() - getO2x() - sl*Math.cos(b);
		double _b = getO4y() - getO2y() - sl*Math.sin(b);
		double _c = Math.pow(getRocker(), 2) - Math.pow(getL(), 2) - Math.pow(_a, 2) - Math.pow(_b, 2);
		_a *= 2*getL();
		_b *= 2*getL();
		
		double A = -Math.pow(_b, 2)-Math.pow(_a, 2);
		double B = -2*_a*_c;
		double C = Math.pow(_b, 2)-Math.pow(_c, 2);
		
		
		double betha1 = (-B + Math.sqrt(Math.pow(B, 2) - 4*A*C))/(2*A);
		//System.out.print("betha1: " + betha1);
		betha1 = Math.acos(betha1);
		//System.out.println(", " + betha1);
		double l4 = Math.sqrt(	Math.pow(getO4x() - (getO2x() + sl*Math.cos(b) + getL()*Math.cos(betha1)), 2) + 
								Math.pow(getO4y() - (getO2y() + sl*Math.sin(b) + getL()*Math.sin(betha1)), 2));
		if (Math.abs(l4 - getRocker()) > 0.0001) {
			betha1 *= -1;
		}
		
		
		
		double betha2 = (-B - Math.sqrt(Math.pow(B, 2) - 4*A*C))/(2*A);
		//System.out.print("betha2: " + betha2);
		betha2 = Math.acos(betha2);
		//System.out.println(", " + betha2);
		l4 = Math.sqrt(	Math.pow(getO4x() - (getO2x() + sl*Math.cos(b) + getL()*Math.cos(betha2)), 2) + 
						Math.pow(getO4y() - (getO2y() + sl*Math.sin(b) + getL()*Math.sin(betha2)), 2));
		if (Math.abs(l4 - getRocker()) > 0.001) {
			betha2 *= -1;
		}
		
		//Hasta aquí tenemos dos soluciones perfectamente válidas para b. Ahora hay que decidir cuál de ellas escoger
		//para que los resultados en todos los valores de b sea coherente.
		
		
		//double ganma = getAngleFromCats();
		//System.out.println("b1: " + betha1 + ", b2: " + betha2);
		
		double vecProd = (getL()*Math.cos(betha1))*(getO4y() - (getO2y() + sl*Math.sin(b) + getL()*Math.sin(betha1)));
		vecProd -= (getL()*Math.sin(betha1))*(getO4x() - (getO2x() + sl*Math.cos(b) + getL()*Math.cos(betha1)));
		
		
		return vecProd*1 < 0? betha1: betha2;
	}

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
		
		
		Bar link = new Bar (0, 0, getL(), 0);
		link.setX(getAPointX(b));
		link.setY(getAPointY(b));
		link.setAngle(getBeta(b));
		link.draw(gc);
		
		Bar rocker = new Bar (0, 0, getRocker(), 0, getRx(), getRy());
		Point2D.Double p = link.localToAbsoluteCoordinates(new Point2D.Double(getL()	, .0));
		rocker.setX(p.x);
		rocker.setY(p.y);
		rocker.setAngle(getAngleFromCats(getO4x()-p.x, getO4y()-p.y));
		rocker.draw(gc);
		
		
		
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
	public void recalc() {
		//Resolver problema de posiciones.
			sl = Math.sqrt(Math.pow(getSx(), 2) + Math.pow(getSy(), 2));
			sa = getAngleFromCats(getSx(), getSy());
			//calcLimits();
		
	}

	@Override
	public double getSwingarmAngleAt(double b) {
		return b - sa;
	}

	@Override
	public double getVerticalWheelDisplacementAt(double b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSuspensionUnitCompressionAt(double b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getVerticalForceAt(double b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEquivalentStiffnessAt(double b) {
		// TODO Auto-generated method stub
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
		return (SuspensionSystem) new UniTrackSuspensionSystem(this);
	}
	
	
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
	
	
	public double getCPointX (double b) { return getO2x() + getS()*Math.cos(getSwingarmAngleAt(b)); }
	public double getCPointY (double b) { return getO2y() + getS()*Math.sin(getSwingarmAngleAt(b)); }
	
	public double getAPointX (double b) { return getO2x() + sl*Math.cos(b); }
	public double getAPointY (double b) { return getO2y() + sl*Math.sin(b); }
	
	public double getBPointX (double b) { return getAPointX(b) + getL()*Math.cos(getBeta(b)); }
	public double getBPointY (double b) { return getAPointY(b) + getL()*Math.sin(getBeta(b)); }

}
