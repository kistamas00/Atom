package model.geometry;

public class Vector extends Coordinate {

	public Vector(double x, double y) {
		super(x, y);
	}

	public Vector(double i) {
		super(i);
	}

	public Vector(Coordinate a, Coordinate b) {
		super(b.x - a.x, b.y - a.y);
	}

	public void decreaseLength(double count) {

		if (count > getLength()) {

			set(0);

		} else {

			double rate = count / getLength();
			double newLengthRate = 1 - rate;
			multiplyLength(newLengthRate);
		}
	}

	public double getLength() {
		return Math.sqrt(x * x + y * y);
	}

	public double getAngleWith(Vector v) {
		return Math.acos((this.x * v.x + this.y * v.y) / this.getLength()
				/ v.getLength());
	}

	public void multiplyLength(double count) {
		this.x *= count;
		this.y *= count;
	}

	public void add(Vector v) {
		this.x += v.x;
		this.y += v.y;
	}
}
