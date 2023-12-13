package suspensionSystem.parameter;

import java.util.ArrayList;

import javafx.scene.control.TreeItem;


public class ParameterGroup {
	private String name;
	private ArrayList<Parameter> parameters;
	
	public ParameterGroup (String name) {
		this.name = name;
		parameters  = new ArrayList<Parameter>();
		
		//initialize();
	}
	
	public ParameterGroup (String name, String ...parameterNames) {
		this(name);
		parameters = new ArrayList<Parameter>();
		for (String parameterName: parameterNames) {
			parameters.add(new Parameter(parameterName));
		}
		//System.out.println("New parameter group created: " + this.name + "\nTotal parameters: " + this.parameterNames.size());
	}
	
	public ArrayList<Parameter> getChildren() {
		return parameters;
	}
	
	public String getName() {
		return name;
	}
	
	public Parameter getParameter (int i) {
		return parameters.get(i);
	}

	public TreeItem<Parameter> getTreeItem() {
		TreeItem<Parameter> tree = new TreeItem<Parameter>(new Parameter(this.name, false));
		for(Parameter parameter: parameters) {
			tree.getChildren().add(new TreeItem<Parameter>(parameter));
		}
		
		return tree;
	}
	
	/*public void initialize () {
		
	}*/
	

}
