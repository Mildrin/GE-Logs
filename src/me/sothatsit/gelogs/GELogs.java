package me.sothatsit.gelogs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.swing.JLabel;

import me.sothatsit.gelogs.paint.Padding;
import me.sothatsit.gelogs.paint.component.SeperatorComponent;
import me.sothatsit.gelogs.paint.component.button.ButtonComponent;
import me.sothatsit.gelogs.paint.component.button.ButtonPressListener;
import me.sothatsit.gelogs.paint.component.button.CheckBoxComponent;
import me.sothatsit.gelogs.paint.component.button.TabComponent;
import me.sothatsit.gelogs.paint.component.button.TabPressListener;
import me.sothatsit.gelogs.paint.component.text.DynamicTextComponent;
import me.sothatsit.gelogs.paint.component.text.DynamicTextFieldComponent;
import me.sothatsit.gelogs.paint.component.text.TextComponent;
import me.sothatsit.gelogs.paint.component.text.TextFieldReciever;
import me.sothatsit.gelogs.paint.component.text.TextReciever;
import me.sothatsit.gelogs.paint.container.PaintContainer;
import me.sothatsit.gelogs.paint.container.VerticalFlowLayout;
import me.sothatsit.gelogs.states.IdleState;
import me.sothatsit.gelogs.states.StateMachine;
import me.sothatsit.gelogs.states.TaskState;
import me.sothatsit.gelogs.task.Bank;
import me.sothatsit.gelogs.task.CheckToBank;
import me.sothatsit.gelogs.task.ChopTree;
import me.sothatsit.gelogs.task.FindTree;
import me.sothatsit.gelogs.task.MoveToBank;
import me.sothatsit.gelogs.task.MoveToBankWaypoint;
import me.sothatsit.gelogs.task.MoveToTrees;

import org.powerbot.script.Filter;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script.Manifest;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Interactive;
import org.powerbot.script.rt6.MobileIdNameQuery;
import org.powerbot.script.rt6.Skills;

@Manifest(name = "GE-Logs", description = "Chops and banks normal logs at the Grand Exchange for Profit.")
public class GELogs extends PollingScript< ClientContext > implements PaintListener , MouseListener , MouseMotionListener
{
	public static final Random r = new Random();
	
	public static final int[] tree_ids =
	{ 38760 , 38787 , 38760 , 38785 , 38760 , 38783 };
	public static final int log_id = 1511;
	public static final int[] banker_ids =
	{ 782 };
	public static final String banker_function = "Bank";
	public static final Tile bank_loc = new Tile(3185, 3444, 0);
	public static final Tile bank_waypoint_loc = new Tile(3176, 3449, 0);
	public static final Tile tree_loc = new Tile(3159, 3451, 0);
	public static final Tile[] banned_trees =
	{ new Tile(3144, 3472, 0) };
	
	private Filter< GameObject > reachable_filter;
	private GameObject current_tree;
	private long real_start_time;
	private long start_time;
	private long pause_time;
	private int start_exp;
	private int start_level;
	private int start_logs;
	private int logs;
	private String status;
	private StateMachine machine;
	private int log_cost;
	
