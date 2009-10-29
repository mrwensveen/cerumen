package org.cerumen.grid.event;

import java.awt.Point;
import java.util.EventListener;

public interface GridEventListener extends EventListener {
	public void gridEntered(Point point);
	public void gridExited(Point point);
	public void gridClicked(Point point);
}
