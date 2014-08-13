package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;

public class MoveToTrees extends Task
{
	
	private GELogs main;
	
	public MoveToTrees( ClientContext ctx , GELogs main )
	{
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate()
	{
		return !main.isInvFull() && main.isPlayerIdle() && ctx.objects.select().id(GELogs.tree_ids).within(20).select(main.getReachableFilter()).isEmpty();
	}
	
	@Override
	public void execute()
	{
		main.setStatus("Travelling to Trees");
		
		ctx.movement.findPath(main.getRandTreeLoc()).traverse();
	}
	
}