	@Override
	public void start()
	{
		log("Starting");
		
		initializePaint();
		
		current_tree = null;
		machine = new StateMachine();
		start_time = pause_time = System.currentTimeMillis();
		real_start_time = start_time;
		start_exp = getCurrentExp();
		start_level = getCurrentLevel();
		start_logs = -1;
		logs = 0;
		status = " Idle ";
		
		log("Retrieving Log Price");
		
		try
		{
			log_cost = getPriceOfItem(log_id);
			
			log_price.setText("Re-Gather Log Price ( " + log_cost + "gp )");
			
			log("Log Price Retrieval Successful - " + commaString(log_cost + "") + "gp");
		}
		catch (IOException e)
		{
			log_cost = 0;
			
			log_price.setText("Re-Gather Log Price ( " + log_cost + "gp )");
			
			e.printStackTrace();
			
			log("Log Price Retrieval Failed");
		}
		
		reachable_filter = new Filter< GameObject >()
		{
			
			public boolean accept(GameObject obj)
			{
				int ox = obj.tile().x();
				int oy = obj.tile().y();
				
				for ( Tile tile : banned_trees )
				{
					if ( tile.x() == obj.tile().x() && tile.y() == obj.tile().y() )
						return false;
				}
				
				if ( ctx.movement.distance(obj.tile() , tree_loc) > 40 )
					return false;
				
				Tile tile = new Tile(ox + 1, oy, 0);
				if ( tile.matrix(ctx).reachable() )
					return true;
				
				tile = new Tile(ox + 1, oy, 0);
				if ( tile.matrix(ctx).reachable() )
					return true;
				
				tile = new Tile(ox - 1, oy, 0);
				
				if ( tile.matrix(ctx).reachable() )
					return true;
				
				tile = new Tile(ox, oy + 1, 0);
				
				if ( tile.matrix(ctx).reachable() )
					return true;
				
				tile = new Tile(ox, oy - 1, 0);
				
				return false;
			}
			
		};
		
		TaskState chop_state = new TaskState("Chop", this);
		chop_state.addTask(new CheckToBank(ctx, this));
		chop_state.addTask(new MoveToTrees(ctx, this));
		chop_state.addTask(new FindTree(ctx, this));
		chop_state.addTask(new ChopTree(ctx, this));
		
		TaskState bank_state = new TaskState("Bank", this);
		bank_state.addTask(new MoveToBankWaypoint(ctx, this));
		bank_state.addTask(new MoveToBank(ctx, this));
		bank_state.addTask(new Bank(ctx, this));
		
		IdleState idle_state = new IdleState(this);
		
		machine.addState(chop_state);
		machine.addState(bank_state);
		machine.addState(idle_state);
		
		machine.setCurrentState("Chop");
		
		this.setIdle(false);
		
		log("Startup Finished");
	}
	
	private PaintContainer paint;
	private TextComponent title;
	private SeperatorComponent seperator_1;
	
	private PaintContainer content;
	private TabComponent tab_bar;
	private SeperatorComponent seperator_2;
	
	private PaintContainer info;
	private DynamicTextFieldComponent info_field;
	private DynamicTextComponent script_status;
	
	private PaintContainer options;
	private CheckBoxComponent breaks;
	private CheckBoxComponent check_levels;
	private CheckBoxComponent price_check;
	private ButtonComponent log_price;
	
	private SeperatorComponent seperator_3;
	private ButtonComponent minimize;
	
	public String[] getLines()
	{
		return new String[]
		{
				"Time Running: " + getTimeSinceStartString() ,
				"Levels:" ,
				" - Current WC Level: " + getCurrentLevel() ,
				" - Exp Per Hour: " + GELogs.commaString(getExpPerHour() + "") ,
				" - Exp TNL: " + GELogs.commaString(getExpTNL() + "") ,
				" - Time TNL: " + getTimeTNL() ,
				" - Total Exp Gained: " + GELogs.commaString(getGainedExp() + "") ,
				" - Total Levels Gained: " + getGainedLevels() ,
				"" ,
				"Logs: " ,
				" - Logs Per Hour: " + GELogs.commaString(getLogsPerHour() + "") ,
				" - Total Logs: " + GELogs.commaString(getLogs() + "") ,
				" - Total Logs Gained: " + GELogs.commaString( ( getStartLogs() < 0 ? getLogs() : getGainedLogs() ) + "") ,
				"" ,
				"Profit: " ,
				" - All Logs Value: " + GELogs.commaString(getAllLogsValue() + "") + "gp" ,
				" - Total Profit: " + GELogs.commaString(getTotalProfit() + "") + "gp" ,
				" - Profit Per Hour: " + GELogs.commaString(getProfitPerHour() + "") + "gp" ,
				" - Profit Per Log: " + getLogCost() + "gp" ,
				""
		};
	}
	
