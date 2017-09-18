package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import model.Arena;

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

	public Panel getPanel() {
		return panel;
	}
}
