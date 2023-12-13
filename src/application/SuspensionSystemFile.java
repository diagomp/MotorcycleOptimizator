package application;

import java.util.ArrayList;

import suspensionSystem.CantileverSuspensionSystem;
import suspensionSystem.ProLinkSuspensionSystem;
import suspensionSystem.SuspensionSystem;

public class SuspensionSystemFile {
	
	private static int numFiles = 0;

	private String name;
	
	private int current;
	private int numHistoryElements;
	SuspensionSystem[] history;
	private int type;
	
	public SuspensionSystemFile (String name, int type) {
		this.name = name;
		this.type = type;
		history = new SuspensionSystem[20];
		current = 0;
		numHistoryElements = 1;
		
		history[current] = SuspensionSystem.getSuspensionSystemOfType(type);
		
	}
	
	public SuspensionSystemFile (String name, SuspensionSystem suspensionSystem) {
		this.name = name;
		this.type = suspensionSystem.getType();
		history = new SuspensionSystem[20];
		current = 0;
		numHistoryElements = 1;
		history[current] = suspensionSystem;
	}
	
	public String getName () {
		return name;
	}
	
	public int getType () {
		return type;
	}
	
	public SuspensionSystem getSuspensionSystem() {
		return history[current];
		
	}
	
	public void undo() {
		//TODO Implementar función undo
		//System.out.println("UNDO: " + name);
		if (current < numHistoryElements - 1)
			current++;
		System.out.println("CURRENT HISTORY POSITION: " + current);
		
	}
	
	public void redo() {
		//TODO Implementar función redo
		if (current > 0)
			current--;
		System.out.println("CURRENT HISTORY POSITION: " + current);
	}
	
	public void modify () {
		System.out.println("----------- MODIFY -----------");
		
		if (current > 0) {
			//history[current - 1] = history[current];
			//current--;
			numHistoryElements -= current - 1;
			history[0] = history[current];
			for (int i = 1; i < numHistoryElements; i++) {
				history[i] = history[i + current - 1].getCopy();
			}
			current = 0;
			
		}
		else {
			if (numHistoryElements < history.length)
				numHistoryElements++;
			
			for (int i = numHistoryElements - 1; i > 0; i--) {
				history[i] = history[i - 1].getCopy();
			}
		}
		System.out.println("CURRENT HISTORY POSITION: " + current);
	}

	public void setSuspensionSystem(SuspensionSystem suspensionSystem) {
		history[0] = suspensionSystem;
		current = 0;
		numHistoryElements = 1;
		
	}
	
}