	public void initializePaint()
	{
		paint = new PaintContainer(0, 0, 300, 0);
		paint.setLayout(new VerticalFlowLayout());
		Rectangle paint_bounds = paint.getBounds();
		
		title = new TextComponent(0, 0, paint_bounds.width, 20, "Grand Exchange Woodcutting - By Mildrin");
		title.setCentreText(true);
		title.setFont(new JLabel().getFont().deriveFont(Font.BOLD));
		
		seperator_1 = new SeperatorComponent(0, 0, 0, 1);
		seperator_1.setFillContainer(true);
		
		content = new PaintContainer(0, 0, paint_bounds.width, 0);
		content.setLayout(new VerticalFlowLayout());
		content.setBorderColour(null);
		content.setBackgroundColour(null);
		
		tab_bar = new TabComponent(0, 0, paint_bounds.width, 20, new String[]
		{
				"Script Info" ,
				"Script Options"
		});
		tab_bar.setPressListener(new TabPressListener()
		{
			
			@Override
			public void onPress(TabComponent component, String tab)
			{
				String[] tabs = component.getTabs();
				
				if ( tab.equalsIgnoreCase(tabs[0]) )
				{
					tab_bar.setSelected(tabs[0]);
					options.setVisible(false);
					info.setVisible(true);
					content.organize();
					return;
				}
				
				if ( tab.equalsIgnoreCase(tabs[1]) )
				{
					tab_bar.setSelected(tabs[1]);
					info.setVisible(false);
					options.setVisible(true);
					content.organize();
					return;
				}
			}
			
		});
		
		seperator_2 = new SeperatorComponent(0, 0, 0, 1);
		seperator_2.setFillContainer(true);
		seperator_2.setPadding(new Padding(1, 1, 1, 0));
		
		info = new PaintContainer(0, 0, paint_bounds.width, 0);
		info.setLayout(new VerticalFlowLayout());
		info.setBorderColour(null);
		info.setBackgroundColour(null);
		
		info_field = new DynamicTextFieldComponent(0, 0, paint_bounds.width - 5, getLines().length * 20, 20, new TextFieldReciever()
		{
			@Override
			public String[] getText()
			{
				return getLines();
			}
		});
		info_field.setPadding(new Padding(5, 0, 0, 0));
		
		script_status = new DynamicTextComponent(0, 0, paint_bounds.width - 5, 20, new TextReciever()
		{
			@Override
			public String getText()
			{
				return "Status: " + ( getStatus() != null ? getStatus().trim() : "Starting" );
			}
		});
		script_status.setPadding(new Padding(5, 0, 0, 0));
		
		options = new PaintContainer(0, 0, paint_bounds.width, 0);
		options.setBorderColour(null);
		options.setBackgroundColour(null);
		options.setVisible(false);
		options.setLayout(new VerticalFlowLayout());
		
		breaks = new CheckBoxComponent(0, 0, paint_bounds.width - 5, 20, 20, "Take 10s - 4m Breaks");
		breaks.setPadding(new Padding(5, 0, 5, 5));
		breaks.setBorderColour(null);
		
		check_levels = new CheckBoxComponent(0, 0, paint_bounds.width - 5, 20, 20, "Check Woodcutting Levels");
		check_levels.setPadding(new Padding(5, 0, 5, 5));
		check_levels.setBorderColour(null);
		
		price_check = new CheckBoxComponent(0, 0, paint_bounds.width - 5, 20, 20, "Check Price of Banked Logs");
		price_check.setPadding(new Padding(5, 0, 5, 5));
		price_check.setBorderColour(null);
		
		log_price = new ButtonComponent(0, 0, paint_bounds.width - 10, 20, "Re-Gather Log Price ( 0gp )");
		log_price.setPadding(new Padding(5, 5, 5, 5));
		log_price.setPressListener(new ButtonPressListener()
		{
			@Override
			public void onPress(ButtonComponent component)
			{
				try
				{
					log("Retrieving Log Price");
					
					log_price.setText("Re-Gather Log Price ( Gathering... )");
					
					int cost = getPriceOfItem(log_id);
					
					if ( cost <= 0 )
					{
						log("Log Price Retrieval Failed");
						return;
					}
					
					log_cost = cost;
					
					log_price.setText("Re-Gather Log Price ( " + log_cost + "gp )");
					
					log("Log Price Retrieval Successful - " + commaString(log_cost + "") + "gp");
				}
				catch (IOException e)
				{
					e.printStackTrace();
					
					log("Log Price Retrieval Failed");
				}
			}
		});
		
		seperator_3 = new SeperatorComponent(0, 0, 0, 1);
		seperator_3.setFillContainer(true);
		
		minimize = new ButtonComponent(0, 0, paint_bounds.width, 20, "Minimize");
		minimize.setBorderColour(null);
		minimize.setPressListener(new ButtonPressListener()
		{
			
			@Override
			public void onPress(ButtonComponent component)
			{
				if ( component.getText().equalsIgnoreCase("minimize") )
				{
					component.setText("Expand");
					content.setVisible(false);
					paint.organize();
					
				}
				else
				{
					component.setText("Minimize");
					content.setVisible(true);
					paint.organize();
				}
			}
			
		});
		
		info.add(info_field);
		info.add(script_status);
		
		options.add(breaks);
		options.add(check_levels);
		options.add(price_check);
		options.add(log_price);
		
		content.add(tab_bar);
		content.add(seperator_2);
		content.add(info);
		content.add(options);
		content.add(seperator_3);
		
		paint.add(title);
		paint.add(seperator_1);
		paint.add(content);
		paint.add(minimize);
	}
	
