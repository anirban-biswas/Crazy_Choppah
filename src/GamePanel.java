//Author: Anirban Biswas and Lee Vardaro.
//Purpose: To make a game similar to flappyBird and add upgrades to it. 

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.*;

public class GamePanel extends JPanel {
	private final int WIDTH = 800, HEIGHT = 600; //Sets The panel Size
	private final int JUMP = 10; // increment for image movement
	private final int DELAY = 20, IMAGE_SIZE = 50;

	File Missile = new File("MISSILE.WAV"); //Sound File for missile
	File Blast = new File("EXP2.WAV"); //Sound File for blast
	File BackGround = new File("bensound-funkyelement.wav"); //Sound File for Background
	private Clip BackClip;
	
	private double currentTime = 0;
	private static DecimalFormat df2 = new DecimalFormat(".#");
	Font myFont = new Font("Courier New", 1, 20);
	private int currentTicks = 0; // Used for calculating when to send off balloons

	private Timer timer;
	private String timerLabel, scoreLabel; //For timer label and score label
	
	private boolean start = false, gameover = false, hit = false, fire = false, intercept = false;
	private int[] x = new int[100];
	private int[] y = new int[100];
	private boolean[] activeBalloons = new boolean[100];// Creates an array of different balloon coordinates.

	private int[] a = new int[100];
	private int[] b = new int[100];
	private boolean[] activeBullets = new boolean[100];// Creates an array of different bullet coordinates.

	private ImageIcon helicopter, currentImage, currentBackground, Background, Bullet, Balloon, destroy;
	private static int xx, yy;
	private int moveX, moveY, health, damage, score;

	// Constructor: Sets up this panel and loads the images.
	
	public GamePanel() {
		addKeyListener(new GameListener());

		xx = WIDTH / 2;
		yy = HEIGHT / 2;

		Background = new ImageIcon("city_illustration.jpg");
		helicopter = new ImageIcon("helicopter1.gif");
		destroy = new ImageIcon("Helicopter_destroyed.gif");

		x[0] = 765;
		y[0] = (int) (Math.random() * (HEIGHT - 80));
		moveX = moveY = -1;
		health = 100;
		damage = 50;

		score = 0;

		timer = new Timer(DELAY, new BalloonListener());
		timerLabel = new String("Time: " + currentTime);

		Balloon = new ImageIcon("Balloon1.gif");
		Bullet = new ImageIcon("Missile.gif");

		currentBackground = Background;
		currentImage = helicopter;

		setBackground(Color.black);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
	}

	
	// Draws the image in the current location.
	
	public void paintComponent(Graphics page) {
		super.paintComponent(page);
		currentBackground.paintIcon(this, page, 0, 0);

		//Life bar
		page.setColor(Color.red);
		page.fillRect(644, 5, 150, 20);
		page.setColor(Color.green);
		page.fillRect(644, 5, (health + IMAGE_SIZE), 20);

		// Draw Timer
		page.setColor(Color.yellow);
		page.setFont(myFont);
		timerLabel = "Time: " + df2.format(currentTime);
		page.drawString(timerLabel, 10, 25);
		
		//Draw Score
		page.setColor(Color.orange);
		page.fillRect(644, 30, 150, 20);
		page.setColor(Color.white);
		page.fillRect(647, 32, 144, 16);
		page.setColor(Color.black);
		page.setFont(myFont);
		scoreLabel = "Score: " + score;
		page.drawString(scoreLabel, 648, 46);

		// Draw Game other game components like helicopter, bullet, balloons and destroy.
		if (gameover)
			destroy.paintIcon(this, page, xx, yy);
		else
			currentImage.paintIcon(this, page, xx, yy);
		for (int i = 0; i < activeBalloons.length; i++) {
			if (activeBalloons[i] == true)
				Balloon.paintIcon(this, page, x[i], y[i]);
		}
		
		for (int i = 0; i < activeBullets.length; i++) {
			if (activeBullets[i] == true)
				Bullet.paintIcon(this, page, a[i], b[i]);
		}
		page.setColor(Color.red);
		page.setFont(new Font("Arial", 1, 60));
		if (!start && !gameover) {
			page.drawString("Press \"Enter\" to start!", 75, HEIGHT / 2);
		} else if (gameover) {
			page.drawString("Game Over!", 250, HEIGHT / 4);
			page.drawString("Press  \"Enter\" to restart!", 75, HEIGHT / 2);
		}
	}

	
	// Represents the listener for keyboard activity.
	
