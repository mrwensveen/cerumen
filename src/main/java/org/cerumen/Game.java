package org.cerumen;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cerumen.grid.Grid;
import org.cerumen.grid.event.GridEventListener;

public class Game extends Grid implements GridEventListener {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(Game.class);

	private final Level level;

	private Point pointClicked;
	private Set<Point> legalMoves;

	public Game(final Level level) {
		super(level.getWidth(), level.getHeight());
		this.level = level;

		addGridEventListener(this);

		// simple rendering loop
		new Thread() {
			@Override
			public void run() {
				while (true) {
					Game.this.repaint();
					try {
						Thread.sleep(100);
					} catch (final InterruptedException e) {
						log.error("Interrupted", e);
					}
				}
			}
		}.start();
	}

	@Override
	protected void paintGrid(final int x, final int y, final Graphics graphics) {
		final Rectangle bounds = graphics.getClipBounds();

		final Point point = new Point(x, y);

		if (legalMoves != null && legalMoves.contains(point)) {
			graphics.setColor(Color.CYAN);
			graphics.fillRect(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());
		}

		final byte data = level.getLevelData(point);

		if (data == LocalLevel.BARRIER) {
			graphics.setColor(Color.GRAY);
			graphics.fillRect(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());
		} else if (data > 0) {
			graphics.setColor(Palette.getColor(data - 1));
			graphics.fillOval(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());
		}
	}

	@Override
	public void gridClicked(final Point point) {
		final byte levelData = level.getLevelData(point);
		final byte currentPlayer = level.getCurrentPlayer();

		if (pointClicked == null && currentPlayer == levelData) {
			pointClicked = point;
			legalMoves = level.getLegalMoves(point);
		} else if (pointClicked != null) {
			assert legalMoves != null : "legalMoves should not be null!";

			if (legalMoves.contains(point)) {
				level.movePiece(currentPlayer, pointClicked, point);

				// check for a winner
				if (currentPlayer == level.nextPlayer()) {
					System.out.println("Player " + currentPlayer + " wins!");
				}
			}
			pointClicked = null;
			legalMoves = null;
		}
		repaint();
	}

	@Override
	public void gridEntered(final Point point) {
	}

	@Override
	public void gridExited(final Point point) {
	}
}