package optimization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import suspensionSystem.parameter.Parameter;

public class TextfieldCellFactory
		implements Callback<TreeTableColumn<Parameter, String>, TreeTableCell<Parameter, String>> {
	private final int option;
	
	TextfieldCellFactory (int option) {
		super();
		this.option = option;
	}

	@Override
	public TreeTableCell<Parameter, String> call(TreeTableColumn<Parameter, String> param) {
		final TreeTableCell<Parameter, String> cell = new TreeTableCell<Parameter, String>() {
			
			private TextField tf;
			
			@Override
			protected void updateItem(String item, boolean empty) {
		
				super.updateItem(item, empty);
				Parameter param = getTreeTableRow().getItem();
				
				if ((param == null)||(param != null&&(empty||!param.isDefinitive()))) {
					setGraphic(null);
				}
				else {
					//setAlignment(Pos.CENTER);
					
					tf = new TextField();
					tf.setAlignment(Pos.CENTER);
					switch (option) {
					case Parameter.VALUE:
						tf.setText(Double.toString(param.getValue()));
						break;
					case Parameter.LOWER_LIMIT:
						tf.setText(Double.toString(param.getLowerLimit()));
						//if (!param.isToOptimize())
							//tf.setDisable(true);
						break;
					case Parameter.HIGHER_LIMIT:
						tf.setText(Double.toString(param.getUpperLimit()));
						//if (!param.isToOptimize())
							//tf.setDisable(true);
						break;
					}
					
					//cell.getTreeTableRow().
					
					tf.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							switch(option) {
							case Parameter.VALUE:
								try {
									param.setValue(Double.parseDouble(tf.getText()));
								}
								catch (Exception e) {
									tf.setText(Double.toString(param.getValue()));
								}
								break;
								
							case Parameter.LOWER_LIMIT:
								try {
									param.setLowerLimit(Double.parseDouble(tf.getText()));
								}
								catch (Exception e) {
									tf.setText(Double.toString(param.getLowerLimit()));
								}
								break;
							case Parameter.HIGHER_LIMIT:
								try {
									param.setUpperLimit(Double.parseDouble(tf.getText()));
								}
								catch (Exception e) {
									tf.setText(Double.toString(param.getUpperLimit()));
								}
								break;
							}
							
						}
						
					});
					
					
					setGraphic(tf);
				}
			}
		};
		return cell;
	}

}
