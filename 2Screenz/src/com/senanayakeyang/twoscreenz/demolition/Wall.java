package com.senanayakeyang.twoscreenz.demolition;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Wall {
	Canvas panel;
	Paint paint;
	int background = Color.WHITE;
	double health;
	double maxHealth;
	double damage;
	int color;
	double x1;
	double y1;
	double x2;
	double y2;
	double r;
	double vx;
	double vy;
	int width;
	int height;
	double decay=.8;
	int one;
	int half;
	//rotation?
	boolean remove=false;
	public Wall(Canvas canvas, double hurt, double xcoord1, double ycoord1, double xcoord2, double ycoord2, double radius, int coloring, double timeLimit, int playerOne, int wid, int hei, int ha, double xv, double yv)
	{
		panel=canvas;
		damage=hurt;
		paint=new Paint();
		x1=xcoord1;
		y1=ycoord1;
		x2=xcoord2;
		y2=ycoord2;
		r=radius;
		color=coloring;
		health=timeLimit;
		maxHealth=timeLimit;
		width=wid;
		height=hei;
		one=playerOne;
		half=ha;
		vx=xv;
		vy=yv;
		draw(panel);
	}
	public void move(double time, Canvas canvas)
	{
		/*
		panel=canvas;
		//maybe do rotation. if collide reverse rotation
		x1+=vx*time;
		y1+=vy*time;
		x2+=vx*time;
		y2+=vy*time;
		double length = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
		double rx= r*Math.abs(y2-y1)/length;
		double ry = r*Math.abs(x2-x1)/length;
		if (Math.min(y1, y2)-ry<0)
		{
			vy*=-1;
			y1+=2*(ry-Math.min(y1, y2));
			y2+=2*(ry-Math.min(y1, y2));
		}
		if (Math.max(y1, y2)+ry>height)
		{
			vy*=-1;
			y1-=2*(Math.max(y1, y2)+ry-height);
			y2-=2*(Math.max(y1, y2)+ry-height);
		}
		if (Math.min(x1, x2)-rx<0)
		{
			vx*=-1;
			x1+=2*(rx-Math.min(x1, x2));
			x2+=2*(rx-Math.min(x1, x2));
		}
		if (Math.max(x1, x2)+rx>height)
		{
			vx*=-1;
			x1-=2*(Math.max(x1, x2)+rx-height);
			x2-=2*(Math.max(x1, x2)+rx-height);
		}
		health -=time;
		if (health<0)
		{
			remove=true;
		}
		vx*=decay;
		vy*=decay;
		*/
		draw(panel);
		health -=time;
		if (health<0)
		{
			remove=true;
		}
	}
	public void draw(Canvas canvas)
	{
		panel=canvas;
		paint.setColor(Color.rgb((int)(Color.red(color)+(240-Color.red(color))*(maxHealth-health)/maxHealth), (int)(Color.green(color)+(240-Color.green(color))*(maxHealth-health)/maxHealth), (int)(Color.blue(color)+(240-Color.blue(color))*(maxHealth-health)/maxHealth)));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth((float)r);
		int extra = 0;
		if (one>0&&GameManager.height2>GameManager.height1)
		{
			extra = (GameManager.height2-GameManager.height1)/2;
	
		}
		else if (one==0&&GameManager.height1<GameManager.height2)
		{
			extra = (GameManager.height1-GameManager.height2)/2;
		}
		panel.drawLine((float)GameManager.convertDpToPixel(x1-one*half), (float)GameManager.convertDpToPixel(y1+extra), (float)GameManager.convertDpToPixel(x2-one*half), (float)GameManager.convertDpToPixel(y2+extra), paint);
	}
}