	private class GameListener implements KeyListener {
		
		// Responds to the user pressing arrow keys by adjusting the
		// image and image location accordingly.
		
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_UP: //moves the helicopter up 10 pixels
				if (yy <= 0)
					yy = 0;
				else {
					currentImage = helicopter;
					yy -= JUMP;
				}
				break;
			case KeyEvent.VK_DOWN: //moves the helicopter down 10 pixels
				if (yy >= 520)
					yy = 520;
				else {
					currentImage = helicopter;
					yy += JUMP;
				}
				break;
			case KeyEvent.VK_LEFT: //moves the helicopter left 10 pixels
				if (xx <= 0)
					xx = 0;
				else {
					currentImage = helicopter;
					xx -= JUMP;
				}
				break;
			case KeyEvent.VK_RIGHT: //moves the helicopter Right 10 pixels
				if (xx >= 720)
					xx = 720;
				else {
					currentImage = helicopter;
					xx += JUMP;
				}
				break;
			case KeyEvent.VK_ENTER: //Starts the game
				if (!start) {
					start = true;
					gameover = false;
					restart();
					resetTimer();
					timer.start();
					BackSound(BackGround);
				} else if (gameover) {
					gameover = false;
				}
				break;
			case KeyEvent.VK_SPACE: //Fires missiles
				if (!fire)
					
					MissileSound(Missile);
					fire = true;
				break;

			}

