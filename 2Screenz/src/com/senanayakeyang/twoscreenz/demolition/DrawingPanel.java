package com.senanayakeyang.twoscreenz.demolition;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

public class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {
	public static final int FPS = 40;
	static boolean firstDraw = true;
	public static GameManager gm;
	VelocityTracker velocity = VelocityTracker.obtain();
	public static MainActivity mainActivityReference;
	int wallStartX;
	int wallStartY;
	boolean drawingWall = false;
    public DrawingPanel _panel;
   
	
	public DrawingPanel(Context context, AttributeSet  attr) { 
        super(context); 
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }
	class MyTask extends TimerTask
	{

		@Override
		public void run() {
			Canvas c = null;
			SurfaceHolder surfaceHolder = getHolder();
			try {

            	
                c = getHolder().lockCanvas(null);
                synchronized (surfaceHolder) {


                 //Insert methods to modify positions of items in onDraw()
                 postInvalidate();


                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
		}
		
	}
	TimerTask task = new MyTask();
	Timer timer = new Timer();
    @Override 
    public void onDraw(Canvas canvas) { 
    	
       if (firstDraw)
       {
  
    	   gm = new GameManager(canvas, mainActivityReference);
    	   /*
    	   gm.makeBall(160, 50, 200, 300);
    	   gm.makeBall(300, 300, -200, -300);
    	   gm.makeBall(100, 500, 500, 600);
    	   gm.makeBall(100, 500, 500, 600);
    	   gm.makeBall(100, 500, 500, 600);
    	   gm.makeBall(100, 500, 500, 600);
    	    */
    	   firstDraw = false;
       }
       else
       {
    	   if (gm.update(canvas))
    	   {
    		   //gameover
    		   stop();
    		   mainActivityReference.gameOver();
    		   sendGameOver();
    	   }
       }
       
    } 
    public void reset()
    {
    	firstDraw = true;
    	if (gm != null)
    	{
    		gm.reset();
    	}
    }
 
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    
    	firstDraw = true;
     setWillNotDraw(false); //Allows us to use invalidate() to call onDraw()
     
     /*
     timer.schedule(task, delay, period);
     	_thread = new PanelThread(getHolder(), this); //Start the thread that
        _thread.setRunning(true);                     //will make calls to 
        _thread.start();                              //onDraw()
        */
    }


public void stop() {
		task.cancel();
		task = new MyTask();
	}
public void start()
{
	timer = new Timer();
	try
	{
		timer.scheduleAtFixedRate(task, 1000/FPS, 1000/FPS);
	}
	catch (Exception e)
	{
		task = new MyTask();
		timer.scheduleAtFixedRate(task, 1000/FPS, 1000/FPS);
	}
}
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	stop();
    }
/*
    class PanelThread extends Thread {
        public SurfaceHolder _surfaceHolder;
        public DrawingPanel _panel;
        private boolean _run = false;


        public PanelThread(SurfaceHolder surfaceHolder, DrawingPanel panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }


        public void setRunning(boolean run) { //Allow us to stop the thread
            _run = run;
        }


        @Override
        public void run() {
            Canvas c;
            while (_run) {     //When setRunning(false) occurs, _run is 
                c = null;      //set to false and loop ends, stopping thread

                long lastSystemTime = System.currentTimeMillis();
                try {

                	
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {


                     //Insert methods to modify positions of items in onDraw()
                     postInvalidate();


                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
                
                if (GameManager.isPlayerOne)
                {
                	sync();
                }
                
                int timeToSleep = (int)((double) 1000/FPS - (System.currentTimeMillis() - lastSystemTime));
               // Log.d("FPS", "Time to sleep" + timeToSleep);
                while (timeToSleep > 20 && _run)
                {
                	try
                	{
                		Thread.sleep(20);
                		timeToSleep -= 20;
                	}
                	catch (Exception e)
                	
                	{}
                }
                if (_run)
                {
	                try
	                {
	                	Thread.sleep(timeToSleep);
	                }
	                catch (Exception e)
	                {}
                }
            }
        }
    }
*/
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		 int x = convertPixelsToDp(event.getX(), v.getContext());
		 int y = convertPixelsToDp(event.getY(), v.getContext());
		 
