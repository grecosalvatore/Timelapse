import java.awt.Point;
/**
 * this class manages the segment and the distance between a point and the segment 
 * (need this to calc the bright compensation) and have two parameter
 * point startPoint is the first point of the segment
 * point endPoint is the last point of the segment
 */
public class Segment {
	private Point startPoint; //first point of the segment
	private Point endPoint;  //last point of the segment
    
	/**
     * 
     * @param a  first point of the segment
     * @param b  last point of the segment
     */
	public Segment(Point a, Point b) {
		this.startPoint = a;
		this.endPoint = b;
	}
 
	/**
	 * this method return the y in a point of the segment with the x passed as a parameter
	 * @param x is the x of the point
	 * @return double y is the y in the point x in the segment
	 */
	public double getYSegment(int x) {
		// return the y of the point with x passed as a parameter of the segment
		double y;
		double a = 0;
		double b = 0;
		double c = 0;
		a = (endPoint.getY() - startPoint.getY());
		b = (endPoint.getX() - startPoint.getX());
		c = (x - startPoint.getX());
		y = (a / b * c) + startPoint.getY();

		return y;
	}

	/**
	 * this method calculate the distance between the y of a point passed as a parameter and 
	 * the y with the same x of the point in the segment
	 * @param p is a point to calculated the distance
	 * @return double d is the distance between the y of the point and the y in the x of the segment
	 */
	public double distance(Point p) {
		double d; //distance between a point and a line
		d = (p.getY() - getYSegment(p.x)); //calc distance between the y of the point and the segment
		d = d * 100; 
		d = Math.round(d); //approximating
		d = d / 100;
		return d * (-1);
	}

}
