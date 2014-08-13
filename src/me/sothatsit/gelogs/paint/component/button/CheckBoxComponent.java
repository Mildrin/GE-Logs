package me.sothatsit.gelogs.paint.component.button;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import me.sothatsit.gelogs.paint.container.IPaintContainer;

public class CheckBoxComponent extends ButtonComponent
{
	
	private boolean selected;
	private Color unselected_colour;
	private Color selected_colour;
	private int box_size;
	
	public CheckBoxComponent( int x , int y , int width , int height , int box_size , String text )
	{
		super(x, y, width, height, text);
		this.unselected_colour = new Color(186, 24, 24);
		this.selected_colour = new Color(68, 186, 24);
		this.box_size = box_size;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	public int getBoxSize()
	{
		return box_size;
	}
	
	public void setBoxSize(int size)
	{
		this.box_size = size;
	}
	
	public Color getUnselectedColour()
	{
		return unselected_colour;
	}
	
	public void setUnselectedColour(Color unselected)
	{
		this.unselected_colour = unselected;
	}
	
	public Color getSelectedColour()
	{
		return selected_colour;
	}
	
	public void setSelectedColour(Color selected)
	{
		this.selected_colour = selected;
	}
	
	@Override
	public void paint(IPaintContainer paint, Graphics2D g)
	{
		Rectangle bounds = getBounds();
		
		if ( getBorderColour() != null )
		{
			g.setColor(getBorderColour());
			
			Point p = paint.toRenderCoords(bounds.x , bounds.y);
			
			g.drawRect(p.x , p.y , bounds.width , bounds.height);
		}
		
		g.setColor(isSelected() ? selected_colour : unselected_colour);
		
		Point box = paint.toRenderCoords(bounds.x + 5 , bounds.y + ( ( bounds.height / 2 ) - ( box_size / 2 ) ));
		
		g.fillRect(box.x , box.y , box_size , box_size);
		
		Font f = g.getFont();
		
		if ( getFont() != null )
			g.setFont(getFont());
		
		g.setColor(isHovered() ? getHoverColour() : getColour());
		
		FontMetrics metrics = g.getFontMetrics();
		
		int xoff = box_size + 10;
		
		Point p = paint.toRenderCoords(bounds.x + xoff , bounds.y + ( ( bounds.height / 2 ) + ( metrics.getHeight() / 2 ) / 2 ));
		
		g.drawString(getText() , p.x , p.y);
		
		g.setFont(f);
	}
	
	@Override
	public void onMouseClick(Point mouse)
	{
		this.setSelected(!selected);
	}
}
