package view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import model.Arena;
import model.Atom;
import model.geometry.Vector;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;
	private Panel panel;

	public Frame(Arena arena) {

		this.panel = new Panel(arena);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Genetic Algorithm Test");

		this.add(panel, BorderLayout.CENTER);
		this.setResizable(false);
		this.setVisible(true);
		this.pack();

	}

	public Frame(Arena arena, Atom controlledAtom) {

		this(arena);

		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				char keyChar = e.getKeyChar();

				switch (keyChar) {
				case 'w':
					controlledAtom.getVelocity().add(new Vector(0, -10));
					break;
				case 'a':
					controlledAtom.getVelocity().add(new Vector(-10, 0));
					break;
				case 's':
					controlledAtom.getVelocity().add(new Vector(0, 10));
					break;
				case 'd':
					controlledAtom.getVelocity().add(new Vector(10, 0));
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
	}

	public Panel getPanel() {
		return panel;
	}
}
