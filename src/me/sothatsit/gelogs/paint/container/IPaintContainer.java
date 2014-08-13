package me.sothatsit.gelogs.paint.container;

import java.awt.Point;
import java.util.List;

import me.sothatsit.gelogs.paint.component.IPaintComponent;

public interface IPaintContainer extends IPaintComponent
{
	
	public void add(IPaintComponent... components);
	
	public void remove(IPaintComponent... components);
	
	public void clear();
	
	public List< IPaintComponent > getComponents();
	
	public IPaintComponent[] getComponents(int x, int y);
	
	public Point toRenderCoords(int x, int y);
	
	public Point fromRenderCoords(int x, int y);
	
	public Point getMouseCoords();
	
	public void setMouseCoords(Point mouse);
	
	public void organize();
	
}
