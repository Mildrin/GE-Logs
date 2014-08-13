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

public class TabComponent extends PaintComponent
{
	
	private Color colour;
	private Color hover;
	private Color seperator;
	private Font font;
	private String[] tabs;
	private String selected;
	
	private TabPressListener listener;
	
	public TabComponent( int x , int y , int width , int height , String[] tabs )
	{
		super(new Rectangle(x, y, width, height), new Padding(1, 1, 1, 1));
		this.colour = new Color(255, 255, 255);
		this.hover = new Color(180, 180, 180);
		this.seperator = new Color(255, 255, 255, 100);
		this.tabs = tabs;
		this.selected = tabs[0];
	}
	
	public String getSelected()
	{
		return selected;
	}
	
	public void setSelected(String tab)
	{
		for ( String str : tabs )
		{
			if ( str.equalsIgnoreCase(tab) )
			{
				selected = str;
				return;
			}
		}
		return;
	}
	
	public TabPressListener getPressListener()
	{
		return listener;
	}
	
	public void setPressListener(TabPressListener listener)
	{
		this.listener = listener;
	}
	
	public Color getSeperatorColour()
	{
		return seperator;
	}
	
	public void setSeperatorColour(Color seperator)
	{
		this.seperator = seperator;
	}
	
	public String[] getTabs()
	{
		return tabs;
	}
	
	public void setText(String[] tabs)
	{
		this.tabs = tabs;
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
	
	public boolean isHovered(int tab)
	{
		if ( this.getContainer() == null )
			return false;
		
		Point mouse = this.getContainer().getMouseCoords();
		
		Rectangle bounds = getBounds(tab);
		
		return mouse.x >= bounds.x && mouse.y >= bounds.y && mouse.x <= bounds.x + bounds.width && mouse.y <= bounds.y + bounds.height;
	}
	
	public Rectangle getBounds(int tab)
	{
		Rectangle bounds = this.getBounds();
		
		int width = bounds.width / tabs.length;
		
		int x = bounds.x + ( width * tab );
		
		return new Rectangle(x, bounds.y, width, bounds.height);
	}
	
	public int getTab(int x)
	{
		Rectangle bounds = this.getBounds();
		
		x -= bounds.x;
		
		int width = bounds.width / tabs.length;
		
		int tab = (int) Math.floor((double) x / (double) width);
		
		return tab;
	}
	
	public String getTabName(int tab)
	{
		if ( tab > tabs.length )
			return tabs[tabs.length - 1];
		
		if ( tab < 0 )
			return tabs[0];
		
		return tabs[tab];
	}
	
	@Override
	public void paint(IPaintContainer paint, Graphics2D g)
	{
		if ( font != null )
			g.setFont(font);
		
		FontMetrics metrics = g.getFontMetrics();
		
		for ( int i = 0; i < tabs.length; i++ )
		{
			String tab = tabs[i];
			
			g.setColor(isHovered(i) || selected.equalsIgnoreCase(tab) ? hover : this.colour);
			
			Rectangle bounds = getBounds(i);
			
			int xoff = ( bounds.width / 2 ) - ( metrics.stringWidth(tab) / 2 );
			
			Point p = paint.toRenderCoords(bounds.x + xoff , bounds.y + ( ( bounds.height / 2 ) + ( metrics.getHeight() / 2 ) / 2 ));
			
			g.drawString(tab , p.x , p.y);
		}
		
		if ( seperator == null )
			return;
		
		g.setColor(seperator);
		
		Rectangle bounds = getBounds();
		
		int width = bounds.width / tabs.length;
		
		for ( int i = 1; i < tabs.length; i++ )
		{
			Point p1 = paint.toRenderCoords(bounds.x + ( width * i ) , bounds.y - 3);
			Point p2 = paint.toRenderCoords(bounds.x + ( width * i ) , bounds.y + bounds.height + 1);
			
			g.drawLine(p1.x , p1.y , p2.x , p2.y);
		}
	}
	
	@Override
	public void onMouseClick(Point mouse)
	{
		String tab = getTabName(getTab(mouse.x));
		
		if ( listener != null )
			listener.onPress(this , tab);
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
