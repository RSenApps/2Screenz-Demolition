package com.senanayakeyang.twoscreenz.demolition;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;



public class Nexus 
{
	Canvas panel;
	Paint paint;
	double health;
	double maxHealth;
	double x;
	double y;
	double r;
	int one;
	int half;
	boolean end=false;
	public Nexus(Canvas canvas, int h, double xcoord, double ycoord, double radius, int playerOne, int ha)
	{
		panel=canvas;
		paint=new Paint();
		health=h;
		maxHealth=h;
		x=xcoord;
		y=ycoord;
		r=radius;
		one=playerOne;
		half=ha;
		paint.setColor(Color.BLUE);
		draw(panel);
	}
	public void damage(double hurt)
	{
		health-=hurt;
		paint.setColor(Color.rgb((int)(255*(1-health)/maxHealth), (int)(120*(.5-Math.abs(health/maxHealth-.5))), (int)(255*health/maxHealth)));
		draw(panel);
		if (health<=0)
		{
			end=true;
		}
	}
	public void draw(Canvas canvas)
	{
		panel=canvas;
		int extra = 0;
		if (one>0&&GameManager.height2>GameManager.height1)
		{
			extra = (GameManager.height2-GameManager.height1)/2;
	
		}
		else if (one==0&&GameManager.height1<GameManager.height2)
		{
			extra = (GameManager.height1-GameManager.height2)/2;
		}
		panel.drawCircle(GameManager.convertDpToPixel((x-one*half)), GameManager.convertDpToPixel(y+extra), GameManager.convertDpToPixel(r), paint);
	}
}