	public void log(String log)
	{
		System.out.println("[INFO] GELogs: " + log);
	}
	
	public int getLogCost()
	{
		return log_cost;
	}
	
	public int getTotalProfit()
	{
		return log_cost * getGainedLogs();
	}
	
	public int getAllLogsValue()
	{
		return log_cost * getLogs();
	}
	
	public int getProfitPerHour()
	{
		return (int) ( getTotalProfit() / ( getTimeSinceStartSeconds() / 3600d ) );
	}
	
	public GameObject getBank()
	{
		return ctx.objects.select().nearest().limit(3).poll();
	}
	
	public MobileIdNameQuery< GameObject > getBanks()
	{
		return ctx.objects.select().id(GELogs.banker_ids).within(20).select(Interactive.areInViewport());
	}
	
	public long getRealStartTime()
	{
		return real_start_time;
	}
	
	public boolean isIdle()
	{
		return pause_time >= 0;
	}
	
	public void setIdle(boolean idle)
	{
		if ( idle && !isIdle() )
		{
			pause_time = System.currentTimeMillis();
		}
		else
			if ( !idle && isIdle() )
			{
				long diff = System.currentTimeMillis() - pause_time;
				pause_time = -1;
				start_time += diff;
			}
	}
	
	public boolean isInvFull()
	{
		return ctx.backpack.count() >= 28;
	}
	
	public boolean isPlayerIdle()
	{
		return ctx.players.local().animation() == -1;
	}
	
	@Override
	public void suspend()
	{
		machine.setCurrentState("Idle");
	}
	
	@Override
	public void resume()
	{
		machine.setCurrentState("Chop");
	}
	
	public int getPriceOfItem(int id) throws IOException
	{
		URL url = new URL("http://services.runescape.com/m=itemdb_rs/viewitem.ws?obj=" + id);
		
		URLConnection con = url.openConnection();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		String line;
		while ( ( line = in.readLine() ) != null)
		{
			if ( line.startsWith("<h3>Current Guide Price <span>") )
			{
				
				String price = line.substring(line.indexOf("<span>") + 6 , line.indexOf("</span>"));
				
				price = price.replace("," , "");
				
				try
				{
					
					return Integer.parseInt(price);
					
				}
				catch (NumberFormatException e)
				{
					
					return 0;
					
				}
			}
		}
		
		return 0;
	}
	
	public StateMachine getStateMachine()
	{
		return machine;
	}
	
