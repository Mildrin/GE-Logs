package me.sothatsit.gelogs.paint;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

public class Paint extends ClientAccessor
{
	
	private GELogs main;
	private Color background_colour;
	private Color line_colour;
	private Color text_colour;
	private Color hover_colour;
	
	private Rectangle bounds;
	private boolean open;
	
	public Paint( ClientContext context , GELogs main )
	{
		super(context);
		
		this.main = main;
		this.background_colour = new Color(0, 0, 0, 100);
		this.line_colour = new Color(255, 255, 255, 100);
		this.text_colour = new Color(255, 255, 255);
		this.hover_colour = new Color(180, 180, 180);
		
		this.bounds = new Rectangle(1, 1, 275, getHeight());
		this.open = true;
	}
	
	public Point getMouseCoords()
	{
		return new Point(main.mousex, main.mousey);
	}
	
	public Color getBackgroundColour()
	{
		return background_colour;
	}
	
	public Color getLineColour()
	{
		return line_colour;
	}
	
	public Color getTextColour()
	{
		return text_colour;
	}
	
	public Color getHoverColour()
	{
		return hover_colour;
	}
	
	public void setBackgroundColour(Color colour)
	{
		this.background_colour = colour;
	}
	
	public void setLineColour(Color colour)
	{
		this.line_colour = colour;
	}
	
	public void setTextColour(Color colour)
	{
		this.text_colour = colour;
	}
	
	public void setHoverColour(Color colour)
	{
		this.hover_colour = colour;
	}
	
	public Point toPaintCoords(int x, int y)
	{
		return new Point(bounds.x + x, bounds.y + y);
	}
	
	public boolean isOpen()
	{
		return open;
	}
	
	public void setOpen(boolean open)
	{
		bounds.height = getHeight();
		
		this.open = open;
	}
	
	public void setLocation(int x, int y)
	{
		bounds.x = x;
		bounds.y = y;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	private int getHeight()
	{
		return !open ? 60 + ( getLines().length * 20 ) : 40;
	}
	
	public boolean inBounds(int x, int y)
	{
		return x >= bounds.x && y >= bounds.y && x <= bounds.x + bounds.width && y <= bounds.x + bounds.height;
	}
	
	public boolean inOpenBounds(int x, int y)
	{
		return x >= bounds.x && y >= bounds.y + bounds.height - 23 && x <= bounds.x + bounds.width && y <= bounds.x + bounds.height;
	}
	
	public void repaint(final Graphics2D g)
	{
		Rectangle bounds = (Rectangle) this.bounds.clone();
		
		// transparent black
		g.setColor(new Color(0, 0, 0, 100));
		
		// background
		g.fillRect(bounds.x , bounds.y , bounds.width , bounds.height);
		
		// transparent white
		g.setColor(new Color(255, 255, 255, 100));
		
		// background border
		g.drawRect(bounds.x - 1 , bounds.y - 1 , bounds.width + 1 , bounds.height + 1);
		
		// line under name of script
		g.drawLine(bounds.x , bounds.y + 20 , bounds.x + bounds.width - 1 , bounds.y + 20);
		
		if ( open )
		{
			// line above close
			g.drawLine(bounds.x , bounds.y + bounds.height - 20 , bounds.x + bounds.width - 1 , bounds.y + bounds.height - 20);
			
			// line below tabs
			g.drawLine(bounds.x , bounds.y + 40 , bounds.x + bounds.width - 1 , bounds.y + 40);
			
			// line separating tabs
			g.drawLine(bounds.x + ( bounds.width / 2 ) , bounds.y + 21 , bounds.x + ( bounds.width / 2 ) , bounds.y + 39);
		}
		
		// opaque white if not hovered over else opaque gray
		g.setColor(!this.inOpenBounds(main.mousex , main.mousey) ? new Color(255, 255, 255) : new Color(180, 180, 180));
		
		FontMetrics font_metrics = g.getFontMetrics();
		
		String option = ( open ? "Minimize" : "Expand" );
		
		g.drawString(option , bounds.x + ( bounds.width / 2 ) - ( font_metrics.stringWidth(option) / 2 ) , bounds.y + bounds.height - 7);
		
		if ( !open )
		{
			// opaque white
			g.setColor(new Color(255, 255, 255));
			
			g.drawString("Grand Exchange Woodcutting - By Mildrin" , bounds.x + 5 , bounds.y + 15);
			
			g.dispose();
			return;
		}
		
		// opaque white
		g.setColor(new Color(255, 255, 255));
		
		g.drawString("Grand Exchange Woodcutting - By Mildrin" , bounds.x + 5 , bounds.y + 15);
		
		int y = 55;
		for ( String str : getLines() )
		{
			g.drawString(str , bounds.x + 5 , bounds.y + y);
			y += 20;
		}
		
		// Tab names
		
		g.dispose();
	}
	
	public String[] getLines()
	{
		return new String[]
		{
				"Time Running: " + main.getTimeSinceStartString() ,
				"Exp Per Hour: " + GELogs.commaString(main.getExpPerHour() + "") ,
				"Logs Per Hour: " + GELogs.commaString(main.getLogsPerHour() + "") ,
				"Profit Per Hour: " + GELogs.commaString(main.getProfitPerHour() + "") ,
				"Total Logs: " + GELogs.commaString(main.getLogs() + "") ,
				"Total Logs Gained: " + GELogs.commaString( ( main.getStartLogs() < 0 ? main.getLogs() : main.getGainedLogs() ) + "") ,
				"Total Profit: " + GELogs.commaString(main.getTotalProfit() + "") ,
				"Current WC Level: " + main.getCurrentLevel() ,
				"Total Levels Gained: " + main.getGainedLevels() ,
				"Total Exp Gained: " + GELogs.commaString(main.getGainedExp() + "") ,
				"Price Per Log: " + main.getLogCost() ,
				"" ,
				"( " + ( main.getStatus() != null ? main.getStatus().trim() : "Starting" ) + " )"
		};
	}
}
