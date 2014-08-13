package me.sothatsit.gelogs.states;


public abstract class State
{
	
	private String name;
	private boolean current;
	
	public State( String name )
	{
		this.name = name;
		this.current = false;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isCurrent()
	{
		return current;
	}
	
	public void setCurrent(boolean current)
	{
		this.current = current;
	}
	
	public abstract void setup();
	
	public abstract void poll();
	
	public abstract void disable();
}
