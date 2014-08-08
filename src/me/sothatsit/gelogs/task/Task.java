package me.sothatsit.gelogs.task;

import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

public abstract class Task extends ClientAccessor
{
	public Task( ClientContext ctx )
	{
		super(ctx);
	}
	
	public abstract boolean activate();
	
	public abstract void execute();
}