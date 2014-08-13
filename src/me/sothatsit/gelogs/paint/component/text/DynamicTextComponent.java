package me.sothatsit.gelogs.paint.component.text;

import java.awt.Graphics2D;

import me.sothatsit.gelogs.paint.container.IPaintContainer;

public class DynamicTextComponent extends TextComponent
{
	TextReciever reciever;
	
	public DynamicTextComponent( int x , int y , int width , int height , TextReciever reciever )
	{
		super(x, y, width, height, reciever.getText());
		this.reciever = reciever;
	}
	
	@Override
	public void paint(IPaintContainer paint, Graphics2D g)
	{
		super.setText(reciever.getText());
		
		super.paint(paint , g);
	}
}
