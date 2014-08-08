package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

public class ChopTree extends Task
{
	
	public ChopTree( ClientContext ctx )
	{
		super(ctx);
	}
	
	@Override
	public boolean activate()
	{
		return ctx.backpack.select().count() < 28 && GELogs.getCurrentTree() != null && GELogs.getCurrentTree().inViewport();
	}
	
	@Override
	public void execute()
	{
		GameObject tree = GELogs.getCurrentTree();
		
		int timeout = 50;
		while (ctx.players.local().animation() == -1 && timeout > 0)
		{
			GELogs.setStatus("Interacting Tree " + ( GELogs.DEBUG ? (int) Math.ceil(timeout / 5) : "" ));
			
			if ( timeout % 5 == 0 )
				tree.interact("Chop");
			
			GELogs.sleep(200 , 300);
			timeout--;
		}
		
		timeout = 50;
		while (ctx.players.local().animation() != -1 && timeout > 0)
		{
			GELogs.setStatus("Chopping Tree " + ( GELogs.DEBUG ? (int) Math.ceil(timeout / 5) : "" ));
			
			GELogs.sleep(200 , 300);
			timeout--;
		}
		
		GELogs.setCurrentTree(null);
	}
	
}
