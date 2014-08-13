package me.sothatsit.gelogs.task;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;

public class MoveToBankWaypoint extends Task
{
	
	private GELogs main;
	
	public MoveToBankWaypoint( ClientContext ctx , GELogs main )
	{
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate()
	{
		return main.getBanks().isEmpty() && main.goToWaypoint();
	}
	
	@Override
	public void execute()
	{
		ctx.camera.angle(GELogs.rand(50 , 70));
		
		main.setStatus("Travelling to Bank Waypoint");
		
		ctx.movement.findPath(main.getRandBankWaypointLoc()).traverse();
	}
	
}
