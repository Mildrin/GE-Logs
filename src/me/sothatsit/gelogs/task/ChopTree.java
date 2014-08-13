package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

public class ChopTree extends Task
{
	
	private GELogs main;
	
	public ChopTree( ClientContext ctx , GELogs main )
	{
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate()
	{
		return !main.isInvFull() && main.getCurrentTree() != null && main.getCurrentTree().inViewport();
	}
	
	@Override
	public void execute()
	{
		GameObject tree = main.getCurrentTree();
		
		int timeout = 30;
		while (ctx.players.local().animation() == -1 && timeout > 0)
		{
			main.setStatus("Interacting Tree");
			
			if ( timeout % 5 == 0 )
				tree.interact("Chop");
			
			GELogs.sleep(200 , 300);
			timeout--;
		}
		
		timeout = 50;
		while (ctx.players.local().animation() != -1 && timeout > 0)
		{
			main.setStatus("Chopping Tree");
			
			GELogs.sleep(200 , 300);
			timeout--;
		}
		
		main.setCurrentTree(null);
	}
	
}
