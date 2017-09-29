package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import model.Arena;
import model.Atom;
import view.Frame;

public class Main {

	private static int NUMBER_OF_POPULATION = 10;
	private static int NUMBER_OF_FREE_FOOD = 50;
	private static double MUTATION_RATE = 0.01;
	private static int GENERATION_TIME = 5000;

	private static Set<Atom> generateBrainlessAtomsIn() {

		Set<Atom> atoms = new HashSet<>();

		for (int i = 0; i < NUMBER_OF_FREE_FOOD; i++) {
			atoms.add(new Atom(3));
		}

		return atoms;
	}

	public static void main(String[] args) throws InterruptedException {

		// Atom controlledAtom = new Atom(7);

		Arena arena = new Arena(Arrays.asList(/* controlledAtom */));
		Frame frame = new Frame(arena/* , controlledAtom */);

		Set<Atom> atoms = new HashSet<>();

		// generate none moving atoms
		atoms.addAll(generateBrainlessAtomsIn());

		// generate living atoms
		for (int i = 0; i < NUMBER_OF_POPULATION; i++) {
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

						if (parentA == null) {
							parentArandom -= atom.getSize();
							if (parentArandom < 0) {
								parentA = atom;
							}
						}
						if (parentB == null) {
							parentBrandom -= atom.getSize();
							if (parentBrandom < 0) {
								parentB = atom;
							}
						}

						if (parentA != null && parentB != null) {
							break;
						}
					}

					System.err.println(
							"Generation " + generationCounter + " has dead!");
					System.err.println(" Max size was "
							+ String.format(Locale.US, "%.2f", maxSize));
					System.err.println(" New parents: " + parentA + " ("
							+ String.format(Locale.US, "%.2f",
									parentA.getSize())
							+ "), " + parentB + " (" + String.format(Locale.US,
									"%.2f", parentB.getSize())
							+ ")");

					// generate new generation

					final double[] parentADNA = parentA.getDNA();
					final double[] parentBDNA = parentB.getDNA();

					Set<Atom> newGeneration = new HashSet<>();
					for (int i = 0; i < NUMBER_OF_POPULATION; i++) {

						final double[] newDNA = new double[parentADNA.length];

						for (int j = 0; j < newDNA.length; j++) {

							if (Math.random() < MUTATION_RATE) {

								newDNA[j] = Math.random() * 2 - 1;

							} else {

								if (Math.random() < 0.5) {
									newDNA[j] = parentADNA[j];
								} else {
									newDNA[j] = parentBDNA[j];
								}
							}
						}

						newGeneration.add(new Atom(newDNA));
					}

					arena.addAtoms(newGeneration);

					System.err.println("Generation " + (generationCounter + 1)
							+ " has born!");

					// loop action(s)
					generationCounter++;

				} else {
					System.err.println("Generation " + generationCounter
							+ " has reloaded!");
					arena.addAtoms(Arrays.asList(new Atom(true)));
				}

				// loop action(s)
				arena.addAtoms(generateBrainlessAtomsIn());
				generationBithDate = System.currentTimeMillis();
			}

			Thread.sleep(10);
		}
	}
}
