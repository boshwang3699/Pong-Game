import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import java.util.*;

public class GamePanel extends JPanel implements Runnable{
    
	//reason for static is :if we have more than one panel exists, we can share the same game width
	//and the reason for final is that prevent us from accidentally modify the number
	static final int GAME_WIDTH = 1000;
	static final int GAME_HEIGHT = (int)(GAME_WIDTH * 0.5555);
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
	static final int BALL_DIAMETER = 20;
	static final int PADDLE_WIDTH = 25;
	static final int PADDLE_HEIGHT = 100;
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	Paddle paddle1;
	Paddle paddle2;
	Ball ball;
	Score score;
	
	
    GamePanel(){
    	newPaddles();
    	newBall();
    	score = new Score(GAME_WIDTH, GAME_HEIGHT);
    	this.setFocusable(true);
    	this.addKeyListener(new AL());
    	this.setPreferredSize(SCREEN_SIZE);
    	
    	gameThread = new Thread(this);
    	gameThread.start();
    	
    	
    }
    public void newBall(){
    	random = new Random();
    	ball =  new Ball((GAME_WIDTH/2) - (BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
    }
    public void newPaddles(){
    	paddle1 = new Paddle(0, (GAME_HEIGHT/2) - (PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
    	paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT/2) - (PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }
    public void paint(Graphics g){
    	image = createImage(getWidth(), getHeight());
    	graphics = image.getGraphics();
    	draw(graphics);
    	g.drawImage(image, 0, 0, this);
    }
    public void draw(Graphics g){
    	paddle1.draw(g);
    	paddle2.draw(g);
    	ball.draw(g);
    	score.draw(g);
    }
    public void move(){
    	paddle1.move();
    	paddle2.move();
    	ball.move();
    	AIEnabled(true);
    }
    public void checkCollision(){
    	//stops paddles at window edges
    	if(paddle1.y <= 0) {
    		paddle1.y = 0;
    	}
    	if(paddle1.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) {
    		paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
    	}
    	if(paddle2.y <= 0) {
    		paddle2.y = 0;
    	}
    	if(paddle2.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) {
    		paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;
    	}
    	//bounce ball off the top and down edges
    	if(ball.y <= 0) {
    		ball.setYDirection(-ball.yVelocity);
    	}if(ball.y >= (GAME_HEIGHT - BALL_DIAMETER)) {
    		ball.setYDirection(-ball.yVelocity);
    	}
    	
    	//bounce ball off the paddles
    	//using the ball's super class 'Rectangle' method 'intersects'
    	if(ball.intersects(paddle1)) {
    		ball.xVelocity = Math.abs(ball.xVelocity);
    		ball.xVelocity++; //increase the difficulty
    		if(ball.yVelocity>0) {
    			ball.yVelocity++; //increase the difficulty
    		}
    		else {
    			ball.yVelocity--; //increase the difficulty
    		}
    		ball.setXDirection(ball.xVelocity);
    		ball.setYDirection(ball.yVelocity);
    	}
    	if(ball.intersects(paddle2)) {
    		ball.xVelocity = Math.abs(ball.xVelocity);
    		ball.xVelocity++; //increase the difficulty
    		if(ball.yVelocity>0) {
    			ball.yVelocity++; //increase the difficulty
    		}
    		else {
    			ball.yVelocity--; //increase the difficulty
    		}
    		ball.setXDirection(-ball.xVelocity);
    		ball.setYDirection(ball.yVelocity);
    	}
    	/*-------my method---------
    	 if((ball.x < (paddle1.x + PADDLE_WIDTH/2 + BALL_DIAMETER/2)) && ((paddle1.y + PADDLE_HEIGHT/2) > ball.y) && ((paddle1.y - PADDLE_HEIGHT/2) < ball.y)) {
    		ball.setXDirection(-ball.xVelocity);
    	}if((ball.x > (paddle2.x - PADDLE_WIDTH/2 - BALL_DIAMETER/2)) && ((paddle2.y + PADDLE_HEIGHT/2) > ball.y) && ((paddle1.y - PADDLE_HEIGHT/2) < ball.y)) {
    		ball.setXDirection(-ball.xVelocity);
    	}----------end-----------*/
    	
    	//give player a point and creates a new ball and paddles
    	if(ball.x <= 0) {
    		score.player2++;   //player2 scores
    		newBall();
    		newPaddles();
    	}
    	if(ball.x >= (GAME_WIDTH-BALL_DIAMETER)) {
    		score.player1++;   //player1 scores
    		newBall();
    		newPaddles();
    	}
    	
    }
    public void run(){
    	//create a game loop
    	long lastTime = System.nanoTime();
    	double amountOfTicks = 60.0;
    	double ns = 1000000000 / amountOfTicks;
    	double delta = 0;
    	while(true) {
    		long now = System.nanoTime();
    		delta += (now - lastTime) / ns;
    		lastTime = now;
    		if(delta >= 1) {
    			move();
    			checkCollision();
    			repaint();
    			delta --;
    		}
    	}
    	
    }
    public void AIEnabled(boolean b) {
    	if(b) {
    		int temp1 = ball.y;
    		paddle2.y = temp1 - PADDLE_HEIGHT/2 - BALL_DIAMETER/2;
    		int temp2 = ball.y;
    		paddle1.y = temp2 - PADDLE_HEIGHT/2 - BALL_DIAMETER/2;

    	}
    }
    
    //inner class
    public class AL extends KeyAdapter{
        public void keyPressed(KeyEvent e){
        	paddle1.keyPressed(e);
        	paddle2.keyPressed(e);
        }
        public void keyReleased(KeyEvent e){
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}
