package org.cerumen.grid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.cerumen.grid.event.GridEventListener;

//public class Grid extends Canvas implements MouseListener, MouseMotionListener {
public abstract class Grid extends Component implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private final int gridSizeX = 100, gridSizeY = 100, marginLeft = 15, marginRight = 15, marginTop = 15, marginBottom = 15;

	private final int gridsX, gridsY;

	private Image backImage;
	private Graphics backGraphics;
	private Color background = Color.white;

// Event support
	private GridEventListener gridEventListener = null;
	private int currentGridEnteredX = -1, currentGridEnteredY = -1;

// constructor
	public Grid(final int width, final int height) {
		gridsX = width;
		gridsY = height;
		final Dimension preferredSize = new Dimension(width*gridSizeX + marginLeft + marginRight, height*gridSizeY + marginTop + marginBottom);
		setPreferredSize(preferredSize);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public int getGridsX() {
		return gridsX;
	}

	public int getGridsY() {
		return gridsY;
	}

	@Override
	public void setBackground(final Color c) {
		background = c;
	}

	protected void paintGrids(final Graphics graphics) {
		final Dimension preferredSize = getPreferredSize();
		final int width = preferredSize.width;
		final int height = preferredSize.height;

		graphics.setColor(background);
		graphics.fillRect(0, 0, width, height);

		for(byte y = 0; y < gridsY; y++) {
			for(byte x = 0; x <gridsX; x++) {
				final Graphics gridGraphics = graphics.create(marginLeft + x*gridSizeX, marginTop + y*gridSizeY, gridSizeX, gridSizeY);
				paintGrid(x, y, gridGraphics);
				graphics.setColor(Color.black);
				graphics.drawRect(marginLeft + x*gridSizeX, marginTop + y*gridSizeY, gridSizeX, gridSizeY);
			}
		}
	}

	protected abstract void paintGrid(int x, int y, Graphics graphics);

	@Override
	public void update(final Graphics g) {
		paint(g);
	}

	@Override
	public void paint(final Graphics g) {
		// do some late initialization
		if (backImage == null) {
			final Dimension preferredSize = getPreferredSize();
			backImage = createImage(preferredSize.width, preferredSize.height);
		}
		if (backGraphics == null) {
			backGraphics = backImage.getGraphics();
		}

		// go paint already!
		paintGrids(backGraphics);
		g.drawImage(backImage, 0, 0, this);
	}

	// Event support

	public void addGridEventListener(final GridEventListener l) {
		gridEventListener = GridEventMulticaster.add(l, gridEventListener);
	}

	public void mouseEntered(final MouseEvent e) {}
	public void mouseExited(final MouseEvent e) {}
	public void mousePressed(final MouseEvent e) {}
	public void mouseReleased(final MouseEvent e) {}
	public void mouseDragged(final MouseEvent e) {}

	public void mouseMoved(final MouseEvent e) {
		final int gridX = translateX(e.getX());
		final int gridY = translateY(e.getY());

		if((gridEventListener != null) && ((gridX != currentGridEnteredX) || (gridY != currentGridEnteredY))) {
			if((currentGridEnteredX < gridsX) && (currentGridEnteredX >= 0) && (currentGridEnteredY < gridsY) && (currentGridEnteredY >= 0)) {
				gridEventListener.gridExited(new Point(currentGridEnteredX, currentGridEnteredY));
			}
			currentGridEnteredX = gridX;
			currentGridEnteredY = gridY;
			if((currentGridEnteredX < gridsX) && (currentGridEnteredX >= 0) && (currentGridEnteredY < gridsY) && (currentGridEnteredY >= 0)) {
				gridEventListener.gridEntered(new Point(currentGridEnteredX, currentGridEnteredY));
			}
		}
	}

	public void mouseClicked(final MouseEvent e) {
		if(gridEventListener != null) {
			gridEventListener.gridClicked(new Point(translateX(e.getX()), translateY(e.getY())));
		}
	}

	protected int translateX(final int componentX) {
		return (int)Math.floor((componentX - marginLeft)/(double)gridSizeX);
	}

	protected int translateY(final int componentY) {
		return (int)Math.floor((componentY - marginTop)/(double)gridSizeY);
	}
}