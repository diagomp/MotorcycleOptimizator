package GUI;

import java.util.ArrayList;

import optimization.OptimizationConfig;
import application.Session;
import application.SuspensionSystemFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import suspensionSystem.ProLinkSuspensionSystem;
import suspensionSystem.SuspensionSystem;

public class EvaluationMenu extends MenuBar {
	Session session;
	EvaluationWindow ew;

	public EvaluationMenu (Session session, EvaluationWindow ew) {
		super();
		this.session = session;
		this.ew = ew;
		
		
		reload();
	}
	
	public void reload() {
		this.getMenus().clear();
		
		Menu fileMenu = new Menu("_File");
		Menu openedFilesMenu = new Menu("Opened _Files");
		if (session.getOpenedFiles().isEmpty()) {
			openedFilesMenu.setDisable(true);
		}
		else {
			ToggleGroup tg = new ToggleGroup();
			
			ArrayList<RadioMenuItem> rmItems = new ArrayList<RadioMenuItem>();
			for(int i = 0; i < session.getOpenedFiles().size(); i++) {
				rmItems.add(new RadioMenuItem(session.getOpenedFiles().get(i).getName()));
				tg.getToggles().add(rmItems.get(i));
				openedFilesMenu.getItems().add(rmItems.get(i));
				if (session.getCurrentFile().equals(session.getOpenedFiles().get(i))) {
					tg.selectToggle(rmItems.get(i));
				}
			}
			
			tg.selectedToggleProperty().addListener(new ChangeListener <Toggle> () {
				@Override
				public void changed(ObservableValue<? extends Toggle> arg0, Toggle oldToggle, Toggle newToggle) {
					// TODO Auto-generated method stub
					int i = 0;
					for (i = 0; i < rmItems.size(); i++) {
						if (newToggle.equals(rmItems.get(i)))
							break;
					}
					System.out.println("Se ha cambiado el archivo abierto actualmente");
					session.setCurrentFile(i);
					ew.reload();
				}
			});;
			
		}
		
		MenuItem newFileItem = new MenuItem("_New");
		newFileItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		newFileItem.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("Creating new SuspensionSystemFile.");
				SuspensionSystemFile suspensionSystemFile = DialogBox.newSuspensionSystemDialogBox();
				if (suspensionSystemFile != null) {
					session.addFile(suspensionSystemFile);
					ew.reload();
				}
			}
		});
		MenuItem openFileItem = new MenuItem("_Open");
		openFileItem.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("Opening a new SuspensionSystemFile");
				SuspensionSystemFile suspensionSystemFile = DialogBox.openSuspensionSystemDialogBox();
				//if (suspensionSystemFile != null)
					//session.getSuspensionSystemFiles().add(suspensionSystemFile);
			}
			
		});
		openFileItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		MenuItem saveFileItem = new MenuItem("_Save");
		saveFileItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		saveFileItem.setDisable(true);
		MenuItem saveAsItem = new MenuItem("Save _As...");
		fileMenu.getItems().addAll(openedFilesMenu, new SeparatorMenuItem(), newFileItem, openFileItem, new SeparatorMenuItem(), saveFileItem, saveAsItem);
		
		Menu editMenu = new Menu("_Edit");
		MenuItem undoEditItem = new MenuItem("_Undo");
		undoEditItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		undoEditItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("Undo button pressed!");
				SuspensionSystemFile ssf = session.getCurrentFile();
				if (ssf != null)
					ssf.undo();
				ew.reload();
				//ew.reloadGraphics();
				//ew.reloadPlots();
				
			}
			
		});
		
		MenuItem redoEditItem = new MenuItem("_Redo");
		redoEditItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		//redoEditItem.setDisable(true);
		redoEditItem.setOnAction(new EventHandler<ActionEvent> () {

			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("Undo button pressed!");
				SuspensionSystemFile ssf = session.getCurrentFile();
				if (ssf != null)
					ssf.redo();
				ew.reload();
				//ew.reloadGraphics();
				//ew.reloadPlots();
				
			}
			
		});
		editMenu.getItems().addAll(undoEditItem, redoEditItem);
		
		
		/****************************************************************************************************************************/
		Menu toolsMenu = new Menu("_Tools");
		MenuItem runOptimizationItem = new MenuItem ("_Run optimization");
		runOptimizationItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		
		runOptimizationItem.setOnAction(new EventHandler<ActionEvent> () {

			@Override
			public void handle(ActionEvent ae) {
				Stage secondaryStage = new Stage();
				OptimizationConfig optimizationConfiguration = new OptimizationConfig(secondaryStage, ew);
				
				
			}
			
		});
		
		toolsMenu.getItems().add(runOptimizationItem);
		
		
		/****************************************************************************************************************************/
		Menu viewMenu = new Menu("_View");
		Menu xAxisMenu = new Menu("_X Axis");
		Menu yAxisMenu = new Menu("_Y Axis");
		
		ArrayList<RadioMenuItem> rmXVariables = new ArrayList<RadioMenuItem>();
		ArrayList<RadioMenuItem> rmYVariables = new ArrayList<RadioMenuItem>();
		ToggleGroup tg1 = new ToggleGroup ();
		ToggleGroup tg2 = new ToggleGroup ();
		
		for (int i = 0; i < SuspensionSystem.variables.length; i++) {
			rmXVariables.add(new RadioMenuItem(SuspensionSystem.variables[i] + ", " + SuspensionSystem.symbols[i] + " (" + SuspensionSystem.units[i] + ")"));
			tg1.getToggles().add(rmXVariables.get(i));
			rmYVariables.add(new RadioMenuItem(SuspensionSystem.variables[i] + ", " + SuspensionSystem.symbols[i] + " (" + SuspensionSystem.units[i] + ")"));
			tg2.getToggles().add(rmYVariables.get(i));
		}
		tg1.selectToggle(rmXVariables.get(SuspensionSystem.VERTICAL_WHEEL_DISPLACEMENT_VARIABLE));
		tg2.selectToggle(rmYVariables.get(SuspensionSystem.EQUIVALENT_STIFFNESS_VARIABLE));
		
		xAxisMenu.getItems().addAll(rmXVariables);
		yAxisMenu.getItems().addAll(rmYVariables);
		
		
		
		tg1.selectedToggleProperty().addListener(new ChangeListener <Toggle> () {
			@Override
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle oldToggle, Toggle newToggle) {
				int i = 0;
				for (i = 0; i < tg1.getToggles().size(); i++) {
					
					if (tg1.getToggles().get(i).equals(newToggle))
						break;	
				}
				PlotsPane.xVariable = i;
				ew.reloadPlots();
				//System.out.println("\nToggle 1: " + i);
				
			}
		});
		
		tg2.selectedToggleProperty().addListener(new ChangeListener <Toggle> () {
			@Override
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle oldToggle, Toggle newToggle) {
				int i = 0;
				for (i = 0; i < tg2.getToggles().size(); i++) {
					
					if (tg2.getToggles().get(i).equals(newToggle))
						break;	
				}
				PlotsPane.yVariable = i;
				ew.reloadPlots();
				//System.out.println("\nToggle 1: " + i);
				
			}
		});
		
		viewMenu.getItems().addAll(xAxisMenu, yAxisMenu);
		
		
		
		
		this.getMenus().addAll(fileMenu, editMenu, toolsMenu, viewMenu);
	}
}
