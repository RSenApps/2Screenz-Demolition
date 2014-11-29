package com.senanayakeyang.twoscreenz.demolition;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class Ball {
	Canvas panel;
	Nexus nexus;
	Paint paint;
	int background = Color.WHITE;
	int damage;
	int color;
	double x;
	double y;
	double r;
	double vx;
	double vy;
	int width;
	int height;
	int half;
	double mass = .1;
	double maxHealth;
	double health;
	boolean remove=false;
	int one;
	public Ball(Canvas canvas, Nexus n, int hurt, double xcoord, double ycoord, double radius, double xvelocity, double yvelocity, int coloring, double timeLimit, int playerOne, int wid, int hei, int ha)
	{
		panel=canvas;
		paint=new Paint();
		nexus=n;
		damage=hurt;
		half=ha;
		r=radius;
		vx=xvelocity;
		vy=yvelocity;
		color=coloring;
		health=timeLimit;
		maxHealth=timeLimit;
		one=playerOne;
		width=wid;
		height=hei;
		x=xcoord;
		y=ycoord;
		draw(panel);
	}
	public boolean aboveLine(double xc, double yc, double x1, double y1, double x2, double y2)
	{
		double m1 = (yc-y1)/(xc-x1);
		double m2 = (yc-y2)/(xc-x2);
		if (x1<x2)
		{
			return (m1>m2);
		}
		else
		{
			return (m1<m2);
		}
	}
	public boolean move(double time, Canvas canvas, List<Wall> walls, int recurse)
	{
		panel=canvas;
		x+=vx*time;
		y+=vy*time;
		if (y-r<0)
		{
			vy*=-1;
			y=2*r-y;
		}
		if (y+r>height)
		{
			vy*=-1;
			y=2*height-2*r-y;
		}
		if (x-r<0)
		{
			vx*=-1;
			x=2*r-x;
		}
		if (x+r>width)
		{
			vx*=-1;
			x=2*width-2*r-x;
		}
		health -=time;

		//collide with walls
		boolean collide = false;
		for (int i = 0; i < walls.size(); i++)
		{
			Wall w = walls.get(i);
			double length = Math.sqrt((w.x1-w.x2)*(w.x1-w.x2)+(w.y1-w.y2)*(w.y1-w.y2));
			double xshift = (w.r/length)*Math.abs(w.y2-w.y1);
			double yshift = (w.r/length)*Math.abs(w.x2-w.x1);
			double m = 0;
			if (w.x2!=w.x1)
			{
				m=(w.y2-w.y1)/(w.x2-w.x1);
			}
			else
			{
				m=Double.MAX_VALUE;
			}
			if (w.x1>w.x2)
			{
				double xtemp = w.x2;
				double ytemp = w.y2;
				w.x2=w.x1;
				w.y2=w.y1;
				w.x1=xtemp;
				w.y1=ytemp;
			}
			double yprime = 0;
			if (w.x2!=w.x1)
			{
				yprime=((x-w.x1)*w.y2+(x-w.x2)*w.y1)/(w.x2-w.x1);
			}
			else
			{
				yprime=(w.y2+w.y1)/2;
			}
			double ydist = Math.abs(y-yprime);
			double theta = Math.atan(m);
			boolean radius = (w.r+r>=Math.abs(ydist*Math.cos(theta)));
			boolean inRange = (((w.r+r)*(w.r+r)+(w.y2-w.y1)*(w.y2-w.y1)/4+(w.x2-w.x1)*(w.x2-w.x1)/4)>=(x-(w.x2+w.x1)/2)*(x-(w.x2+w.x1)/2)+(y-(w.y2+w.y1)/2)*(y-(w.y2+w.y1)/2));
			
//			if ((m>0 && 
//					aboveLine(x, y, w.x1+xshift, w.y1-yshift, w.x2+xshift, w.y2-yshift) && 
//					aboveLine(x, y, w.x1+xshift, w.y1-yshift, w.x1-xshift, w.y1+yshift) && 
//					!aboveLine(x, y, w.x1-xshift, w.y1+yshift, w.x2-xshift, w.y2+yshift) && 
//					!aboveLine(x, y, w.x2+xshift, w.y2-yshift, w.x2-xshift, w.y2+yshift))||
//					((m<0)&&
//					aboveLine(x, y, w.x1-xshift, w.y1-yshift, w.x2-xshift, w.y2-yshift) && 
//					aboveLine(x, y, w.x1-xshift, w.y1-yshift, w.x1+xshift, w.y1+yshift) && 
//					!aboveLine(x, y, w.x1+xshift, w.y1+yshift, w.x2+xshift, w.y2+yshift) && 
//					!aboveLine(x, y, w.x2-xshift, w.y2-yshift, w.x2+xshift, w.y2+yshift)))
//			{
			if (radius && inRange)
			{	
				collide=true;
				w.vx+=vx*mass;
				w.vy+=vy*mass;
				double velocity = Math.sqrt(vx*vx+vy*vy);
				double walltheta = 0;
				if (m!=0)
				{
					walltheta=Math.atan(-1/m)*180/Math.PI;
				}
				else
				{
					walltheta=90;
				}
				double vtheta=0;
				if (vx!=0)
				{
					vtheta = Math.atan(vy/vx)*180/Math.PI;
				}
				else
				{
					vtheta=90;
				}
				if (vy>0)
				{
					vtheta+=180;
				}
				double newtheta = (2*walltheta-vtheta);
				
				if (walltheta+90==Math.max(walltheta+90, Math.max(vtheta, newtheta))||walltheta+90==Math.min(walltheta+90,  Math.min(vtheta, newtheta)))
				{
					vx = -velocity*Math.cos(newtheta*Math.PI/180);
					vy = -velocity*Math.sin(newtheta*Math.PI/180);
				}
				else
				{
					vx = velocity*Math.cos(newtheta*Math.PI/180);
					vy = velocity*Math.sin(newtheta*Math.PI/180);
				}
				w.x1+=w.vx*time;
				w.x2+=w.vx*time;
				w.y1+=w.vy*time;
				w.y2+=w.vy*time;
				w.health-=damage;
				if (i%3==0)
				{
					walls.get(i+1).health-=damage;
					walls.get(i+2).health-=damage;
				}
				if (i%3==1)
				{
					walls.get(i+1).health-=damage;
					walls.get(i-1).health-=damage;
				}
				if (i%3==2)
				{
					walls.get(i-1).health-=damage;
					walls.get(i-2).health-=damage;
				}
				health-=w.damage;
			}
			if (collide)
			{
				if (recurse>0)
				{
					move(time, panel, walls, recurse-1);
				}
			}
		}
		if (health<0)
		{
			remove=true;
		}
		if ((nexus.r+r)>=Math.sqrt((x-nexus.x)*(x-nexus.x)+(y-nexus.y)*(y-nexus.y)))
		{
			nexus.damage(damage);
			remove=true;
		}
		draw(panel);
		return collide;
	}
	public void draw(Canvas canvas)
	{
		panel=canvas;
		paint.setColor(Color.rgb((int)(Color.red(color)+(240-Color.red(color))*(maxHealth-health)/maxHealth), (int)(Color.green(color)+(240-Color.green(color))*(maxHealth-health)/maxHealth), (int)(Color.blue(color)+(240-Color.blue(color))*(maxHealth-health)/maxHealth)));
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
