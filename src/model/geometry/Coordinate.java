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

	public Coordinate(Coordinate c) {
		this(c.x, c.y);
	}

	public double getDistanceFrom(Coordinate c) {
		return new Vector(this, c).getLength();
	}

	public Coordinate getCoordinateAfterTranslateBy(Vector v) {
		return new Coordinate(this.x + v.x, this.y + v.y);
	}

	public Coordinate getCoordinateAfterRotateBy(double rad) {
		return new Coordinate(Math.cos(rad) * this.x - Math.sin(rad) * this.y,
				Math.sin(rad) * this.x + Math.cos(rad) * this.y);
	}

	public Coordinate getCoordinateAfterRotate(Coordinate point, double rad) {

		Coordinate result = new Coordinate(this);
		Vector translateVector = new Vector(point, new Coordinate(0));

		result = result.getCoordinateAfterTranslateBy(translateVector);
		result = result.getCoordinateAfterRotateBy(rad);

		translateVector.multiplyLength(-1);

		return result.getCoordinateAfterTranslateBy(translateVector);
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
