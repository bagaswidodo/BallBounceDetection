/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pytaghoran;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
 
/** Bouncing Ball (Animation) via Swing Timer */
//@SuppressWarnings("serial")
public class Main extends JFrame implements Runnable {
   // Define named-constants
   private static final int CANVAS_WIDTH = 640;
   private static final int CANVAS_HEIGHT = 480;
   private static final int UPDATE_PERIOD = 10; // milliseconds
 
   private DrawCanvas canvas;  // the drawing canvas (extends JPanel)
   // Attributes of moving object
   private int x1 = 100, y1 = 100;  // top-left (x, y)
   private int r1 = 25;        // width and height
   private int vX1 = 3, vY1 = 5; // displacement per step in x, y
   private Color c1 = Color.BLUE; //dynamic ball color
   private Color c2 = Color.RED; //static ball color
   
   //another ball properties
   private int r2 = 75;
   private int x2 = CANVAS_WIDTH-2*r2;
   private int y2 = 240;
   private int vX2 = 3, vY2 = 5;
   
   //point ball
   private int cx,cy;
 
   //time properties
   private long elapsedTime;
   private long startTime;
   private long endTime;
   
   //paddle pos
   private int paddleX = CANVAS_WIDTH-530;
   private int paddleY = CANVAS_HEIGHT-30;
   /** Constructor to setup the GUI components */
   
   public Main() {
       canvas = new DrawCanvas();
       canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
       this.setContentPane(canvas);
       this.setDefaultCloseOperation(EXIT_ON_CLOSE);
       this.pack();
       this.setTitle("Collision Dengan Pytagoran Theorem");
       this.setResizable(false);
       this.setLocationRelativeTo(null);
       this.setVisible(true);
       startGame();

   }
 
   boolean running = false;
    @Override
   public void run()
   {
       running = true;
       while (running) {
           repaint();
           update();   // update the (x, y) position

           try {
               Thread.sleep(30);
           } catch (InterruptedException e) {
           }
       }
   }
   
    public void delayGame() {
        //running = false;
        c1 = Color.GREEN;
        c2 = Color.GREEN;

        cx = x1 + x2 / 2;
        cy = y1 + y2 / 2;
        //System.out.println(cx + "," + cy);
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        startTime = 0;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

    }
       
   private void startGame()
   {
      Thread t = new Thread(this);
      t.start();
      
   }
   /** Update the (x, y) position of the moving object */
   public void update() {
      startTime = System.nanoTime();
      x1 += vX1;
      y1 += vY1;
        
      
      //wall collision
      if (x1 > CANVAS_WIDTH - r1 || x1 < 0) {
         vX1 = -vX1;
      }else if (y1 > CANVAS_HEIGHT - r1 || y1 < 0) {
         vY1 = -vY1;
      }

      
      //properties to run big Ball
      x2+= vX2;
      if (x2 > (CANVAS_WIDTH-r2) || x2 < r2) {
          vX2= -vX2;
      }
      
      double a = x1-x2;
      double b = y1-y2;
       /*
       * Collision occur when d^2 = a^2 + b ^2
       * where
       * a = x1 - x2; b = y1 - y2
       */
       double d = (a*a) + (b*b);
       int sumR = ( r1 + r2)*( r1 + r2);
       if (d <= sumR) {
           delayGame();
           
			/*
			 * newVelX1 = (vx1 + (2 * vx2)); 
			 * newVelY1 = (vy1 + (2 * vy2));
			 * x1+=newVelX1; 
			 * y1+=newVely1
			 */
           
           
/*           float newVelX1 = (vX1 + 2*vX2);
           float newVelY1 = (0 + 2*vY2);
           
           x1 += newVelX1;
           y1 += newVelY1;*/
           

	           vX1 = -vX1;
	           vY1 = -vY1;

      } else {
           c1 = Color.BLUE;
           c2 = Color.RED;
       }
   }
 
     
   /** DrawCanvas (inner class) is a JPanel used for custom drawing */
   private class DrawCanvas extends JPanel {
      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);  // paint parent's background
         setBackground(Color.BLACK);
         g.setColor(c1);
         g.fillOval(x1-r1, y1-r1, r1*2, r1*2);  // draw a circle
         
         //another circle
         g.setColor(c2);
         g.fillOval(x2-r2, y2-r2, r2*2, r2*2);
         
         //central point x
         g.setColor(Color.WHITE);
         g.drawString("A", x1,y1);
         g.drawString("B",x2,y2);
         
         //status 
//         elapsedTime = endTime - startTime;
         g.setColor(Color.white);
         g.setFont(new Font("Tahoma", Font.PLAIN, 12));
//         g.drawString("Striker X  :\t" + x1 + " Y : " + y1, 10, 10);
         g.drawString("Elapsed Time " + elapsedTime + " ns", 10, 25);
         
         float jarak = (float) Math.sqrt(Math.pow((x1-x2), 2) + Math.pow(y1-y2, 2));
         g.drawString("Distance : " + jarak, 10, 40);
         
         //line
         g.drawLine(x1, y1, x2, y2);
      }
   }
 
   /** The entry main method */
   public static void main(String[] args) {
      // Run GUI codes in Event-Dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new Main(); // Let the constructor do the job
         }
      });
   }
}