		if (!GameManager.isPlayerOne)
		{
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				wallStartX = x;
				wallStartY = y;
				drawingWall = true;
			}
			else if (event.getAction() == MotionEvent.ACTION_MOVE && drawingWall)
			{
				double wallLength = Math.sqrt((x - wallStartX) * (x-wallStartX) + (y-wallStartY) * (y-wallStartY));
				if (wallLength > GameManager.WALL_MAX_WIDTH)
				{
					gm.makeWall(wallStartX, wallStartY, x, y, 0, 0);
					sendCreateWall(wallStartX, wallStartY, x, y);
					drawingWall = false;
				}
			}
			else if (event.getAction() == MotionEvent.ACTION_UP && drawingWall)
			 {
				gm.makeWall(wallStartX, wallStartY, x, y, 0, 0);
				sendCreateWall(wallStartX, wallStartY, x, y);
				drawingWall = false;
			 }
		}
		else
		{
			 if(event.getAction() == MotionEvent.ACTION_MOVE)
			    {
			        velocity.addMovement(event);
			    }
			 else if (event.getAction() == MotionEvent.ACTION_UP)
			 {
				 velocity.computeCurrentVelocity(1000);
				
				 int vx = convertPixelsToDp(velocity.getXVelocity(), v.getContext());
				 int vy = convertPixelsToDp(velocity.getYVelocity(), v.getContext());
				 int speed = (int) Math.sqrt(vx*vx+vy*vy);
				 if (speed>700)
				 {
					 vx*=(double)700/speed;
					 vy*=(double)700/speed;
				 }
				 gm.makeBall(x, y, vx, vy);
				 sendCreateBall(x, y, vx, vy);
			 }
		}
		return true;
	}
	 public static int convertPixelsToDp(float px, Context context){
	        Resources resources = context.getResources();
	        DisplayMetrics metrics = resources.getDisplayMetrics();
	        float dp = px / (metrics.densityDpi / 160f);
	        return (int) dp;
	    }
	 void sendCreateBall(int x, int y, int vx, int vy) {
	       
	        // First byte in message indicates whether it's a final score or not
	        ByteBuffer buffer = ByteBuffer.allocate(100);
	        buffer.putChar('B');
	        buffer.putInt(x);
	        buffer.putInt(y);
	        buffer.putInt(vx);
	        buffer.putInt(vy);

	        // Send to every other participant.
	        for (Participant p : mainActivityReference.mParticipants) {
	            if (p.getParticipantId().equals(mainActivityReference.mMyId))
	                continue;
	            if (p.getStatus() != Participant.STATUS_JOINED)
	                continue;
	                Games.RealTimeMultiplayer.sendReliableMessage(mainActivityReference.getApiClientPublic(), null, buffer.array(),
	                        mainActivityReference.mRoomId, p.getParticipantId());
	        }
	    }
	 void sendGameOver() {
	       
	        // First byte in message indicates whether it's a final score or not
	        ByteBuffer buffer = ByteBuffer.allocate(100);
	        buffer.putChar('G');
	       

	        // Send to every other participant.
	        for (Participant p : mainActivityReference.mParticipants) {
	            if (p.getParticipantId().equals(mainActivityReference.mMyId))
	                continue;
	            if (p.getStatus() != Participant.STATUS_JOINED)
	                continue;
	                Games.RealTimeMultiplayer.sendReliableMessage(mainActivityReference.getApiClientPublic(), null, buffer.array(),
	                        mainActivityReference.mRoomId, p.getParticipantId());
	        }
	    }
	 void sendCreateWall(int x, int y, int x2, int y2) {
	       
	        // First byte in message indicates whether it's a final score or not
	        ByteBuffer buffer = ByteBuffer.allocate(100);
	        buffer.putChar('W');
	        buffer.putInt(x);
	        buffer.putInt(y);
	        buffer.putInt(x2);
	        buffer.putInt(y2);

	        // Send to every other participant.
	        for (Participant p : mainActivityReference.mParticipants) {
	            if (p.getParticipantId().equals(mainActivityReference.mMyId))
	                continue;
	            if (p.getStatus() != Participant.STATUS_JOINED)
	                continue;
	                Games.RealTimeMultiplayer.sendReliableMessage(mainActivityReference.getApiClientPublic(), null, buffer.array(),
	                        mainActivityReference.mRoomId, p.getParticipantId());
	        }
	    }
	 /*
	 void sync() {
	       
	        // First byte in message indicates whether it's a final score or not
	        ByteBuffer buffer = ByteBuffer.allocate(100000);
	        buffer.putChar('S');
	        buffer.putInt(gm.balls.size());
	        for (Ball b : gm.balls)
	        {
	        	buffer.putDouble(b.x);
	        	buffer.putDouble(b.y);
	        	buffer.putDouble(b.vx);
	        	buffer.putDouble(b.vy);
	        }
	        for (Wall b : gm.walls)
	        {
	        	buffer.putDouble(b.x1);
	        	buffer.putDouble(b.y1);
	        	buffer.putDouble(b.x2);
	        	buffer.putDouble(b.y2);
	        	buffer.putDouble(b.vx);
	        	buffer.putDouble(b.vy);
	        }

	        // Send to every other participant.
	        for (Participant p : mainActivityReference.mParticipants) {
	            if (p.getParticipantId().equals(mainActivityReference.mMyId))
	                continue;
	            if (p.getStatus() != Participant.STATUS_JOINED)
	                continue;
	                Games.RealTimeMultiplayer.sendUnreliableMessage(mainActivityReference.getApiClientPublic(), buffer.array(),
	                        mainActivityReference.mRoomId, p.getParticipantId());
	        }
	    }
	    */
}
