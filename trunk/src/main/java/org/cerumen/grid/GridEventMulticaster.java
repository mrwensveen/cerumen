package org.cerumen.grid;

import java.awt.Point;
import java.util.EventListener;

import org.cerumen.grid.event.*;

public class GridEventMulticaster implements GridEventListener {
	protected final EventListener a, b;

	protected GridEventMulticaster(EventListener a, EventListener b) {
		this.a = a; this.b = b;
	}

	protected EventListener remove(EventListener oldl) {
		if (oldl == a)  return b;
		if (oldl == b)  return a;
		EventListener a2 = removeInternal(a, oldl);
		EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b) {
			return this;	// it's not here
		}
		return addInternal(a2, b2);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)  return b;
		if (b == null)  return a;
		return new GridEventMulticaster(a, b);
	}

	protected static EventListener removeInternal(EventListener l, EventListener oldl) {
		if (l == oldl || l == null) {
			return null;
		}
		else if (l instanceof GridEventMulticaster) {
			return ((GridEventMulticaster)l).remove(oldl);
		}
		else {
			return l;		// it's not here
		}
	}

	public static GridEventListener add(GridEventListener a, GridEventListener b) {
		return (GridEventListener)addInternal(a, b);
	}

	public static GridEventListener remove(GridEventListener l, GridEventListener oldl) {
		return (GridEventListener)removeInternal(l, oldl);
	}

	public void gridEntered(Point point) {
		((GridEventListener)a).gridEntered(point);
		((GridEventListener)b).gridEntered(point);
	}

	public void gridExited(Point point) {
		((GridEventListener)a).gridExited(point);
		((GridEventListener)b).gridExited(point);
	}

	public void gridClicked(Point point) {
		((GridEventListener)a).gridClicked(point);
		((GridEventListener)b).gridClicked(point);
	}
}