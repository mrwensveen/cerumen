package org.cerumen;

import java.awt.Point;
import java.util.Set;

public interface Level {

	/**
	 * Advances to the next player and returns the next player's number
	 * @return
	 * @see #getCurrentPlayer()
	 */
	public byte nextPlayer();

	public void movePiece(final byte player, final Point from, final Point to);

	public Set<Point> getLegalMoves(final Point from);

	public int getVersion();

	public int getWidth();

	public int getHeight();

	public int getNumPlayers();

	public byte getCurrentPlayer();

	public byte getLevelData(final Point point);

	public void setLevelData(final Point point, final byte value);

}