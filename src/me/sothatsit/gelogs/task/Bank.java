package me.sothatsit.gelogs.task;

import java.util.Iterator;

import me.sothatsit.gelogs.GELogs;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Item;

public class Bank extends Task
{
	
	private GELogs main;
	
	public Bank( ClientContext ctx , GELogs main )
	{
		super(ctx);
		this.main = main;
	}
	
	@Override
	public boolean activate()
	{
		return !main.getBanks().isEmpty();
	}
	
	@Override
	public void execute()
	{
		main.setStatus("Banking Inventory");
		
		GameObject bank = main.getBank();
		
		ctx.camera.turnTo(bank);
		
		bank.interact(GELogs.banker_function);
		
		ctx.bank.open();
		
		int timeout = 10;
		
		while (!ctx.bank.opened() && timeout > 0)
		{
			GELogs.sleep(500 , 750);
			timeout--;
		}
		
		if ( timeout <= 0 )
			return;
		
		ctx.bank.select().id(GELogs.log_id);
		
		if ( main.getStartLogs() < 0 )
		{
			Iterator< Item > items = ctx.bank.iterator();
			
			int logs = 0;
			
			while (items.hasNext())
			{
				Item i = items.next();
				logs += i.stackSize();
			}
			
			main.setStartLogs(logs);
		}
		
		ctx.bank.depositInventory();
		
		Iterator< Item > items = ctx.bank.iterator();
		
		int logs = 0;
		
		while (items.hasNext())
		{
			Item i = items.next();
			logs += i.stackSize();
		}
		
		main.setLogs(logs);
		
		ctx.bank.close();
		
		main.getStateMachine().setCurrentState("Chop");
	}
}
