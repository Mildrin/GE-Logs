package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;

public class MoveToBank extends Task
{
	
	public MoveToBank( ClientContext ctx )
	{
		super(ctx);
	}
	
	@Override
	public boolean activate()
	{
		return ctx.backpack.select().count() >= 28 && ctx.objects.select().id(GELogs.banker_ids).within(10).isEmpty();
	}
	
	@Override
	public void execute()
	{
		GELogs.setStatus("Travelling to Bank");
		
		Tile dest = GELogs.getRandTreeLoc();
		
		int timeout = 10;
		while (ctx.players.local().tile().x() < dest.x() && timeout > 0)
		{
			ctx.movement.findPath(dest).traverse();
			timeout--;
		}
		
		if ( ctx.players.local().tile().x() < dest.x() )
		{
			GELogs.setStatus("Unable to Find Path to Bank");
			GELogs.sleep(400 , 600);
			return;
		}
		
		ctx.movement.findPath(GELogs.getRandBankLoc()).traverse();
	}
	
}
