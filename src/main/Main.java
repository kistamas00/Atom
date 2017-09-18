package main;

import java.util.Arrays;

import model.Arena;
import model.Atom;
import view.Frame;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		Arena arena = new Arena(Arrays.asList(new Atom()));
		Frame frame = new Frame(arena);

		while (true) {

			arena.doStep();
			frame.repaint();

			Thread.sleep(10);
		}
	}
}
