package me.sothatsit.gelogs.states;

import java.util.ArrayList;
import java.util.List;

public class StateMachine
{
	private List< State > states;
	private State current;
	
	public StateMachine()
	{
		states = new ArrayList< State >();
		current = null;
	}
	
	public State getCurrentState()
	{
		return current;
	}
	
	public List< State > getStates()
	{
		return new ArrayList< State >(states);
	}
	
	public State getState(String name)
	{
		for ( State state : getStates() )
		{
			if ( state.getName().equalsIgnoreCase(name) )
				return state;
		}
		return null;
	}
	
	public boolean addState(State state)
	{
		if ( getState(state.getName()) != null )
			return false;
		
		states.add(state);
		
		if ( states.size() <= 1 )
			setCurrentState(state.getName());
		
		return true;
	}
	
	public boolean removeState(String name)
	{
		int index = 0;
		while (index < states.size())
		{
			State state = states.get(index);
			
			if ( state.getName().equalsIgnoreCase(name) )
			{
				states.remove(index);
				return true;
			}
			
			index++;
		}
		
		return false;
	}
	
	public boolean setCurrentState(String name)
	{
		State state = getState(name);
		
		if ( state == null )
			return false;
		
		if ( current != null )
		{
			current.disable();
			current.setCurrent(false);
		}
		
		current = state;
		
		current.setCurrent(true);
		current.setup();
		
		return true;
	}
	
	public void poll()
	{
		if ( current != null )
			current.poll();
	}
}
