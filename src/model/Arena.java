package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import model.geometry.Coordinate;
import model.geometry.Vector;

public class Arena {

	public static final int WIDTH = 1400;
	public static final int HEIGHT = 700;
	private final Set<Atom> intelligentAtoms;
	private final Set<Atom> brainlessAtoms;
	private final Set<Atom> allAtoms;

	public Arena(Collection<? extends Atom> atoms) {

		Set<Atom> tmpSet = new HashSet<>(atoms);

		this.intelligentAtoms = tmpSet.stream().filter(Atom::isIntelligent)
				.collect(Collectors.toSet());
		tmpSet.removeAll(this.intelligentAtoms);
		this.brainlessAtoms = new HashSet<>(tmpSet);

		this.allAtoms = new HashSet<>();
		this.allAtoms.addAll(intelligentAtoms);
		this.allAtoms.addAll(brainlessAtoms);

	}

	public void doStep() {

		Set<Atom> killedAtoms = new HashSet<>();

		// move every living atom
		for (Atom atom : intelligentAtoms) {

			double[] atomInput = new double[Atom.NUMBER_OF_VIEW_RAYS];
			Arrays.fill(atomInput, 0);

			// viewable atoms
			for (Atom viewableAtom : allAtoms.stream().filter(
					targetAtom -> targetAtom.getPosition().getDistanceFrom(
							atom.getPosition()) < targetAtom.getSize()
									+ atom.getDistanceOfView()
							&& targetAtom != atom)
					.collect(Collectors.toList())) {

				Coordinate[] pointsOfView = atom.getPointsOfView();
				for (int i = 0; i < pointsOfView.length; i++) {

					Coordinate pointOfView = pointsOfView[i];

					final Vector dirVec = new Vector(atom.getPosition(),
							pointOfView);
					final Vector norVec = new Vector(-dirVec.getY(),
							dirVec.getX());
					final double A = norVec.getX();
					final double B = norVec.getY();
					final double C = -(A * pointOfView.getX()
							+ B * pointOfView.getY());
					final double d = Math
							.abs(A * viewableAtom.getPosition().getX()
									+ B * viewableAtom.getPosition().getY() + C)
							/ Math.sqrt(A * A + B * B);

					double viewAngleDeg = Math.toDegrees(
							dirVec.getAngleWith(new Vector(atom.getPosition(),
									viewableAtom.getPosition())));

					if (d <= viewableAtom.getSize() && viewAngleDeg < 90) {

						double distanceRate = atom.getPosition()
								.getDistanceFrom(viewableAtom.getPosition())
								/ atom.getDistanceOfView();

						if (distanceRate > 1) {
							distanceRate = 1;
						}

						if (viewableAtom.getSize() > atom.getSize()) {
							distanceRate *= -1;
						} else if (viewableAtom.getSize() == atom.getSize()) {
							distanceRate = 0;
						}

						if (atomInput[i] == 0 || Math.abs(atomInput[i]) > Math
								.abs(distanceRate)) {
							atomInput[i] = distanceRate;
						}
					}
				}
			}

			// viewable walls
			Coordinate[] pointsOfView = atom.getPointsOfView();
			for (int i = 0; i < pointsOfView.length; i++) {

				final Coordinate pointOfView = pointsOfView[i];
				final Set<Double> distances = new HashSet<>();

				if (pointOfView.getX() < 0) {
					distances.add(atom.getPosition().getX());
				}
				if (pointOfView.getX() >= Arena.WIDTH) {
					distances.add(Arena.WIDTH - 1 - atom.getPosition().getX());
				}
				if (pointOfView.getY() < 0) {
					distances.add(atom.getPosition().getY());
				}
				if (pointOfView.getY() >= Arena.HEIGHT) {
					distances.add(Arena.HEIGHT - 1 - atom.getPosition().getY());
				}

				if (distances.size() > 0) {

					final double minDistance = distances.stream()
							.collect(Collectors
									.summarizingDouble(Double::doubleValue))
							.getMin();
					double distanceRate = minDistance
							/ atom.getDistanceOfView();

					if (distanceRate > 1) {
						distanceRate = 1;
					}

					distanceRate *= -1;

					if (atomInput[i] == 0 || Math.abs(atomInput[i]) > Math
							.abs(distanceRate)) {
						atomInput[i] = distanceRate;
					}
				}
			}

			if (!atom.move(atomInput)) {
				killedAtoms.add(atom);
			}
		}

		// remove dead atoms
		for (Atom atom : allAtoms) {

			allAtoms.forEach(targetAtom -> {

				double minSize;
				double maxSize;

				if (atom.getSize() < targetAtom.getSize()) {
					minSize = atom.getSize();
					maxSize = targetAtom.getSize();
				} else {
					minSize = targetAtom.getSize();
					maxSize = atom.getSize();
				}

				if (targetAtom.getPosition().getDistanceFrom(
						atom.getPosition()) <= maxSize - minSize + 5
						&& atom.getSize() > targetAtom.getSize()
						&& !killedAtoms.contains(targetAtom)
						&& !killedAtoms.contains(atom) && targetAtom != atom) {

					atom.eat(targetAtom);
					killedAtoms.add(targetAtom);
				}
			});
		}

		intelligentAtoms.removeAll(killedAtoms);
		brainlessAtoms.removeAll(killedAtoms);
		allAtoms.removeAll(killedAtoms);
	}

	public void draw(Graphics2D target) {

		target.setColor(Color.WHITE);
		target.fillRect(0, 0, WIDTH, HEIGHT);

		Set<Atom> shadowCopy = new HashSet<>(allAtoms);

		for (Atom atom : shadowCopy) {

			target.setColor(Color.GRAY);
			atom.drawVisibility(target);

			target.setColor(Color.RED);
			atom.drawVelocity(target);

			target.setColor(Color.BLACK);
			atom.drawAtom(target);
		}
	}

	public void addAtoms(Collection<? extends Atom> newAtoms) {

		newAtoms.forEach(newAtom -> {

			if (newAtom.isIntelligent()) {
				intelligentAtoms.add(newAtom);
			} else {
				brainlessAtoms.add(newAtom);
			}
		});

		allAtoms.addAll(newAtoms);
	}

	public Set<Atom> pullOutIntelligentAtomsAndClear() {

		Set<Atom> result = new HashSet<>(intelligentAtoms);

		intelligentAtoms.clear();
		brainlessAtoms.clear();
		allAtoms.clear();

		return result;
	}
}
