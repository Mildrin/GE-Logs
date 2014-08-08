package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

public class FindTree extends Task
{
	
	public FindTree( ClientContext ctx )
	{
		super(ctx);
	}
	
	@Override
	public boolean activate()
	{
		return ctx.backpack.select().count() < 28 && !ctx.objects.select().id(GELogs.tree_ids).select(GELogs.getReachableFilter()).isEmpty()
				&& ctx.players.local().animation() == -1;
	}
	
	@Override
	public void execute()
	{
		GELogs.setStatus("Looking For Tree");
		
		GameObject tree = ctx.objects.nearest().poll();
		
		if ( !tree.inViewport() )
		{
			ctx.movement.step(tree);
			ctx.camera.turnTo(tree);
		}
		
		if ( tree.inViewport() )
			GELogs.setCurrentTree(tree);
	}
	
}
