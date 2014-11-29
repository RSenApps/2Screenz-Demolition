package com.senanayakeyang.twoscreenz.demolition;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;


public class GameManager 
{
	//time limit = 20 seconds or so? send updated gm, nexus
	public static final int WALL_MAX_WIDTH = 200;
	public static int width1;
	public static int height1;
	public static int width2;
	public static int height2;
	public static boolean isPlayerOne;
	public static Resources resources;
	public double maxGameTime = 20;
	double gameTime = maxGameTime;
	double lastBall=maxGameTime;
	double lastWall=maxGameTime;
	Canvas panel;
	Paint paint;
	Paint textPaint;
	List<Ball> balls=new ArrayList<Ball>();
	List<Wall> walls=new ArrayList<Wall>();
	Nexus nexus;
	int health=30;
	double wallcost=.1;
	int balldamage=1;
	int walldamage=1;
	int width;
	int height;
	int half;
	double nexusRatio=0.1;
	double nexusAcross=0.9;
	double ballRatio=0.05;
	double wallRatio=0.04;
	long time;
	double ballLimit=5;
	double wallLimit=3;
	boolean isWinner = false;
	int one;
	int background=Color.WHITE;
	
	public GameManager(Canvas canvas, Context context)
	{
		panel=canvas;
		resources = context.getResources();
		paint=new Paint();
		textPaint = new Paint();
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize((float)convertDpToPixel(150));
		panel.drawColor(background);
		width=width1+width2;
		height=Math.min(height1, height2);
		half=width1;
		if (isPlayerOne)
		{
			one=0;
		}
		else
		{
			one=1;
		}
		double x = nexusAcross*width;
		double y = height/2;
		double r = nexusRatio*Math.min(width, height);
		nexus=new Nexus(panel, health, x, y, r, one, half);
		time=System.currentTimeMillis();
	}
	public static float convertDpToPixel(double dp){
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = (float)dp * (metrics.densityDpi / 160f);
        return px;
    }
	public void reset()
	{
		panel.drawColor(background);
		isWinner=false;
		gameTime=maxGameTime;
		lastBall=maxGameTime;
		lastWall=maxGameTime;
		balls=new ArrayList<Ball>();
		double x = nexusAcross*width;
		double y = height/2;
		double r = nexusRatio*Math.min(width, height);
		nexus=new Nexus(panel, health, x, y, r, one, half);
	}
	public void makeBall(int x, int y, int vx, int vy)
	{
		//change vx, vy maybe
		
		if (gameTime+0.25<=lastBall)
		{
			double r = ballRatio*Math.min(width, height);
			lastBall=gameTime;
			Ball b = new Ball(panel, nexus, balldamage, x, y, r, vx, vy, Color.RED, ballLimit, one, width, height, half);
			balls.add(b);
		}
	}
	public void makeWall(int x1, int y1, int x2, int y2, double vx, double vy)
	{
		if (gameTime+0.5<=lastWall)
		{
			x1+=half;
			x2+=half;
			double dist = Math.sqrt(((x1+x2)/2-nexusAcross*width)*((x1+x2)/2-nexusAcross*width)+((y1+y2)/2-height/2)*((y1+y2)/2-height/2));
			double nexusR = 2*nexusRatio*Math.min(width, height);
			if (dist!=0&&dist<nexusR)
			{
				int xshift = (int)(((x1+x2)/2-nexusAcross*width)*nexusR/dist-((x1+x2)/2-nexusAcross*width));
				int yshift = (int)(((y1+y2)/2-height/2)*nexusR/dist-((y1+y2)/2-height/2));
				x1+=xshift;
				x2+=xshift;
				y1+=yshift;
				y2+=yshift;
			}
			if (dist!=0)
			{
				lastWall = gameTime;
				double r = wallRatio*Math.min(width, height);
				Wall w = new Wall(panel, walldamage, x1, y1, x2, y2, r, Color.GREEN, wallLimit, one, width, height, half, vx, vy);
				Wall w2 = new Wall(panel, walldamage, x1+1, y1, x2+1, y2, r, Color.GREEN, wallLimit, one, width, height, half, vx, vy);
				Wall w3 = new Wall(panel, walldamage, x1, y1+1, x2, y2+1, r, Color.GREEN, wallLimit, one, width, height, half, vx, vy);
				walls.add(w);
				walls.add(w2);
				walls.add(w3);
				nexus.damage(wallcost);
			}
		}
	}
	public boolean update(Canvas canvas)
	{
		panel=canvas;
		panel.drawColor(background);
		nexus.draw(panel);
		if (one==0)
		{
			int extra = 0; 
			if (height1>height2)
			{
				extra = (height1-height2)/2;
			}
			textPaint.setColor(Color.rgb(255, (int)(255*gameTime/maxGameTime), (int)(255*gameTime/maxGameTime)));
			panel.drawText(""+(int)(gameTime+1), convertDpToPixel(half/2), convertDpToPixel(height/2+extra), textPaint);
		}
		long newTime=System.currentTimeMillis();
		double elapse=((double)(newTime-time))/1000.0;
		time=newTime;
		for (int i = walls.size()-1; i >=0; i--)
		{
			if (walls.get(i).remove)
			{
				walls.remove(i);
			}
			else
			{
				walls.get(i).move(1.0/DrawingPanel.FPS, panel);
			}
		}
		for (int i = balls.size()-1; i >=0; i--)
		{
			if (balls.get(i).remove)
			{
				balls.remove(i);
			}
			else
			{
				balls.get(i).move(1.0/DrawingPanel.FPS, panel, walls, 2);
			}
		}
		gameTime-=1.0/DrawingPanel.FPS;
		if (gameTime<=0)
		{
			if (one>0)
			{
				isWinner=true;
			}
		}
		if (nexus.end)
		{
			if (one==0)
			{
				isWinner=true;
			}
		}
		return nexus.end || (gameTime<=0);
	}
}
