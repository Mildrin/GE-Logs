package me.sothatsit.gelogs.task;

import java.util.Iterator;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Item;

public class Bank extends Task
{
	
	public Bank( ClientContext ctx )
	{
		super(ctx);
	}
	
	@Override
	public boolean activate()
	{
		return ctx.backpack.select().count() >= 28 &&
				!ctx.objects.select().id(GELogs.banker_ids).isEmpty() &&
				ctx.objects.nearest().poll().inViewport() &&
				ctx.players.local().animation() == -1;
	}
	
	@Override
	public void execute()
	{
		GELogs.setStatus("Banking Inventory");
		
		GameObject bank = ctx.objects.poll();
		
		ctx.camera.turnTo(bank);
		
		bank.interact(GELogs.banker_function);
		
		ctx.bank.open();
		
		if ( GELogs.getStartLogs() < 0 )
		{
			Iterator< Item > items = ctx.bank.select().id(GELogs.tree_item_id).iterator();
			
			int logs = 0;
			
			while (items.hasNext())
			{
				Item i = items.next();
				logs += i.stackSize();
			}
			
			GELogs.setStartLogs(logs);
		}
		
		ctx.bank.depositInventory();
		
		Iterator< Item > items = ctx.bank.select().id(GELogs.tree_item_id).iterator();
		
		int logs = 0;
		
		while (items.hasNext())
		{
			Item i = items.next();
			logs += i.stackSize();
		}
		
		GELogs.setLogs(logs);
		
		ctx.bank.close();
	}
}
