//Author: Anirban Biswas and Lee Vardaro.
//Purpose: To make a game similar to flappyBird and add upgrades to it. Upgrades are listed below the code. 

import javax.swing.*;

public class Game
{
   //  Displays the main frame of the program.
   
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("Crazy Choppah");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      frame.getContentPane().add(new GamePanel());
      frame.pack();
      frame.setVisible(true);
   }
}
/* The Program functionality and upgrades are as follows:

Functionality: 

The flappy bird program contained one object bird which used key-listener to jump
and skip over clouds that are timed to move forward toward the bird. If there is a collision between the bird
and the cloud it showed game-over and asked user if he wanted to restart the game. 
"Crazy Choppah" have used key-listener to move the helicopter around the panel to skip collision with balloons. 
It is timed to make the balloons move from one end of the screen to the other. It shows game over and asks user
if he/she wants to restart the game.

Upgrades in "Crazy Choppah":

1) Uses image in the Background instead of color only. 2)Uses image for Helicopter and Balloon instead of 
rectangles. 3) Uses a Timer to time the length of the game played. 4) Uses a health bar to keep track of 
helicopter's health. 5) Have a Score panel to record the score generated. 6) When there is a collision between
helicopter and balloon, helicopter jumps back. 7) Collision between balloon and helicopter reduces helicopter's 
health. 8) Space-bar generates missiles. 9) If a balloon is intercepted by missile, both goes off of the panel. 
10) After 15 balloons have been generated, the speed for balloons and missile go up. 11) The Balloon Generates 
faster when the speed goes up. 12) When the game start and till the game is over, there is a helicopter 
hovering noise in the background. 13) Collision and intercept creates explosion sound. 14) When the helicopter 
dies there is a explosion image generated. 15) Every time a balloon is popped the score adds 10 score and it
goes up. 16) Changed font for the life bar and health bar 17) When the game restarts, all attributes of the game
changed to their original value. */