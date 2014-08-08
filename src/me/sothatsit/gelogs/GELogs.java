package me.sothatsit.gelogs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.sothatsit.gelogs.task.Bank;
import me.sothatsit.gelogs.task.ChopTree;
import me.sothatsit.gelogs.task.FindTree;
import me.sothatsit.gelogs.task.MoveToBank;
import me.sothatsit.gelogs.task.MoveToTrees;
import me.sothatsit.gelogs.task.Task;

import org.powerbot.script.Filter;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script.Manifest;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Skills;

@Manifest(name = "GE Logs", description = "Chops and banks normal logs at the Grand Exchange.")
public class GELogs extends PollingScript< ClientContext > implements PaintListener
{
	public static final Random r = new Random();
	public static final boolean DEBUG = false;
	
	public static final int[] tree_ids =
	{ 38760 , 38787 , 38760 , 38785 , 38760 , 38783 };
	public static final int tree_item_id = 1511;
	public static final int[] banker_ids =
	{ 782 };
	public static final String banker_function = "Bank";
	public static final Tile bank_loc = new Tile(3185, 3444, 0);
	public static final Tile tree_loc = new Tile(3169, 3451, 0);
	public static final Tile[] banned_trees =
	{ new Tile(3144, 3472, 0) };
	
	private static Filter< GameObject > reachable_filter;
	private static GELogs instance;
	private static GameObject current_tree;
	private static List< Task > tasks;
	private static long start_time;
	private static int start_exp;
	private static int start_level;
	private static int start_logs;
	private static int logs;
	private static String status;
	
	@Override
	public void start()
	{
		instance = this;
		
		current_tree = null;
		tasks = new ArrayList< Task >();
		start_time = System.currentTimeMillis();
		start_exp = getCurrentExp();
		start_level = getCurrentLevel();
		start_logs = -1;
		logs = 0;
		status = " Starting ";
		
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
		
		ctx.movement.findPath(GELogs.getRandTreeLoc()).traverse();
		
		tasks.addAll(Arrays.asList(new MoveToTrees(ctx) , new FindTree(ctx) , new ChopTree(ctx) , new MoveToBank(ctx) , new Bank(ctx)));
	}
	
	@Override
	public void stop()
	{
		tasks.clear();
		current_tree = null;
	}
	
	@Override
	public void suspend()
	{
		
	}
	
	@Override
	public void resume()
	{
		
	}
	
	public static void setStatus(String status)
	{
		GELogs.status = status;
	}
	
	public static String getStatus()
	{
		return status;
	}
	
	public static void setStartLogs(int logs)
	{
		start_logs = logs;
	}
	
	public static void setLogs(int logs)
	{
		GELogs.logs = logs;
	}
	
	public static int getLogs()
	{
		return logs + getCtx().backpack.select().id(tree_item_id).count();
	}
	
	public static int getGainedLogs()
	{
		return getLogs() - start_logs;
	}
	
	public static Filter< GameObject > getReachableFilter()
	{
		return reachable_filter;
	}
	
	public static GELogs instance()
	{
		return instance;
	}
	
	public static Tile getRandBankLoc()
	{
		Tile tile = null;
		while (tile == null)
		{
			int x = bank_loc.x();
			int y = bank_loc.y();
			x += r.nextInt(5) - 2;
			y += r.nextInt(5) - 2;
			tile = new Tile(x, y, 0);
			if ( !tile.matrix(getCtx()).reachable() )
				tile = null;
		}
		
		return tile;
	}
	
	public static Tile getRandTreeLoc()
	{
		Tile tile = null;
		while (tile == null)
		{
			int x = tree_loc.x();
			int y = tree_loc.y();
			x += r.nextInt(5) - 2;
			y += r.nextInt(5) - 2;
			tile = new Tile(x, y, 0);
			if ( !tile.matrix(getCtx()).reachable() )
				tile = null;
		}
		
		return tile;
	}
	
	public static ClientContext getCtx()
	{
		return instance.ctx;
	}
	
	public static long getStartTime()
	{
		return start_time;
	}
	
	public static int getStartLogs()
	{
		return start_logs;
	}
	
	public static long getTimeSinceStart()
	{
		return System.currentTimeMillis() - start_time;
	}
	
	public static double getTimeSinceStartSeconds()
	{
		return Math.floor(getTimeSinceStart() / 1000d);
	}
	
	public static String getTimeSinceStartString()
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
	
	public static int getGainedExp()
	{
		return getCurrentExp() - start_exp;
	}
	
	public static int getGainedLevels()
	{
		return getCurrentLevel() - start_level;
	}
	
	public static int getCurrentExp()
	{
		return getCtx().skills.experience(Skills.WOODCUTTING);
	}
	
	public static int getCurrentLevel()
	{
		return getCtx().skills.realLevel(Skills.WOODCUTTING);
	}
	
	public static int getExpPerHour()
	{
		return (int) ( getGainedExp() / ( getTimeSinceStartSeconds() / 3600d ) );
	}
	
	public static int getLogsPerHour()
	{
		return (int) ( getGainedLogs() / ( getTimeSinceStartSeconds() / 3600d ) );
	}
	
	public static GameObject getCurrentTree()
	{
		return current_tree;
	}
	
	public static void setCurrentTree(GameObject tree)
	{
		current_tree = tree;
	}
	
	@Override
	public void poll()
	{
		for ( Task task : tasks )
		{
			if ( task.activate() )
			{
				task.execute();
			}
		}
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
	
	public void repaint(final Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		
		int width = 275;
		int height = 220;
		
		// transparent black
		g.setColor(new Color(0, 0, 0, 100));
		
		// background
		g.fillRect(1 , 1 , width , height);
		
		// transparent white
		g.setColor(new Color(255, 255, 255, 100));
		
		// background border
		g.drawRect(0 , 0 , width + 1 , height + 1);
		
		// line under name of script
		g.drawLine(2 , 20 , width - 1 , 20);
		
		// opaque white
		g.setColor(new Color(255, 255, 255));
		
		g.drawString("Grand Exchange Woodcutting - By Mildrin" , 5 , 15);
		
		if ( instance == null || getCtx() == null )
			return;
		
		g.drawString("Time Running: " + GELogs.getTimeSinceStartString() , 5 , 35);
		
		g.drawString("Exp Per Hour: " + commaString(GELogs.getExpPerHour() + "") , 5 , 55);
		
		g.drawString("Logs Per Hour: " + commaString(GELogs.getLogsPerHour() + "") , 5 , 75);
		
		g.drawString("Total Logs: " + commaString(GELogs.getLogs() + "") , 5 , 95);
		
		g.drawString("Total Logs Gained: " + commaString( ( GELogs.getStartLogs() < 0 ? GELogs.getLogs() : GELogs.getGainedLogs() ) + "") , 5 , 115);
		
		g.drawString("Current WC Level: " + GELogs.getCurrentLevel() , 5 , 135);
		
		g.drawString("Total Levels Gained: " + GELogs.getGainedLevels() , 5 , 155);
		
		g.drawString("Total Exp Gained: " + commaString(GELogs.getGainedExp() + "") , 5 , 175);
		
		g.drawString("" , 5 , 195);
		
		g.drawString("( " + status + " )" , 5 , 215);
		
		g.dispose();
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
}
