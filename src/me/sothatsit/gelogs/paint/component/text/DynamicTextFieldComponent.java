package me.sothatsit.gelogs.paint.component.text;

import java.awt.Graphics2D;

import me.sothatsit.gelogs.paint.container.IPaintContainer;

public class DynamicTextFieldComponent extends TextFieldComponent
{
	TextFieldReciever reciever;
	
	public DynamicTextFieldComponent( int x , int y , int width , int height , int space_per_line , TextFieldReciever reciever )
	{
		super(x, y, width, height, space_per_line, reciever.getText());
		this.reciever = reciever;
	}
	
	@Override
	public void paint(IPaintContainer paint, Graphics2D g)
	{
		super.setText(reciever.getText());
		
		super.paint(paint , g);
	}
}
