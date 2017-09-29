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

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
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

		// move every living atom
		for (Atom atom : intelligentAtoms) {

			double[] atomInput = new double[Atom.NUMBER_OF_VIEW_RAYS];
			Arrays.fill(atomInput, -1);

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

						if (atomInput[i] == -1
								|| atomInput[i] >= distanceRate) {
							atomInput[i] = distanceRate > 1 ? 1 : distanceRate;
						}
					}
				}
			}

			atom.move(atomInput);
		}

		// remove dead atoms
		Set<Atom> killedAtoms = new HashSet<>();
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
						atom.getPosition()) <= maxSize - minSize
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

		for (Atom atom : allAtoms) {

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
