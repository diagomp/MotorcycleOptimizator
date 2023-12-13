package optimization;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import suspensionSystem.parameter.Parameter;
import suspensionSystem.parameter.ParameterGroup;

public class ParameterSelection {

	public ArrayList<ParameterGroup> groups;
	OptimizationConfig optimizationConfig;
	
	public ParameterSelection (ArrayList<ParameterGroup> arrayList, OptimizationConfig optimizationConfig) {
		//groups = new ArrayList<ParameterGroup>();
		this.groups = arrayList;
		this.optimizationConfig = optimizationConfig;
		/*groups.add(new ParameterGroup("Chassis", "O2x", "O2y", "O4x", "O4y"));
		groups.add(new ParameterGroup("Swinging-arm", "b", "xb", "yb"));
		//groups.add(new ParameterGroup("Rocker", "r", "xr", "yr"));
		//groups.add(new ParameterGroup("Link", "l"));
		groups.add(new ParameterGroup("Suspension Unit", "ln", "lm", "k"));
		groups.add(new ParameterGroup("Wheel", "r"));*/
	}
	
	public ParameterSelection (ArrayList<ParameterGroup> groups) {
		this.groups = groups;
	}
	
	public Pane getGUI() {
		VBox vbox = new VBox();
		vbox.setSpacing(10);
        vbox.setPadding(new Insets(15, 15, 15, 15));
		
		TreeTableView<Parameter> tree = new TreeTableView<Parameter>();
		tree.setShowRoot(false);
		//Name column
		TreeTableColumn<Parameter, String> parameterNameColumn = new TreeTableColumn<>("Parameter");
		parameterNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		
		//To Optimize column
		TreeTableColumn<Parameter, Void> toOptimizeColumn = new TreeTableColumn<>("To optimize");
		toOptimizeColumn.setCellFactory(new CheckboxCellFactory());
		
		//Value column
		TreeTableColumn<Parameter, String> valueColumn = new TreeTableColumn<>("Value");
		valueColumn.setCellFactory(new TextfieldCellFactory(Parameter.VALUE));

		TreeTableColumn<Parameter, String> lowerValueColumn = new TreeTableColumn<>("Lower limit");
		lowerValueColumn.setCellFactory(new TextfieldCellFactory(Parameter.LOWER_LIMIT));
		
		TreeTableColumn<Parameter, String> higherValueColumn = new TreeTableColumn<>("Higher limit");
		higherValueColumn.setCellFactory(new TextfieldCellFactory(Parameter.HIGHER_LIMIT));
		
		//tree.getColumns().addAll(parameterNameColumn, toOptimizeColumn, valueColumn, lowerValueColumn, higherValueColumn);
		tree.getColumns().add(parameterNameColumn);
		tree.getColumns().add(toOptimizeColumn);
		tree.getColumns().add(valueColumn);
		tree.getColumns().add(lowerValueColumn);
		tree.getColumns().add(higherValueColumn);
		
		TreeItem<Parameter> root = new TreeItem<Parameter>(new Parameter("Parameters", false));
		
		for (ParameterGroup group: groups) {
			root.getChildren().add(group.getTreeItem());
		}
		
		tree.setRoot(root);
		
		
		Button runButton = new Button ("Run optimization");
		runButton.setOnAction(new EventHandler<ActionEvent> () {

			@Override
			public void handle(ActionEvent ae) {
				//System.out.println("Runing optimization");
				optimizationConfig.runOptimization();
				
			}
			
		});
		HBox hbox = new HBox(runButton);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		
		
		
		vbox.getChildren().addAll(tree, hbox);
		return vbox;
	}

}
