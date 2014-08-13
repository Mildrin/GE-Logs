package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;

public class CheckToBank extends Task
{
	
	private GELogs main;
	
	public CheckToBank( ClientContext ctx , GELogs main )
	{
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate()
	{
		return main.isInvFull();
	}
	
	@Override
	public void execute()
	{
		main.setStatus("Checking Whether to Bank");
		
		main.getStateMachine().setCurrentState("Bank");
	}
	
}
