package model;

import java.awt.Graphics2D;
import java.util.Random;

import model.geometry.Coordinate;
import model.geometry.Vector;

public class Atom {

	private final Coordinate position;
	private final Vector velocity;
	private long timeOfPreviousMove;
	private double size;
	private double distanceOfView;

	public Atom() {

		Random random = new Random();

		this.position = new Coordinate(random.nextInt(Arena.WIDTH),
				random.nextInt(Arena.HEIGHT));
		this.velocity = new Vector(0);

		this.size = 5;
		this.distanceOfView = 25;

		this.timeOfPreviousMove = System.currentTimeMillis();
	}

	public void move() {
		// TODO make a decision

		// TODO recalculate velocity

		// set position
		long currentTime = System.currentTimeMillis();
		double seconds = (currentTime - timeOfPreviousMove) / 1000.0;
		timeOfPreviousMove = currentTime;
		position.set(position.getX() + velocity.getX() * seconds,
				position.getY() + velocity.getY() * seconds);
		if (position.getX() < 0) {
			position.setX(0);
		}
		if (position.getX() >= Arena.WIDTH) {
			position.setX(Arena.WIDTH - 1);
		}
		if (position.getY() < 0) {
			position.setY(0);
		}
		if (position.getY() >= Arena.HEIGHT) {
			position.setY(Arena.HEIGHT - 1);
		}

		// decrease velocity (friction)
		velocity.decreaseLength(100 * seconds);

	}

	public void drawVisibility(Graphics2D target) {
		target.drawOval((int) Math.round(position.getX() - distanceOfView),
				(int) Math.round(position.getY() - distanceOfView),
				(int) Math.round(2 * distanceOfView),
				(int) Math.round(2 * distanceOfView));
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
}
