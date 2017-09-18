package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import model.Arena;

public class Panel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Arena arena;

	public Panel(Arena arena) {

		this.arena = arena;

		this.setPreferredSize(new Dimension(Arena.WIDTH, Arena.HEIGHT));
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		arena.draw(g2);
	}
}
