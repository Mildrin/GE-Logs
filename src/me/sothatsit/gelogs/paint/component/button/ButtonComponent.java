package me.sothatsit.gelogs.paint.component.button;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import me.sothatsit.gelogs.paint.Padding;
import me.sothatsit.gelogs.paint.component.PaintComponent;
import me.sothatsit.gelogs.paint.container.IPaintContainer;

public class ButtonComponent extends PaintComponent
{
	
	private Color colour;
	private Color hover;
	private Color border;
	private Font font;
	private String text;
	
	private ButtonPressListener listener;
	
	public ButtonComponent( int x , int y , int width , int height , String text )
	{
		super(new Rectangle(x, y, width, height), new Padding(1, 1, 1, 1));
		this.colour = new Color(255, 255, 255);
		this.hover = new Color(180, 180, 180);
		this.border = new Color(255, 255, 255, 100);
		this.text = text;
	}
	
	public ButtonPressListener getPressListener()
	{
		return listener;
	}
	
	public void setPressListener(ButtonPressListener listener)
	{
		this.listener = listener;
	}
	
	public Color getBorderColour()
	{
		return border;
	}
	
	public void setBorderColour(Color border)
	{
		this.border = border;
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
	
	public Color getHoverColour()
	{
		return hover;
	}
	
	public void setHoverColour(Color hover)
	{
		this.hover = hover;
	}
	
	public boolean isHovered()
	{
		if ( this.getContainer() == null )
			return false;
		
		Point mouse = this.getContainer().getMouseCoords();
		
		return this.inBounds(mouse.x , mouse.y);
	}
	
	@Override
	public void paint(IPaintContainer paint, Graphics2D g)
	{
		Rectangle bounds = getBounds();
		
		if ( border != null )
		{
			g.setColor(border);
			
			Point p = paint.toRenderCoords(bounds.x , bounds.y);
			
			g.drawRect(p.x , p.y , bounds.width , bounds.height);
		}
		
		if ( font != null )
			g.setFont(font);
		
		g.setColor(isHovered() ? hover : colour);
		
		FontMetrics metrics = g.getFontMetrics();
		
		int xoff = ( bounds.width / 2 ) - ( metrics.stringWidth(text) / 2 );
		
		Point p = paint.toRenderCoords(bounds.x + xoff , bounds.y + ( ( bounds.height / 2 ) + ( metrics.getHeight() / 2 ) / 2 ));
		
		g.drawString(text , p.x , p.y);
	}
	
	@Override
	public void onMouseClick(Point mouse)
	{
		if ( listener != null )
			listener.onPress(this);
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