			repaint();
		}

		
		// Provide empty definitions for unused event methods.
		
		public void keyTyped(KeyEvent event) {
		}

		public void keyReleased(KeyEvent event) {
		}
	}

	private class BalloonListener implements ActionListener {
		
		// Updates the position of the image and possibly the direction
		// of movement whenever the timer fires an action event.
		
		public void actionPerformed(ActionEvent event) {
			currentTime += .02; //counts the time by the timer delay
			currentTicks += 1; //counts how many times the panel has been repainted.
			if (start) // Starts the timer
			{
				// Creates new missiles when user hits spacebar.
				if (fire) {
					for (int j = 0; j < activeBullets.length; j++) {
						if (activeBullets[j] == false) {
							activeBullets[j] = true;
							a[j] = xx + 25;
							b[j] = yy + 25;
							break;
						}
					}
				}
				
				// For moving active balloons faster after every 15th balloon, uses time of deployment
				
				if(currentTicks%4500==0) {
					moveX--;
					moveY--;
				}
				
				// For moving active balloons

				for (int i = 0; i < activeBalloons.length; i++) {
					if (activeBalloons[i] == true) {
						x[i] += moveX;
						setCollision(x[i], y[i], i); // To check there is a collision with balloon and helicopter
						getCollision();
						if (hit) {
							gameover = true;
							BackClip.stop();
							stop();
							hit = false;
						}
						//For moving active bullets
						for (int j = 0; j < activeBullets.length; j++) {
							if (activeBullets[j] == true) {
								a[j] += (-moveX * 2);
								setAttack(x[i], y[i], a[j],b[j], i); //To check there has been an intercept
								getAttack();
								if (intercept)
								{
									destroyBalloon(i);
									destroyBullet(j);
									intercept = false;
								}
								
							}
						}
					}
				}

				// For generating new active balloons
				if (currentTicks % (150/(-moveX)) == 0) {
					for (int i = 0; i < activeBalloons.length; i++) {
						if (activeBalloons[i] == false) {
							activeBalloons[i] = true;
							x[i] = 765;
							y[i] = (int) (Math.random() * (HEIGHT - 80));
							break;
						}
					}
				}

				// If balloon leaves screen, set activeBalloon to false
				for (int i = 0; i < activeBalloons.length; i++) {
					if (x[i] < -60)
						activeBalloons[i] = false;
				}
			
				// If bullets leaves screen, set activeBullets to false
				for (int j = 0; j < activeBullets.length; j++) {
					if (x[j] < -60)
						activeBullets[j] = false;
				}

				fire = false;
				repaint();
			}

		}

	}

	// setCollision and getCollision get the information when helicopter hits a balloon.
	public void setCollision(int x, int y, int i){
		if (x < (xx + 70) && x > (xx - 40)) {
			if ((yy - (IMAGE_SIZE / 2)) < (y + 20) && (yy + (IMAGE_SIZE / 2)) > (y - 40)) {
				destroyBalloon(i);
				hit = true;
			}
		}
	} 

	public boolean getCollision() {
		if (hit && health != 0) // gets the health count down and moves the helicopter back.
		{
			if (xx < 720 && xx > 0 && yy > 480) {
				xx = xx - (moveX * JUMP);
				yy = yy + (moveY * JUMP);
				health = health - damage;
				hit = false;

			} else if (xx < 720 && xx > 0 && yy < IMAGE_SIZE) {
				xx = xx + (moveX * JUMP);
				yy = yy - (moveY * JUMP);
				health = health - damage;
				hit = false;

			} else {
				xx = xx + (moveX * JUMP);
				health = health - damage;
				hit = false;
			}
		} else if (hit && health == 0) // When health reaches 0, it returns "hit" as true.
		{
			start = false;
			for (int i = 0; i < activeBalloons.length; i++) {
				activeBalloons[i] = false;
			}
			health = health - damage;
			for (int i = 0; i < activeBalloons.length;) {
				activeBalloons[i] = true;
				break;
			}

		}

		return hit;
	}
	
	// setAttack and getAttact gets the information when balloons are intercepted by missile
	
	public void setAttack(int x,int y, int a,int b, int i){
		if ((b - 20) < (y + 20) && (b + 20) > (y - 40)) {
			if (x < (a + 20) && x > (a - 20)) {
				intercept = true;
			}
		}
	}

	public boolean getAttack() {
		
		return intercept;
	}

	// When a balloon is hit by helicopter or missile, the balloons are removed from screen by destroyBalloon
	
	public void destroyBalloon(int x) {
		score += 10;
		BlastSound(Blast);
		activeBalloons[x] = false;
	}
	
	// When a bullet hit the balloon it is removed from screen by destroyBullet
	
	public void destroyBullet(int x) {
		activeBullets[x] = false;
	}
	
	// Timer reset when game is over
	
	public void resetTimer() {
		this.currentTime = 0;
	}

	// Timer stop when game is over
	
	public void stop() {
		timer.stop();
	}

	//Resets all the variables back to original before the game restarts.
	
	public void restart() {
		xx = WIDTH / 2;
		yy = HEIGHT / 2;
		x[0] = 765;
		y[0] = (int) (Math.random() * (HEIGHT - 80));
		moveX = moveY = -1;
		health = 100;
		damage = 50;
		score = 0;
	}
	
	// All the sound files has been activated in the functions below. 
	// It includes the missile sounds, blast sounds when hit be missile or balloon and
	// the helicopter rotor noise as a background music. 
	
	public static void MissileSound(File M) 
	{
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(M));
			clip.start();	
		}catch (Exception e) {}
	}
	
	public static void BlastSound(File M)
	{
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(M));
			clip.start();
		}catch (Exception e) {}
	}
	
	public void BackSound(File M)
	{
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(M));
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			BackClip = clip;
		}catch (Exception e) {}
	}
	
}
