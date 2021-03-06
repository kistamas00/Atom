package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import controller.NeuralNetwork;
import model.geometry.Coordinate;
import model.geometry.Vector;

public class Atom {

	public static final int NUMBER_OF_VIEW_RAYS = 50;

	private static final double DEFAULT_SIZE = 5;
	private static final boolean DEFAULT_LIVE_STATE = false;
	private static final double DEFAULT_DISTANCE_OF_VIEW = 50;
	private static final double MAX_ACCELERATION = 200;

	private final Coordinate position;
	private final Vector velocity;
	private double[] lastView;
	private long timeOfPreviousMove;
	private double size;
	private double distanceOfView;
	private NeuralNetwork neuralNetwork;

	public Atom() {
		this(DEFAULT_LIVE_STATE, DEFAULT_SIZE, null);
	}

	public Atom(boolean isAlive) {
		this(isAlive, DEFAULT_SIZE, null);
	}

	public Atom(double size) {
		this(DEFAULT_LIVE_STATE, size, null);
	}

	public Atom(double[] dna) {
		this(true, DEFAULT_SIZE, dna);
	}

	public Atom(boolean isAlive, double size, double[] dna) {

		Random random = new Random();

		this.position = new Coordinate(random.nextInt(Arena.WIDTH),
				random.nextInt(Arena.HEIGHT));
		this.velocity = new Vector(0);

		this.size = size;
		this.distanceOfView = DEFAULT_DISTANCE_OF_VIEW;

		if (isAlive) {

			if (dna == null) {

				this.neuralNetwork = new NeuralNetwork(NUMBER_OF_VIEW_RAYS,
						(int) Math.round(NUMBER_OF_VIEW_RAYS / 2.0),
						(int) Math.round(NUMBER_OF_VIEW_RAYS / 2.0), 2);

			} else {

				this.neuralNetwork = new NeuralNetwork(dna, NUMBER_OF_VIEW_RAYS,
						(int) Math.round(NUMBER_OF_VIEW_RAYS / 2.0),
						(int) Math.round(NUMBER_OF_VIEW_RAYS / 2.0), 2);
			}
		}

		this.timeOfPreviousMove = System.currentTimeMillis();
	}

	public boolean move(double[] view) {

		final long currentTime = System.currentTimeMillis();
		final double seconds = (currentTime - timeOfPreviousMove) / 1000.0;
		timeOfPreviousMove = currentTime;

		if (view.length != NUMBER_OF_VIEW_RAYS) {
			throw new IllegalStateException(
					"View parameter doesn't match the number of viewable fields!");
		} else {
			lastView = view;
		}

		if (isIntelligent()) {

			// make a decision
			neuralNetwork.setInputs(view);
			double[] outputs = neuralNetwork.getOutputs();

			if (outputs.length != 2) {
				throw new IllegalStateException(
						"Number of neural network outputs doesn't two!");
			}

			// recalculate velocity
			final double maxSpeedDiff = MAX_ACCELERATION * seconds;
			velocity.set(velocity.getX() + maxSpeedDiff * outputs[0],
					velocity.getY() + maxSpeedDiff * outputs[1]);
		} else {
			throw new UnsupportedOperationException(
					"Brainless atom can't move!");
		}

		// set position
		position.set(position.getX() + velocity.getX() * seconds,
				position.getY() + velocity.getY() * seconds);

		if (position.getX() < 0 || position.getX() >= Arena.WIDTH
				|| position.getY() < 0 || position.getY() >= Arena.HEIGHT) {

			return false;
		}

		if (position.getX() < 0) {
			position.setX(0);
			velocity.setX(0);
		}
		if (position.getX() >= Arena.WIDTH) {
			position.setX(Arena.WIDTH - 1);
			velocity.setX(0);
		}
		if (position.getY() < 0) {
			position.setY(0);
			velocity.setY(0);
		}
		if (position.getY() >= Arena.HEIGHT) {
			position.setY(Arena.HEIGHT - 1);
			velocity.setY(0);
		}

		// decrease velocity (friction)
		velocity.decreaseLength(100 * seconds);

		return true;

	}

	public void eat(Atom a) {

		double area = this.size * this.size * Math.PI;
		double targetArea = a.size * a.size * Math.PI;

		area += targetArea;
		a.die();

		double newSize = Math.sqrt(area / Math.PI) * 1.05;

		if (newSize > 100) {
			newSize = 100;
		}

		this.distanceOfView += newSize - this.size;
		this.size = newSize;
	}

	private void die() {

		// String logMessage = this + " has dead!";
		//
		// if (!isIntelligent()) {
		// logMessage = "(" + logMessage + ")";
		// } else {
		// logMessage = " " + logMessage;
		// }
		//
		// System.out.println(logMessage);
	}

	public Coordinate[] getPointsOfView() {

		Coordinate[] result = new Coordinate[NUMBER_OF_VIEW_RAYS];

		double sectorAngleInRadian = Math
				.toRadians(360.0 / NUMBER_OF_VIEW_RAYS);
		Coordinate pointOfCircumfence = position.getCoordinateAfterTranslateBy(
				new Vector(0, -this.distanceOfView));
		for (int i = 0; i < NUMBER_OF_VIEW_RAYS; i++) {
			result[i] = pointOfCircumfence;
			pointOfCircumfence = pointOfCircumfence.getCoordinateAfterRotate(
					this.position, sectorAngleInRadian);
		}

		return result;
	}

	public void drawVisibility(Graphics2D target) {

		if (!isIntelligent()) {
			return;
		}

		target.drawOval((int) Math.round(position.getX() - distanceOfView),
				(int) Math.round(position.getY() - distanceOfView),
				(int) Math.round(2 * distanceOfView),
				(int) Math.round(2 * distanceOfView));

		Color originalColor = target.getColor();
		Coordinate[] pointsOfView = getPointsOfView();
		for (int i = 0; i < pointsOfView.length; i++) {

			Coordinate pointOfView = pointsOfView[i];

			if (lastView != null && lastView[i] != 0) {
				if (lastView[i] > 0) {
					target.setColor(Color.YELLOW);
				} else {
					target.setColor(Color.ORANGE);
				}
			}

			target.drawLine((int) Math.round(position.getX()),
					(int) Math.round(position.getY()),
					(int) Math.round(pointOfView.getX()),
					(int) Math.round(pointOfView.getY()));

			target.setColor(originalColor);
		}
	}

	public void drawVelocity(Graphics2D target) {
		target.drawLine((int) Math.round(position.getX()),
				(int) Math.round(position.getY()),
				(int) Math.round(position.getX() + velocity.getX()),
				(int) Math.round(position.getY() + velocity.getY()));
	}

	public void drawAtom(Graphics2D target) {
		target.fillOval((int) Math.round(position.getX() - size),
				(int) Math.round(position.getY() - size),
				(int) Math.round(2 * size), (int) Math.round(2 * size));
	}

	public double getDistanceOfView() {
		return distanceOfView;
	}

	public Coordinate getPosition() {
		return position;
	}

	public double getSize() {
		return size;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public boolean isIntelligent() {
		return neuralNetwork != null;
	}

	public double[] getDNA() {

		if (!isIntelligent()) {
			throw new UnsupportedOperationException(
					"Brainless atom hasn't DNA!");
		}

		double[] result = neuralNetwork.getDNA();

		return result;
	}
}
