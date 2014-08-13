package me.sothatsit.gelogs.paint.container;

import java.awt.Dimension;
import java.awt.Point;

import me.sothatsit.gelogs.paint.component.IPaintComponent;

public interface IPaintLayout
{
	public Point getAvailableSpace(IPaintContainer container, IPaintComponent component);
	
	public Dimension getContainerSize(IPaintContainer container);
}
