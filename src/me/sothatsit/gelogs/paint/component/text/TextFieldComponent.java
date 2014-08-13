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

public class TextFieldComponent extends PaintComponent
{
	
	private Color colour;
	private Font font;
	private String[] text;
	private boolean centre_text;
	private int space_per_line;
	
	public TextFieldComponent( int x , int y , int width , int height , int space_per_line , String[] text )
	{
		super(new Rectangle(x, y, width, height), new Padding(1, 1, 1, 1));
		this.colour = new Color(255, 255, 255);
		this.text = text;
		this.font = null;
		this.centre_text = false;
		this.space_per_line = 20;
	}
	
	public int getSpacePerLine()
	{
		return space_per_line;
	}
	
	public void setSpacePerLine(int space)
	{
		this.space_per_line = space;
	}
	
	public boolean isCentreText()
	{
		return centre_text;
	}
	
	public void setCentreText(boolean centre)
	{
		this.centre_text = centre;
	}
	
	public String[] getText()
	{
		return text;
	}
	
	public void setText(String[] text)
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
		
		for ( int i = 0; i < text.length; i++ )
		{
			String str = text[i];
			
			int yoff = space_per_line * i;
			
			if ( yoff > bounds.height )
				break;
			
			int xoff = 0;
			if ( centre_text )
				xoff = ( bounds.width / 2 ) - ( metrics.stringWidth(str) / 2 );
			
			Point p = paint.toRenderCoords(bounds.x + xoff , bounds.y + yoff + ( ( space_per_line / 2 ) + ( metrics.getHeight() / 2 ) / 2 ));
			
			g.drawString(str , p.x , p.y);
		}
		
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
