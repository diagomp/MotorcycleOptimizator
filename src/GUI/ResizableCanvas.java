package GUI;

import java.util.ArrayList;

import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class ResizableCanvas extends Canvas {
	
	private ArrayList<Drawable> drawableObjects;
	private GraphicsContext gc;
	private double xTranslate;
	private double yTranslate;
	
	public ResizableCanvas () {
		super();
		drawableObjects = new ArrayList<Drawable>();
		this.setRotationAxis(new Point3D(1, 0, 0));
		this.setRotate(180);
		gc = this.getGraphicsContext2D();
	}
	
	@Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double maxHeight(double width) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double maxWidth(double height) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double minWidth(double height) {
        return 1D;
    }

    @Override
    public double minHeight(double width) {
        return 1D;
    }

    @Override
    public void resize(double width, double height) {
    	gc.translate((width - getWidth())/2, (height - getHeight())/2);
        this.setWidth(width);
        this.setHeight(height);
    }
    
    public ArrayList<Drawable> getChildren() {
    	return drawableObjects;
    	
    }
    
    public void translate (double x, double y) {
    	//gc.translate(x, y);
    	this.xTranslate += x;
    	this.yTranslate += y;
    	draw();
    }
    
    private void drawAxisSystem() {
    	//Ejes de coordenadas
    	double arrowAngle = Math.PI/6;
    	double arrowLength = 40;
    	double arrowRate = 0.22;
    	gc.setStroke(Color.rgb(0, 74, 124));
		gc.setLineWidth(2);
		gc.strokeLine(0, 0, arrowLength, 0);
		gc.strokeLine(arrowLength, 0, arrowLength*(1-arrowRate*Math.cos(arrowAngle)), arrowLength*arrowRate*Math.sin(arrowAngle));
		gc.strokeLine(arrowLength, 0, arrowLength*(1-arrowRate*Math.cos(arrowAngle)), -arrowLength*arrowRate*Math.sin(arrowAngle));
		gc.strokeLine(0, 0, 0, arrowLength);
		gc.strokeLine(0, arrowLength, arrowLength*arrowRate*Math.sin(arrowAngle), arrowLength*(1-arrowRate*Math.cos(arrowAngle)));
		gc.strokeLine(0, arrowLength, -arrowLength*arrowRate*Math.sin(arrowAngle), arrowLength*(1-arrowRate*Math.cos(arrowAngle)));
		
    }
    
    private void drawGrid () {
    	//Rejilla
    	int divisionSize = 50;
    	gc.setStroke(Color.gray(0.85));
    	gc.setLineWidth(0.5);
    	double i = 0;
    	while (i <= getWidth()/2 - xTranslate) {
    		gc.strokeLine(i, -getHeight()/2 - yTranslate, i, getHeight()/2 - yTranslate);
    		i += divisionSize;
    	}
    	
    	i = 0;
    	while (i >= -getWidth()/2 - xTranslate/0.75) {
    		gc.strokeLine(i, -getHeight()/2 - yTranslate, i, getHeight()/2 - yTranslate);
    		i -= divisionSize;
    	}
    	
    	i = 0;
    	while (i <= getHeight()/2 - yTranslate) {
    		gc.strokeLine(-getWidth()/2 - xTranslate, i, getWidth()/2 - xTranslate, i);
    		i += divisionSize;
    	}
    	i = 0;
    	while (i >= -getHeight()/2 - yTranslate) {
    		gc.strokeLine(-getWidth()/2 - xTranslate, i, getWidth()/2 - xTranslate, i);
    		i -= divisionSize;
    	}
    }
    
    public void draw () {
    	gc.clearRect(-getWidth()/2, -getHeight()/2, getWidth(), getHeight());
    	//gc.scale(0.75, 0.75);
    	gc.translate(xTranslate, yTranslate);
 
    	
    	//drawGrid();
    	
    	
    	
    	
    	//Gradient background
    	Stop[] stops = {new Stop(0, Color.WHITE), new Stop(1, Color.TRANSPARENT)};
    	LinearGradient lg = new LinearGradient(0, 1, 0 , 0, true, CycleMethod.NO_CYCLE, stops);
    	gc.setFill(lg);
    	gc.fillRect(-getWidth()/2-xTranslate, getHeight()/2-yTranslate-getHeight()/8, getWidth(), getHeight()/8);
    	gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops));
    	gc.fillRect(-getWidth()/2-xTranslate, -getHeight()/2-yTranslate, getWidth(), getHeight()/8);
    	
    	//Default properties
    	gc.setFill(Color.TRANSPARENT);
    	gc.setStroke(Color.BLACK);
    	
    	for (Drawable i: drawableObjects) {
    		gc.setLineWidth(2);
    		i.draw(gc);
    	}
    	
    	drawAxisSystem();
    	
    	
    	gc.translate(-xTranslate, -yTranslate);
    	//gc.scale(1/0.75, 1/0.75);
    	
    }

}
