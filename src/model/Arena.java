package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Arena {

	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	private final List<Atom> atoms;

	public Arena(List<Atom> atoms) {
		this.atoms = atoms;
	}

	public void doStep() {

		for (Atom atom : atoms) {

			List<Double> partsOfView = new ArrayList<>();

			for (Atom viewableAtom : atoms.stream().filter(
					targetAtom -> targetAtom.getPosition().getDistanceFrom(
							atom.getPosition()) < targetAtom.getSize()
									+ atom.getDistanceOfView()
							&& targetAtom != atom)
					.collect(Collectors.toList())) {

			}

			atom.move(partsOfView.stream().mapToDouble(f -> f).toArray());
		}
	}

	public void draw(Graphics2D target) {

		target.setColor(Color.WHITE);
		target.fillRect(0, 0, WIDTH, HEIGHT);

		for (Atom atom : atoms) {

			target.setColor(Color.GRAY);
			atom.drawVisibility(target);

			target.setColor(Color.RED);
			atom.drawVelocity(target);

			target.setColor(Color.BLACK);
			atom.drawAtom(target);
		}
	}
}
