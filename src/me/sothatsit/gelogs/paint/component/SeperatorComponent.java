package me.sothatsit.gelogs.paint.component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import me.sothatsit.gelogs.paint.Padding;
import me.sothatsit.gelogs.paint.container.IPaintContainer;

public class SeperatorComponent extends PaintComponent
{
	
	private Color colour;
	private boolean fill_container;
	
	public SeperatorComponent( int x , int y , int width , int height )
	{
		super(new Rectangle(x, y, width, height), new Padding(1, 1, 1, 1));
		this.colour = new Color(255, 255, 255, 100);
	}
	
	public boolean isFillContainer()
	{
		return fill_container;
	}
	
	public void setFillContainer(boolean fill_container)
	{
		this.fill_container = fill_container;
	}
	
	public Color getColour()
	{
		return colour;
	}
	
	public void setColour(Color colour)
	{
		this.colour = colour;
	}
	
	@Override
	public void paint(IPaintContainer paint, Graphics2D g)
	{
		Rectangle bounds = getBounds();
		Padding padding = getPadding();
		
		Point p1 = paint.toRenderCoords(bounds.x , bounds.y);
		Point p2 = paint.toRenderCoords(bounds.x + ( !fill_container || this.getContainer() == null ? bounds.width : this.getContainer().getBounds().width - ( padding.left + padding.right ) ) ,
				bounds.y);
		
		g.setColor(colour);
		
		g.drawLine(p1.x , p1.y , p2.x , p2.y);
	}
	
	@Override
	public void onMouseClick(Point mouse)
	{
		
	}
	
	@Override
	public void onMouseDrag(Point from, Point to)
	{
		
	}
	
	@Override
	public boolean valid()
	{
		Rectangle bounds = getBounds();
		
		return super.valid() && bounds.x >= 0 && bounds.y >= 0 && bounds.width >= 0 && bounds.height >= 0;
	}
	
}
