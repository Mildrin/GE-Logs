package me.sothatsit.gelogs.paint.component.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import me.sothatsit.gelogs.paint.Padding;
import me.sothatsit.gelogs.paint.component.PaintComponent;
import me.sothatsit.gelogs.paint.container.IPaintContainer;

public class TextComponent extends PaintComponent
{
	
	private Color colour;
	private Font font;
	private String text;
	private boolean centre_text;
	
	public TextComponent( int x , int y , int width , int height , String text )
	{
		super(new Rectangle(x, y, width, height), new Padding(1, 1, 1, 1));
		this.colour = new Color(255, 255, 255);
		this.text = text;
	}
	
	public boolean isCentreText()
	{
		return centre_text;
	}
	
	public void setCentreText(boolean centre)
	{
		this.centre_text = centre;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public Font getFont()
	{
		return font;
	}
	
	public void setFont(Font font)
	{
		this.font = font;
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
		
		Font f = g.getFont();
		
		if ( font != null )
			g.setFont(font);
		
		g.setColor(colour);
		
		FontMetrics metrics = g.getFontMetrics();
		
		int xoff = 0;
		if ( centre_text )
		{
			xoff = ( bounds.width / 2 ) - ( metrics.stringWidth(text) / 2 );
		}
		
		Point p = paint.toRenderCoords(bounds.x + xoff , bounds.y + ( ( bounds.height / 2 ) + ( metrics.getHeight() / 2 ) / 2 ));
		
		g.drawString(text , p.x , p.y);
		
		g.setFont(f);
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