	public void setStatus(String status)
	{
		if ( !this.status.equals(status) )
			log("Status Updated: " + status.trim());
		this.status = status;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStartLogs(int logs)
	{
		start_logs = logs;
	}
	
	public void setLogs(int logs)
	{
		this.logs = logs;
	}
	
	public int getLogs()
	{
		return logs + ctx.backpack.select().id(log_id).count();
	}
	
	public int getGainedLogs()
	{
		return getLogs() - ( start_logs < 0 ? 0 : start_logs );
	}
	
	public Filter< GameObject > getReachableFilter()
	{
		return reachable_filter;
	}
	
	public boolean goToWaypoint()
	{
		return ctx.players.local().tile().x() < getBorderX();
	}
	
	public int getBorderX()
	{
		return bank_waypoint_loc.x() - 5;
	}
	
	public Tile getRandBankLoc()
	{
		Tile tile = null;
		while (tile == null)
		{
			int x = bank_loc.x();
			int y = bank_loc.y();
			x += r.nextInt(5) - 2;
			y += r.nextInt(5) - 2;
			tile = new Tile(x, y, 0);
			if ( !tile.matrix(ctx).reachable() )
				tile = null;
		}
		
		return tile;
	}
	
	public Tile getRandBankWaypointLoc()
	{
		Tile tile = null;
		while (tile == null)
		{
			int x = bank_waypoint_loc.x();
			int y = bank_waypoint_loc.y();
			x += r.nextInt(5) - 2;
			y += r.nextInt(5) - 2;
			tile = new Tile(x, y, 0);
			if ( !tile.matrix(ctx).reachable() )
				tile = null;
		}
		
		return tile;
	}
	
	public Tile getRandTreeLoc()
	{
		Tile tile = null;
		while (tile == null)
		{
			int x = tree_loc.x();
			int y = tree_loc.y();
			x += r.nextInt(5) - 2;
			y += r.nextInt(5) - 2;
			tile = new Tile(x, y, 0);
			if ( !tile.matrix(ctx).reachable() )
				tile = null;
		}
		
		return tile;
	}
	
	public long getStartTime()
	{
		return start_time;
	}
	
	public int getStartLogs()
	{
		return start_logs;
	}
	
	public long getTimeSinceStart()
	{
		long time = System.currentTimeMillis();
		return ( time - start_time ) - ( this.pause_time < 0 ? 0 : time - pause_time );
	}
	
	public double getTimeSinceStartSeconds()
	{
		return Math.floor(getTimeSinceStart() / 1000d);
	}
	
	public String getTimeSinceStartString()
	{
		int seconds = (int) getTimeSinceStartSeconds();
		int minutes = (int) Math.floor(seconds / 60d);
		int hours = (int) Math.floor(minutes / 60d);
		int days = (int) Math.floor(hours / 24d);
		
		seconds -= minutes * 60d;
		minutes -= hours * 60d;
		hours -= days * 24d;
		
		return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
	}
	
	public int getGainedExp()
	{
		return getCurrentExp() - start_exp;
	}
	
	public int getGainedLevels()
	{
		return getCurrentLevel() - start_level;
	}
	
	public int getCurrentExp()
	{
		return ctx.skills.experience(Skills.WOODCUTTING);
	}
	
	public int getCurrentLevel()
	{
		return ctx.skills.realLevel(Skills.WOODCUTTING);
	}
	
	public int getExpPerHour()
	{
		return (int) ( getGainedExp() / ( getTimeSinceStartSeconds() / 3600d ) );
	}
	
	public int getLogsPerHour()
	{
		return (int) ( getGainedLogs() / ( getTimeSinceStartSeconds() / 3600d ) );
	}
	
	public int getExpTNL()
	{
		return getExp(getCurrentLevel() + 1) - getCurrentExp();
	}
	
	public double getTimeTNLSeconds()
	{
		int perhour = getExpPerHour();
		
		return Math.floor( ( getExpTNL() / ( perhour == 0 ? 1 : perhour ) ) * 3600d);
	}
	
	public String getTimeTNL()
	{
		int seconds = (int) getTimeTNLSeconds();
		int minutes = (int) Math.floor(seconds / 60d);
		int hours = (int) Math.floor(minutes / 60d);
		int days = (int) Math.floor(hours / 24d);
		
		seconds -= minutes * 60d;
		minutes -= hours * 60d;
		hours -= days * 24d;
		
		return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
	}
	
	public GameObject getCurrentTree()
	{
		return current_tree;
	}
	
	public void setCurrentTree(GameObject tree)
	{
		current_tree = tree;
	}
	
	@Override
	public void poll()
	{
		machine.poll();
	}
	
	public static int rand(int min, int max)
	{
		return r.nextInt(max - min) + min;
	}
	
	public static void sleep(int min, int max)
	{
		sleep(rand(min , max));
	}
	
	public static void sleep(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void repaint(final Graphics g)
	{
		if ( paint != null )
			paint.paint(null , (Graphics2D) g);
	}
	
	public static String commaString(String str)
	{
		if ( str.length() <= 3 )
			return str;
		
		str = str.substring(0 , str.length() - 3) + "," + str.substring(str.length() - 3);
		
		if ( str.length() <= 7 )
			return str;
		
		str = str.substring(0 , str.length() - 7) + "," + str.substring(str.length() - 7);
		
		if ( str.length() <= 11 )
			return str;
		
		str = str.substring(0 , str.length() - 11) + "," + str.substring(str.length() - 11);
		
		return str;
	}
	
	public static int getExp(int level)
	{
		int exp = 0;
		
		for ( int i = 1; i < level; i++ )
		{
			exp += (int) Math.floor(i + ( 300 * Math.pow(2 , ( i / 7d )) ));
		}
		
		return (int) Math.floor(exp / 4d);
	}
	
	public int mousex = 0;
	public int mousey = 0;
	
	public int mouse_down_x = 0;
	public int mouse_down_y = 0;
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		mousex = e.getX();
		mousey = e.getY();
		
		paint.setMouseCoords(new Point(mousex, mousey));
		
		paint.onMouseClick(new Point(mousex, mousey));
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		mousex = e.getX();
		mousey = e.getY();
		
		paint.setMouseCoords(new Point(mousex, mousey));
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		mousex = e.getX();
		mousey = e.getY();
		
		paint.setMouseCoords(new Point(mousex, mousey));
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		mousex = mouse_down_x = e.getX();
		mousey = mouse_down_y = e.getY();
		
		paint.setMouseCoords(new Point(mousex, mousey));
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		mousex = e.getX();
		mousey = e.getY();
		
		paint.setMouseCoords(new Point(mousex, mousey));
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		Dimension screen = ctx.game.dimensions();
		
		if ( paint == null )
			return;
		
		if ( !paint.inBounds(mouse_down_x , mouse_down_y) )
			return;
		
		int difx = e.getX() - mouse_down_x;
		int dify = e.getY() - mouse_down_y;
		
		Point from = new Point(mousex, mousey);
		
		mousex = e.getX();
		mousey = e.getY();
		
		Point to = new Point(mousex, mousey);
		
		paint.setMouseCoords(new Point(mousex, mousey));
		
		paint.onMouseDrag(from , to);
		
		Rectangle bounds = paint.getOriginalBounds();
		bounds.x += difx;
		bounds.y += dify;
		
		Point far = new Point(bounds.x + bounds.width, bounds.y + bounds.height);
		
		if ( bounds.x < 0 )
			bounds.x = 0;
		else
			if ( far.x > screen.width - 2 )
				bounds.x = screen.width - bounds.width - 2;
			else
				mouse_down_x = mousex;
		
		if ( bounds.y < 0 )
			bounds.y = 0;
		else
		{
			if ( bounds.y + bounds.height > screen.height - 2 )
				bounds.y = screen.height - bounds.height - 2;
			else
				mouse_down_y = mousey;
		}
		
		paint.setLocation(bounds.x , bounds.y);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		mousex = e.getX();
		mousey = e.getY();
		
		paint.setMouseCoords(new Point(mousex, mousey));
	}
}
