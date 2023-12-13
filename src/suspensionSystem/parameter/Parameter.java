package suspensionSystem.parameter;

public class Parameter {
	
	public static final int VALUE = 0;
	public static final int LOWER_LIMIT = 1;
	public static final int HIGHER_LIMIT = 2;
	
	private String name;
	private double value;
	
	private boolean toOptimize;
	private double lowerLimit;
	private double upperLimit;
	
	private boolean definitive;
	
	public Parameter (String name) {
		this(name, true);
		
	}
	
	public Parameter (String name, double value, double lowerLimit, double upperLimit, boolean toOptimize) {
		this(name, value);
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.toOptimize = toOptimize;
	}
	
	public Parameter (String name, double value) {
		this(name);
		this.value = value;
		this.lowerLimit = value*(1-0.35);
		this.upperLimit = value*(1+0.35);
		
		if (value < 0) {
			double aux = this.upperLimit;
			this.upperLimit = this.lowerLimit;
			this.lowerLimit = aux;
			
		}
	}
	
	public Parameter (String name, boolean definitive) {
		this.name = name;
		this.definitive = definitive;
		this.toOptimize = true;
	}
	
	public void setValue (double value) {
		this.value = value;
		
		if (value > upperLimit)
			upperLimit = value;
		if (value < lowerLimit)
			lowerLimit = value;
	}
	
	public void setUpperLimit (double upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public void setLowerLimit (double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	
	public void setToOptimize(boolean toOptimize) {
		this.toOptimize = toOptimize;
		//System.out.println(toString());
	}
	
	
	public String getName () {
		return name;
	}
	
	public double getValue() {
		return value;
	}
	
	public double getUpperLimit () {
		return upperLimit;
	}
	
	public double getLowerLimit () {
		return lowerLimit;
	}
	
	public boolean isToOptimize() {
		return toOptimize;
	}
	
	public boolean isDefinitive () { return definitive; }
	
	public Parameter getCopy () {
		Parameter p = new Parameter(this.getName(), this.getValue());
		p.setLowerLimit(this.getLowerLimit());
		p.setUpperLimit(this.getUpperLimit());
		p.setToOptimize(this.isToOptimize());
		
		return p;
	}
	
	
	@Override
	public String toString () {
		String str = "PARAMETER:\n" + "- Name: " + name + "\n- Value: " + value + "\n";
		str += "- LL: " + lowerLimit + "\n- UL: " + upperLimit + "\n";
		return str;
	}
	
	
	

}
