package me.sothatsit.gelogs.states;

import java.util.ArrayList;
import java.util.List;

import me.sothatsit.gelogs.GELogs;
import me.sothatsit.gelogs.task.Task;

public class TaskState extends State
{
	
	private List< Task > tasks;
	private GELogs main;
	
	public TaskState( String name , GELogs main )
	{
		super(name);
		this.tasks = new ArrayList< Task >();
		this.main = main;
	}
	
	public List< Task > getTasks()
	{
		return new ArrayList< Task >(tasks);
	}
	
	public void addTask(Task task)
	{
		tasks.add(task);
	}
	
	public void addTasks(List< Task > task_list)
	{
		tasks.addAll(task_list);
	}
	
	public void removeTask(Task task)
	{
		tasks.remove(task);
	}
	
	public void setup()
	{
		main.setStatus(getName());
	}
	
	public void poll()
	{
		for ( Task task : tasks )
		{
			if ( !this.isCurrent() )
				return;
			
			if ( task.activate() )
			{
				task.execute();
				GELogs.sleep(300 , 600);
			}
		}
	}
	
	public void disable()
	{
	}
}
