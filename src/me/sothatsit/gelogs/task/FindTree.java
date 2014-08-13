package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.MobileIdNameQuery;

public class FindTree extends Task
{
	
	private GELogs main;
	
	public FindTree( ClientContext ctx , GELogs main )
	{
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate()
	{
		return !main.isInvFull() && !select().isEmpty() && main.isPlayerIdle() && ( main.getCurrentTree() == null || !main.getCurrentTree().inViewport() );
	}
	
	@Override
	public void execute()
	{
		main.setStatus("Looking For Tree");
		
		GameObject tree = getTree();
		
		if ( !tree.inViewport() )
		{
			ctx.movement.step(tree);
			ctx.camera.turnTo(tree);
		}
		
		if ( tree.inViewport() )
			main.setCurrentTree(tree);
	}
	
	public GameObject getTree()
	{
		return ctx.objects.nearest().limit(3).shuffle().poll();
	}
	
	public MobileIdNameQuery< GameObject > select()
	{
		return ctx.objects.select().id(GELogs.tree_ids).select(main.getReachableFilter());
	}
	
}
