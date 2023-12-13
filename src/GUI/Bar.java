package GUI;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.*;
//import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.lang.Double;
import GUI.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Bar implements Drawable {
	static double size = 40;
	//Posición de la barra
	private double x, y, a;
	private String name;
	
	//Datos geométricos de la barra
	private ArrayList<Point2D.Double> joints;
	
	public Bar () {
		this(new double[0]);
	}
	
	public Bar (double ... arrayList) {
		x = 0;
		y = 0;
		a = 0;
		
		setJoints(arrayList);
	}
	
	
	public Point2D.Double localToAbsoluteCoordinates (Point2D.Double local) {
		Point2D.Double absolut = new Point2D.Double();
		absolut.x = this.x + local.x*Math.cos(this.a) - local.y*Math.sin(this.a);
		absolut.y = this.y + local.x*Math.sin(this.a) + local.y*Math.cos(this.a);
		return absolut;
	}
	


	/*Getter methods.*/
	public ArrayList<Point2D.Double> getJoints() {
		return joints;
	}
	
	public String getName () {
		return name;
	}
	
	public double getX () {
		return x;
	}
	
	public double getY () {
		return y;
	}
	
	public double getAngle () {
		return a;
	}
	
	
	
	/*Setter methods.*/
	public void setJoints(double[] points) {
		joints = new ArrayList<Point2D.Double>();
		for (int i = 1; i < points.length; i+=2) {
			joints.add(new Point2D.Double(points[i-1], points [i]));
		}
	}

	public void setJoints(ArrayList<Point2D.Double> joints) {
		this.joints = joints;
	}
	
	public void addJoint( double x, double y) {
		this.joints.add(new Point2D.Double(x, y));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setX (double x) {
		this.x = x;
	}
	
	public void setY (double y) {
		this.y = y;
	}
	
	public void setAngle (double a) {
		this.a = a;
	}
	
	
	/*Other methods.*/
	@Override
	public String toString () {
		String str = "Bar called: " + name + "\n";
		for (Point2D.Double p: joints) {
			str += "\t(" + p.getX() + ", " + p.getY() + ")\n";
		}
		return str;
	}
	
	

	
	@Override
	/*From interface 'Drawable'*/
	public void draw(GraphicsContext gc) {
		/*Polygon shape = new Polygon();
		shape.getrPoints*/
		int len = joints.size();
		double[] xPoints = new double[len];
		double[] yPoints = new double[len];
		for (int i = 0; i < len; i++) {
			Point2D.Double joint = localToAbsoluteCoordinates(joints.get(i));
			xPoints[i] = joint.getX();
			yPoints[i] = joint.getY();
		}
		
		gc.strokePolygon(xPoints, yPoints, len);
		//gc.fillPolygon(xPoints, yPoints, len);
		
		//double size = 30;
		for (int i = 0; i < len; i++) {
			gc.setFill(Color.WHITE);
			gc.fillOval(xPoints[i]-size/8, yPoints[i]-size/8, size/4, size/4);
			gc.strokeOval(xPoints[i]-size/8, yPoints[i]-size/8, size/4, size/4);
		}
		
	}
	
	
	
	
	public static void drawFixPoint (GraphicsContext gc, double x, double y, int orientation) {
		double angle = 0;
		switch (orientation) {
		case 0:
			angle = 180;
			break;
		case 1:
			angle = 90;
			break;
		case 2:
			angle = 0;
			break;
		case 3:
			angle = 270;
			break;
		}
		
		gc.translate(x, y);
		gc.rotate(angle);
		
		double size = 40;
		/*double[] xPoints = {0, size*Math.cos(Math.PI/3), -size*Math.cos(Math.PI/3)};
		double[] yPoints = {0, -size*Math.sin(Math.PI/3) , -size*Math.sin(Math.PI/3)};
		gc.strokePolygon(xPoints, yPoints, 3);*/
		gc.setFill(Color.WHITE);
		gc.fillOval(-size/8, -size/8, size/4, size/4);
		gc.strokeOval(-size/8, -size/8, size/4, size/4);
		gc.strokeOval(-size/4, -size/4, size/2, size/2);
		
		gc.rotate(-angle);
		gc.translate(-x, -y);
	}
	
	public static void drawSpring (GraphicsContext gc, final double x1, final double y1, double x2, double y2) {
		double len = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
		double angle; 
		if (x2 - x1 != 0) {
			angle = Math.atan((y2 - y1)/(x2 - x1));
			angle += x2 - x1 < 0? Math.PI: 0;
		}
		else {
			angle = y2 - y1 >= 0? Math.PI/2: -Math.PI/2;
		}
		
		gc.translate(x1, y1);
		gc.rotate(angle*180/Math.PI);
		
		
		int n = 20;
		double ratio = 1;
		double l = len - 2*size;
		double b = size/2;
		
		gc.strokeLine(0, 0, (len - l)/2, 0);
		gc.strokeLine(len-(len - l)/2, 0, len, 0);
		
		
		for (int i = 0; i < n; i++) {
			switch (i%4) {
			case 0:
				gc.strokeLine((len - l)/2 + (1-ratio)*l/2 + i*ratio*l/n, 0, (len - l)/2 + (1-ratio)*l/2 + (i+1)*ratio*l/n, b);
				break;
			case 1:
				gc.strokeLine((len - l)/2 + (1-ratio)*l/2 + i*ratio*l/n, b, (len - l)/2 + (1-ratio)*l/2 + (i+1)*ratio*l/n, 0);
				break;
			case 2:
				gc.strokeLine((len - l)/2 + (1-ratio)*l/2 + i*ratio*l/n, 0, (len - l)/2 + (1-ratio)*l/2 + (i+1)*ratio*l/n, -b);
				break;
			case 3:
				gc.strokeLine((len - l)/2 + (1-ratio)*l/2 + i*ratio*l/n, -b, (len - l)/2 + (1-ratio)*l/2 + (i+1)*ratio*l/n, 0);
				break;
			}
		}
		//double size = 40;
		gc.fillOval(-size/8, -size/8, size/4, size/4);
		gc.strokeOval(-size/8, -size/8, size/4, size/4);
		gc.fillOval(len-size/8, -size/8, size/4, size/4);
		gc.strokeOval(len-size/8, -size/8, size/4, size/4);
		
		gc.rotate(-angle*180/Math.PI);
		gc.translate(-x1, -y1);
		
		
	}
}
