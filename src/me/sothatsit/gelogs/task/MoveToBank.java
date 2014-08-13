package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.LocalPath;

public class MoveToBank extends Task
{
	
	private GELogs main;
	
	public MoveToBank( ClientContext ctx , GELogs main )
	{
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate()
	{
		return main.getBanks().isEmpty() && !main.goToWaypoint();
	}
	
	@Override
	public void execute()
	{
		main.setStatus("Travelling to Bank");
		
		ctx.camera.angle(GELogs.rand(50 , 70));
		
		findPath().traverse();
	}
	
	public LocalPath findPath()
	{
		return ctx.movement.findPath(main.getRandBankLoc());
	}
}
