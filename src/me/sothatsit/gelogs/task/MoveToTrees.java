package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;

public class MoveToTrees extends Task
{
	
	public MoveToTrees( ClientContext ctx )
	{
		super(ctx);
	}
	
	@Override
	public boolean activate()
	{
		return ctx.backpack.select().count() < 28 && ctx.players.local().animation() == -1
				&& ctx.objects.select().id(GELogs.tree_ids).within(20).select(GELogs.getReachableFilter()).isEmpty();
	}
	
	@Override
	public void execute()
	{
		GELogs.setStatus("Travelling to Trees");
		
		ctx.movement.findPath(GELogs.getRandTreeLoc()).traverse();
	}
	
}
