package model.geometry;

public class Coordinate {

	protected double x;
	protected double y;

	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate(double i) {
		this(i, i);
	}

	public double getDistanceFrom(Coordinate c) {
		return new Vector(this, c).getLength();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void set(double i) {
		this.x = i;
		this.y = i;
	}
}
