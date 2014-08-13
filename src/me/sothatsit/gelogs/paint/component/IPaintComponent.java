package me.sothatsit.gelogs.paint.component;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import me.sothatsit.gelogs.paint.Padding;
import me.sothatsit.gelogs.paint.container.IPaintContainer;

public interface IPaintComponent
{
	
	public boolean isVisible();
	
	public void setVisible(boolean visible);
	
	public Padding getPadding();
	
	public void setPadding(Padding padding);
	
	public Rectangle getPaddedBounds();
	
	public Rectangle getOriginalBounds();
	
	public Rectangle getBounds();
	
	public void setLocation(int x, int y);
	
	public void setSize(int width, int height);
	
	public void setBounds(Rectangle bounds);
	
	public boolean inBounds(int x, int y);
	
	public void paint(IPaintContainer paint, Graphics2D g);
	
	public void onMouseClick(Point mouse);
	
	public void onMouseDrag(Point from, Point to);
	
	public IPaintContainer getContainer();
	
	public void setContainer(IPaintContainer container);
	
	public boolean valid();
	
}
