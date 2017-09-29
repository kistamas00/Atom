package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import model.Arena;
import model.Atom;
import view.Frame;

public class Main {

	private static int NUMBER_OF_LIVING_ATOMS = 10;
	private static int NUMBER_OF_DEAD_ATOMS = 50;

	private static int GENERATION_TIME = 5000;

	public static void main(String[] args) throws InterruptedException {

		// Atom controlledAtom = new Atom(7);

		Arena arena = new Arena(Arrays.asList(/* controlledAtom */));
		Frame frame = new Frame(arena/* , controlledAtom */);

		Set<Atom> atoms = new HashSet<>();

		// generate none moving atoms
		for (int i = 0; i < NUMBER_OF_DEAD_ATOMS; i++) {
			atoms.add(new Atom(2));
		}

		// generate living atoms
		for (int i = 0; i < NUMBER_OF_LIVING_ATOMS; i++) {
			atoms.add(new Atom(true));
		}

		arena.addAtoms(atoms);

		int generationCounter = 0;
		long generationBithDate = System.currentTimeMillis();
		System.err.println("Generation 0 has born!");
		while (true) {

			arena.doStep();
			frame.repaint();

			if (System.currentTimeMillis()
					- generationBithDate >= GENERATION_TIME) {

				// choosing parents

				final List<Atom> survivorAtoms = new ArrayList<>(
						arena.pullOutIntelligentAtomsAndClear());

				if (survivorAtoms.size() > 0) {

					survivorAtoms.sort((a,
							b) -> -Double.compare(a.getSize(), b.getSize()));

					final double sizeSum = survivorAtoms.stream()
							.collect(Collectors.summingDouble(Atom::getSize));
					final double maxSize = survivorAtoms.get(0).getSize();

					double parentArandom = Math.random() * sizeSum;
					double parentBrandom = Math.random() * sizeSum;

					Atom parentA = null;
					Atom parentB = null;
					for (int i = 0; i < survivorAtoms.size(); i++) {

						Atom atom = survivorAtoms.get(i);

						parentArandom -= atom.getSize();
						if (parentArandom < 0) {
							parentA = atom;
						}
						parentBrandom -= atom.getSize();
						if (parentBrandom < 0) {
							parentB = atom;
						}

						if (parentA != null && parentB != null) {
							break;
						}
					}

					System.err.println(
							"Generation " + generationCounter + " has dead!");
					System.err.println(
							"Max size was " + ((int) (maxSize * 100) / 100.0));

					System.err.println("Generation " + (generationCounter + 1)
							+ " has born!");

					// loop action(s)
					generationCounter++;

				} else {

					arena.addAtoms(Arrays.asList(new Atom(true)));
				}

				generationBithDate = System.currentTimeMillis();
			}

			Thread.sleep(10);
		}
	}
}
