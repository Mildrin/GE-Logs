package me.sothatsit.gelogs.paint.component;

import java.awt.Rectangle;

import me.sothatsit.gelogs.paint.Padding;
import me.sothatsit.gelogs.paint.container.IPaintContainer;

public abstract class PaintComponent implements IPaintComponent
{
	
	// Bound before Padding applied
	private Rectangle original_bounds;
	
	// Bounds of Component
	private Rectangle bounds;
	
	// Padding Around Component
	private Padding padding;
	
	// Parent Container The Component Is In
	private IPaintContainer container;
	
	// Whether The Component Is Visible
	private boolean visible;
	
	public PaintComponent( Rectangle bounds , Padding padding )
	{
		this.original_bounds = (Rectangle) bounds.clone();
		this.padding = padding;
		this.container = null;
		this.visible = true;
		
		applyPadding();
	}
	
	@Override
	public boolean isVisible()
	{
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	@Override
	public Rectangle getOriginalBounds()
	{
		return original_bounds;
	}
	
	@Override
	public Padding getPadding()
	{
		return padding.clone();
	}
	
	private void applyPadding()
	{
		bounds = (Rectangle) original_bounds.clone();
		
		bounds.x += padding.left;
		bounds.y += padding.top;
	}
	
	@Override
	public void setPadding(Padding padding)
	{
		this.padding = padding.clone();
		
		applyPadding();
	}
	
	@Override
	public Rectangle getPaddedBounds()
	{
		Rectangle bounds = getBounds();
		
		bounds.x -= padding.left;
		bounds.y -= padding.top;
		bounds.width += padding.left + padding.right;
		bounds.height += padding.top + padding.bottom;
		
		return bounds;
	}
	
	@Override
	public Rectangle getBounds()
	{
		return (Rectangle) bounds.clone();
	}
	
	@Override
	public void setLocation(int x, int y)
	{
		this.original_bounds.x = x;
		this.original_bounds.y = y;
		
		this.applyPadding();
	}
	
	@Override
	public void setSize(int width, int height)
	{
		this.original_bounds.width = width;
		this.original_bounds.height = height;
		
		this.bounds.width = width;
		this.bounds.height = height;
	}
	
	@Override
	public void setBounds(Rectangle bounds)
	{
		this.original_bounds = (Rectangle) bounds.clone();
		
		applyPadding();
	}
	
	@Override
	public boolean inBounds(int x, int y)
	{
		return x >= bounds.x && y >= bounds.y && x <= bounds.x + bounds.width && y <= bounds.y + bounds.height;
	}
	
	@Override
	public IPaintContainer getContainer()
	{
		return container;
	}
	
	@Override
	public void setContainer(IPaintContainer container)
	{
		this.container = container;
	}
	
	@Override
	public boolean valid()
	{
		return visible;
	}
}
