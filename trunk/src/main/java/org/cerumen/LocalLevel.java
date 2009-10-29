package org.cerumen;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LocalLevel implements Level {
	public static final byte BARRIER = -1;
	private static final byte CURRENT_VERSION = 0;
	private final int version, width, height;
	private final byte[] levelData;
	private final byte[] players;

	private byte currentPlayerIndex;

	public LocalLevel(final byte version, final byte width, final byte height, final byte numPlayers, final byte firstPlayer) {
		this.version = version;
		this.width = width;
		this.height = height;

		assert numPlayers != Byte.MAX_VALUE;

		players = new byte[numPlayers];
		for (byte i = 0; i < numPlayers; i++) {
			players[i] = (byte) (i + 1);
		}
		Collections.shuffle(Arrays.asList(players));

		for (currentPlayerIndex = 0; currentPlayerIndex < numPlayers; currentPlayerIndex++) {
			if (players[currentPlayerIndex] == firstPlayer) {
				break;
			}
		}

		levelData = new byte[width*height];

		DEBUG("New level created, version = " + version + ", width = " + width + ", height = " + height + ", numPlayers = " + numPlayers);
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#nextPlayer()
	 */
	public byte nextPlayer() {
		currentPlayerIndex = (byte) ((currentPlayerIndex + 1) % getNumPlayers());

		return getCurrentPlayer();
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#movePiece(byte, java.awt.Point, java.awt.Point)
	 */
	public void movePiece(final byte player, final Point from, final Point to) {
		assert getLevelData(from) == player : "Illegal player!";
		assert getLegalMoves(from).contains(to) : "Illegal move!";

		setLevelData(to, player);
		setLevelData(from, (byte) 0);

		// check for a loser
		final Point check = new Point();
		byte loser = 0;
		for (int x = 0; loser == 0 && x < getWidth(); x++) {
			for (int y = 0; loser == 0 && y < getHeight(); y++) {
				check.setLocation(x, y);
				if (getLevelData(check) != 0 && getLegalMoves(check).isEmpty()) {
					loser = getLevelData(check);
				}
			}
		}

		// found a loser?
		if (loser != 0) {
			// make all his pieces barriers
			for (int x = 0; x < getWidth(); x++) {
				for (int y = 0; y < getHeight(); y++) {
					check.setLocation(x, y);
					if (getLevelData(check) == loser) {
						setLevelData(check, BARRIER);
					}
				}
			}

			// kill the player (remove from players array)
			for (int p = 0; p < players.length; p++) {
				if (players[p] == loser) {
					players[p] = 0;
					break;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#getLegalMoves(java.awt.Point)
	 */
	public Set<Point> getLegalMoves(final Point from) {
		final Set<Point> legalMoves = new HashSet<Point>();
		Point to;

		// above
		to = new Point(from.x, from.y - 1);
		if (to.y >= 0 && getLevelData(to) == 0) {
			legalMoves.add(to);
		}
		// right of
		to = new Point(from.x + 1, from.y);
		if (to.x < getWidth() && getLevelData(to) == 0) {
			legalMoves.add(to);
		}
		// below
		to = new Point(from.x, from.y + 1);
		if (to.y < getHeight() && getLevelData(to) == 0) {
			legalMoves.add(to);
		}
		// left of
		to = new Point(from.x - 1, from.y);
		if (to.x >= 0 && getLevelData(to) == 0) {
			legalMoves.add(to);
		}

		return legalMoves;
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#getVersion()
	 */
	public int getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#getNumPlayers()
	 */
	public int getNumPlayers() {
		return players.length;
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#getCurrentPlayer()
	 */
	public byte getCurrentPlayer() {
		final byte player = players[currentPlayerIndex];
		return player != 0 ? player : nextPlayer();
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#getLevelData(java.awt.Point)
	 */
	public byte getLevelData(final Point point) {
		return levelData[point.y * width + point.x];
	}

	/* (non-Javadoc)
	 * @see org.cerumen.ILevel#setLevelData(java.awt.Point, byte)
	 */
	public void setLevelData(final Point point, final byte value) {
		levelData[point.y * width + point.x] = value;
	}

	public void saveLevel(final String fileName) {
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(fileName);
		}
		catch(final FileNotFoundException e) {
			System.out.println(e);
			System.out.println("Save unsuccessful!");
			return;
		}

		try{
			final byte[] first3Bytes = {'R', 'G', 'L'};
			fOut.write(first3Bytes);
			fOut.write(LocalLevel.CURRENT_VERSION);
			fOut.write(width);
			fOut.write(height);
			fOut.write(getNumPlayers());
			fOut.write(levelData);
			fOut.close();
		}
		catch(final IOException e) {
			System.out.println(e);
		}
	}

	public static Level loadLevel(final String fileName) {
		LocalLevel rgLevel = null;
		FileInputStream fIn;
		try {
			fIn = new FileInputStream(fileName);
		}
		catch(final FileNotFoundException e) {
			System.out.println(e);
			return null;
		}

		try {
			final byte[] first3Bytes = new byte[3];
			fIn.read(first3Bytes);
			if(!(new String(first3Bytes).equals("RGL"))) {
				System.out.println("File is not a Rommert's Game Level!");
				System.out.println(new String(first3Bytes));
				return null;
			}

			rgLevel = new LocalLevel((byte)fIn.read(), (byte)fIn.read(), (byte)fIn.read(), (byte)fIn.read(), (byte)fIn.read());

			if(fIn.read(rgLevel.levelData) == -1) {
				System.out.println("Premature EOF while reading level data");
				return null;
			}
			fIn.close();
		}
		catch(final IOException e) {
			System.out.println(e);
		}

		return rgLevel;
	}

	private static void DEBUG(final String msg) {
		System.out.println("DEBUG: " + msg);
	}
}