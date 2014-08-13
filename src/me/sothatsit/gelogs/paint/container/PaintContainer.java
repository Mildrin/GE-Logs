package me.sothatsit.gelogs.paint.container;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.sothatsit.gelogs.paint.Padding;
import me.sothatsit.gelogs.paint.component.IPaintComponent;
import me.sothatsit.gelogs.paint.component.PaintComponent;

public class PaintContainer extends PaintComponent implements IPaintContainer
{
	
	private List< IPaintComponent > components;
	private IPaintLayout layout;
	private Color border;
	private Color background;
	
	private int mousex;
	private int mousey;
	
	public PaintContainer( int x , int y , int width , int height )
	{
		super(new Rectangle(x, y, width, height), new Padding(1, 1, 1, 1));
		
		this.components = new CopyOnWriteArrayList< IPaintComponent >();
		this.background = new Color(0, 0, 0, 100);
		this.border = new Color(255, 255, 255, 100);
		
		this.layout = null;
	}
	
	public PaintContainer()
	{
		this(0, 0, 1, 1);
	}
	
	public PaintContainer( int x , int y , int width , int height , IPaintContainer super_container )
	{
		this(x, y, width, height);
	}
	
	public IPaintLayout getLayout()
	{
		return layout;
	}
	
	public void setLayout(IPaintLayout layout)
	{
		this.layout = layout;
	}
	
	public Color getBackgroundColour()
	{
		return background;
	}
	
	public void setBackgroundColour(Color colour)
	{
		this.background = colour;
	}
	
	@Override
	public void paint(IPaintContainer paint, Graphics2D g)
	{
		if ( !valid() )
			return;
		
		Rectangle bounds = getBounds();
		Point render = toRenderCoords(0 , 0);
		
		if ( background != null )
		{
			g.setColor(background);
			
			g.fillRect(render.x , render.y , bounds.width , bounds.height);
		}
		
		if ( border != null )
		{
			g.setColor(border);
			
			g.drawRect(render.x , render.y , bounds.width , bounds.height);
		}
		
		for ( IPaintComponent component : components )
		{
			if ( component.valid() )
				component.paint(this , g);
		}
	}
	
	@Override
	public void organize()
	{
		List< IPaintComponent > components = new ArrayList< IPaintComponent >(this.components);
		clear();
		
		for ( IPaintComponent comp : components )
		{
			add(comp);
		}
		
		if ( this.getContainer() != null )
			this.getContainer().organize();
	}
	
	@Override
	public void add(IPaintComponent... components)
	{
		for ( IPaintComponent component : components )
		{
			if ( layout != null )
			{
				Point p = layout.getAvailableSpace(this , component);
				
				component.setLocation(p.x , p.y);
			}
			
			this.components.add(component);
			
			if ( layout != null )
			{
				Dimension size = layout.getContainerSize(this);
				
				this.setSize(size.width , size.height);
			}
			
			component.setContainer(this);
		}
	}
	
	@Override
	public void remove(IPaintComponent... components)
	{
		this.components.remove(Arrays.asList(components));
	}
	
	@Override
	public void clear()
	{
		components.clear();
	}
	
	@Override
	public List< IPaintComponent > getComponents()
	{
		return new ArrayList< IPaintComponent >(components);
	}
	
	@Override
	public IPaintComponent[] getComponents(int x, int y)
	{
		List< IPaintComponent > components = new ArrayList< IPaintComponent >();
		
		for ( IPaintComponent c : this.components )
		{
			if ( !c.valid() )
				continue;
			
			if ( c.inBounds(x , y) )
				components.add(c);
		}
		
		return components.toArray(new IPaintComponent[0]);
	}
	
	@Override
	public Point toRenderCoords(int x, int y)
	{
		Rectangle bounds = getBounds();
		
		Point p = new Point(bounds.x + x, bounds.y + y);
		
		if ( this.getContainer() != null )
			p = this.getContainer().toRenderCoords(p.x , p.y);
		
		return p;
	}
	
	@Override
	public Point fromRenderCoords(int x, int y)
	{
		Rectangle bounds = getBounds();
		
		Point p = new Point(x - bounds.x, y - bounds.y);
		
		if ( this.getContainer() != null )
			p = this.getContainer().fromRenderCoords(p.x , p.y);
		
		return p;
	}
	
	public Color getBorderColour()
	{
		return border;
	}
	
	public void setBorderColour(Color border)
	{
		this.border = border;
	}
	
	@Override
	public void onMouseClick(Point mouse)
	{
		Rectangle bounds = getBounds();
		
		mouse.x -= bounds.x;
		mouse.y -= bounds.y;
		
		IPaintComponent[] components = this.getComponents(mouse.x , mouse.y);
		
		for ( IPaintComponent component : components )
		{
			component.onMouseClick(mouse);
		}
	}
	
	@Override
	public void onMouseDrag(Point from, Point to)
	{
		from = fromRenderCoords(from.x , from.y);
		to = fromRenderCoords(to.x , to.y);
		
		IPaintComponent[] components = this.getComponents(to.x , to.y);
		
		for ( IPaintComponent component : components )
		{
			component.onMouseDrag(from , to);
		}
	}
	
	@Override
	public Point getMouseCoords()
	{
		return new Point(mousex, mousey);
	}
	
	@Override
	public void setMouseCoords(Point mouse)
	{
		Rectangle bounds = getBounds();
		
		this.mousex = mouse.x - bounds.x;
		this.mousey = mouse.y - bounds.y;
		
		for ( IPaintComponent component : components )
		{
			if ( component instanceof IPaintContainer )
				( (IPaintContainer) component ).setMouseCoords(getMouseCoords());
		}
	}
}
