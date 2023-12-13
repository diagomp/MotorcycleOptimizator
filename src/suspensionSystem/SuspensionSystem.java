package suspensionSystem;

import java.util.ArrayList;

import GUI.Drawable;
import suspensionSystem.parameter.*;

public abstract class SuspensionSystem implements Drawable {
	public static final int CLASSIC_TYPE = 0;
	public static final int CANTILEVER_TYPE = 1;
	public static final int PROLINK_TYPE = 2;
	public static final int UNIT_PROLINK_TYPE = 3;
	public static final int UNITRACK_TYPE = 4;
	public static final int FULLFLOATER_TYPE = 5;
	public static final String[] typeNames = {	"Classic suspension system", 
										"Cantilever swingarm suspension system",
										"Pro-Link suspension system",
										"Unit Pro-Link syspension system",
										"Uni-Track suspension system",
										"Full floater syspension system"};
	
	public static final int SWINGARM_ANGLE_VARIABLE = 0;
	public static final int VERTICAL_WHEEL_DISPLACEMENT_VARIABLE = 1;
	public static final int SUSPENSION_UNIT_COMPRESSION_VARIABLE = 2;
	public static final int VERTICAL_FORCE_ON_WHEEL_VARIABLE = 3;
	public static final int EQUIVALENT_STIFFNESS_VARIABLE = 4;
	public static final String[] variables = {	"Swingarm angle", 
										"Vertical wheel displacement", 
										"Suspension Unit compression", 
										"Vertical force on rear wheel",
										"Equivalent Stiffness"};
	public static final String[] symbols = {	"B",
												"H",
												"SUC",
												"F",
												"K"};
	
	public static final String[] units = {		"deg",
												"mm",
												"mm",
												"N",
												"N/mm"};
	
	public static SuspensionSystem getSuspensionSystemOfType (int type) {
		switch (type) {
			case SuspensionSystem.CLASSIC_TYPE:
				//return new ClassicSuspensionSystem();
			case SuspensionSystem.CANTILEVER_TYPE:
				return new CantileverSuspensionSystem();
			case SuspensionSystem.PROLINK_TYPE:
				return new ProLinkSuspensionSystem();
			case SuspensionSystem.UNIT_PROLINK_TYPE:
				return new UnitProLinkSuspensionSystem();
			case SuspensionSystem.UNITRACK_TYPE:
				return new UniTrackSuspensionSystem();
			case SuspensionSystem.FULLFLOATER_TYPE:
				//return new FullFloaterSuspensionSystem();
			default:
				return new CantileverSuspensionSystem();
		}
	}
	
public static int getTypeIndexOf(String typeName) {
		for (int i = 0; i < typeNames.length; i++) {
			if (typeNames[i].equals(typeName))
				return i;
		}
		return 0;
	}
	
	public ArrayList<Parameter> getLastLevelParameters() {
		ArrayList<Parameter> allParameters = new ArrayList<Parameter>();
		for (ParameterGroup pg: getParameterGroups()) {
			for (Parameter p: pg.getChildren()) {
				allParameters.add(p);
			}
		}
		return allParameters;
	}
	
	public double getVariableValueAt(int variable, double b) {
		switch (variable) {
			case SuspensionSystem.SWINGARM_ANGLE_VARIABLE:
				return getSwingarmAngleAt(b);
			case SuspensionSystem.VERTICAL_WHEEL_DISPLACEMENT_VARIABLE:
				return getVerticalWheelDisplacementAt(b);
			case SuspensionSystem.SUSPENSION_UNIT_COMPRESSION_VARIABLE:
				return getSuspensionUnitCompressionAt(b);
			case SuspensionSystem.VERTICAL_FORCE_ON_WHEEL_VARIABLE:
				return getVerticalForceAt(b);
			case SuspensionSystem.EQUIVALENT_STIFFNESS_VARIABLE:
				return getEquivalentStiffnessAt(b);
			default:
				System.out.println("Unexistent variable.");
			}
		return 0;
	}
	
	public double getVariableValueAt(int variable) {
		return getVariableValueAt(variable, this.getB());
	}
	
	protected double getAngleFromCats (double dx, double dy) {
		double angle;
		angle = dx != 0? Math.atan(dy/dx): Math.PI/2;
		angle += dx < 0? Math.PI: 0;
		return angle;
	}
	
	
	/*public double getAngleWhere (int varType, double var) {
		
		return 0.0;
	}*/
	
	
	
	/*Métodos a implementar por las subclases*/
	public abstract double getB();
	public abstract void setB(double b);
	public abstract void recalc();
	public abstract double getSwingarmAngleAt(double b);
	public abstract double getVerticalWheelDisplacementAt(double b);
	public abstract double getSuspensionUnitCompressionAt(double b);
	public abstract double getVerticalForceAt(double b);
	public abstract double getEquivalentStiffnessAt(double b);
	
	public abstract double getLowerLimit();
	public abstract double getUpperLimit();
	
	public abstract int getType ();
	
	public abstract ArrayList<ParameterGroup> getParameterGroups();
	
	public abstract SuspensionSystem getCopy ();


}
