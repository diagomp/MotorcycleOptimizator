package optimization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import suspensionSystem.parameter.Parameter;

public class CheckboxCellFactory implements Callback<TreeTableColumn<Parameter, Void>, TreeTableCell<Parameter, Void>>{

	@Override
	public TreeTableCell<Parameter, Void> call(TreeTableColumn<Parameter, Void> arg0) {
		final TreeTableCell<Parameter, Void> cell = new TreeTableCell<Parameter, Void> () {


			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				Parameter param = getTreeTableRow().getItem();
				
				if ((param == null)||(param != null&&(empty||!param.isDefinitive()))) {
					setGraphic(null);
				}
				else {
					setAlignment(Pos.CENTER);
					
					CheckBox checkbox = new CheckBox();
					checkbox.selectedProperty().addListener(new ChangeListener<Boolean> () {

						@Override
						public void changed(ObservableValue<? extends Boolean> observable, Boolean preValue,
								Boolean newValue) {
							param.setToOptimize(newValue);
							System.out.println("Parameter " + param.getName() + (param.isToOptimize()? " will be used for optimization.": " wont be used for optimization"));
							
						}
						
					});
					
					if (param.isToOptimize()) {
						checkbox.setSelected(true);
					}
					else {
						checkbox.setSelected(false);
					}
					setGraphic(checkbox);
				}
				
				//System.out.println(param);
			}		
		};
		return cell;
	}

}
