package org.cerumen;
import java.awt.Color;

public class Palette {
	final static Color[] palette = {Color.blue, Color.green, Color.red, Color.yellow, Color.orange, Color.pink, Color.cyan, Color.magenta};

	static Color getColor(final int index) {
		return palette[index];
	}
}