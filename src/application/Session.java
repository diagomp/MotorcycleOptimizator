package application;

import java.util.ArrayList;

import suspensionSystem.SuspensionSystem;

public class Session {
	
	private ArrayList<SuspensionSystemFile> openedFiles;
	private int currentFile;
	
	private double windowWidth;
	private double windowHeight;
	private double dividerPosition;
	
	
	public Session () {
		openedFiles = new ArrayList<SuspensionSystemFile>();
		currentFile = -1;
		
		windowWidth = 820;
		windowHeight = 560;
		dividerPosition = 0.25;
		
	}
	
	public final ArrayList<SuspensionSystemFile> getOpenedFiles () {
		return openedFiles;
	}
	
	public SuspensionSystemFile getCurrentFile () {
		return currentFile >= 0? openedFiles.get(currentFile): null;
	}
	
	public void addFile (SuspensionSystemFile newFile) {
		openedFiles.add(newFile);
		currentFile = openedFiles.size()-1;
	}

	public void setCurrentFile(int currentFile) {
		this.currentFile = currentFile;
		
	}
	
	public double getWidth () {
		return windowWidth;
	}
	
	public double getHeight () {
		return windowHeight;
	}
	
	public void setWidth (double width) {
		this.windowWidth = width;
	}
	
	public void setHeight (double height) {
		this.windowHeight = height;
	}
	
	public double getDividerPosition () {
		return dividerPosition;
	}
	
	public void setDividerPosition (double dividerPosition) {
		this.dividerPosition = dividerPosition;
	}
	
	public void modify () {
		openedFiles.get(currentFile).modify();
	}

	
}
