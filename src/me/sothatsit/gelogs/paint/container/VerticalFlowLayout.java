package me.sothatsit.gelogs.paint.container;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import me.sothatsit.gelogs.paint.component.IPaintComponent;

public class VerticalFlowLayout implements IPaintLayout
{
	
	@Override
	public Point getAvailableSpace(IPaintContainer container, IPaintComponent component)
	{
		Point p = new Point(component.getOriginalBounds().x, 0);
		
		for ( IPaintComponent comp : container.getComponents() )
		{
			if ( !comp.valid() )
				continue;
			
			Rectangle bounds = comp.getPaddedBounds();
			
			if ( bounds.y + bounds.height > p.y )
				p.y = bounds.y + bounds.height;
		}
		
		return p;
	}
	
	@Override
	public Dimension getContainerSize(IPaintContainer container)
	{
		Dimension d = new Dimension(0, 0);
		
		for ( IPaintComponent comp : container.getComponents() )
		{
			if ( !comp.valid() )
				continue;
			
			Rectangle bounds = comp.getPaddedBounds();
			
			if ( bounds.y + bounds.height > d.height )
				d.height = bounds.y + bounds.height;
			
			if ( bounds.x + bounds.width > d.width )
				d.width = bounds.x + bounds.width;
		}
		
		return d;
	}
}
