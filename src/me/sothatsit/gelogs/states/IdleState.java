package me.sothatsit.gelogs.states;

import me.sothatsit.gelogs.GELogs;

public class IdleState extends State
{
	
	private GELogs main;
	
	public IdleState( GELogs main )
	{
		super("Idle");
		
		this.main = main;
	}
	
	@Override
	public void setup()
	{
		main.setStatus(" Idle ");
		main.setIdle(true);
	}
	
	@Override
	public void poll()
	{
		
	}
	
	@Override
	public void disable()
	{
		main.setIdle(false);
	}
	
}
