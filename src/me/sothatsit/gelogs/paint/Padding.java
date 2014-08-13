package me.sothatsit.gelogs.paint;

public class Padding
{
	public int left;
	public int right;
	public int top;
	public int bottom;
	
	public Padding( int left , int right , int top , int bottom )
	{
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	public Padding clone()
	{
		return new Padding(left, right, top, bottom);
	}
}
