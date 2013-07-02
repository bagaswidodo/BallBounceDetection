package gacollision.paddle;
import static java.lang.Math.PI;

import java.awt.*;
import java.awt.event.*;
import java.util.Formatter;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jscience.mathematics.number.Float64;
 

final class Real implements Function<Genotype<Float64Gene >, Float64>
{
 @Override
 public Float64 apply (Genotype<Float64Gene> genotype ) {
	final double x = genotype.getGene ().doubleValue();
	final double y = genotype.getGene().doubleValue();
	return Float64.valueOf(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
	
 }
}


public class Main extends JPanel  {
    //status properties
    private long startTime,endTime,elapsedTime;
    
    // Canvas properties
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;    

    //paddle Properties
    private int paddleW = 200;
    private int paddleH = 100;    
    private int paddleX = 0;
    private int paddleY = 550;
    private Color paddleColor = Color.cyan;
       
   // Ball's properties (running)
   private float ballRadius = 25; // Ball's radius
   private float ballX = 100; // Ball's center (x, y)
   private float ballY = 100; 
   private float ballSpeedX = 3;   // Ball's speed for x and y
   private float ballSpeedY = 5;
   private Color runnerBallColor = Color.RED; 
   
   //Ball's properties (static)
   private float staticBallRadius = 100;
   private float staticBallX = CANVAS_WIDTH-2*staticBallRadius;
   private float staticBallY = 240;
   private float staticBallSpeedX = 3;
   private Color staticBallColor = Color.BLUE;
   
    public Main() {
        this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));        
          // To handle key events
          this.addKeyListener(new KeyAdapter() {
             @Override
             public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                case KeyEvent.VK_ESCAPE:   // ESC to quit
                   System.exit(0);
                   break;
                case KeyEvent.VK_LEFT:
                     if(paddleX < 0)
                        paddleX = 0;
                    else
                       paddleX -= 5;
                    break;
                case KeyEvent.VK_RIGHT:
                    if((paddleX+paddleW) > CANVAS_WIDTH)
                        paddleX = (CANVAS_WIDTH - paddleW);
                    else
                        paddleX += 5;
                break;

                }
             }
          });
          this.setFocusable(true);  // To receive key event
        
         Thread gameThread = new Thread() {
         public void run() {
             while (true) {
                 // Refresh the display
                 repaint(); // Callback paintComponent()
               
                 //properties to run big Ball
                 staticBallX+= staticBallSpeedX;
                 if (staticBallX > (CANVAS_WIDTH-staticBallRadius) || staticBallX < staticBallRadius ) {
                     staticBallSpeedX = -staticBallSpeedX;
                 }
                 
                 
                
               // Execute one update step
               // Calculate the ball's new position
               ballX += ballSpeedX;
               ballY += ballSpeedY;                                          
                if(ballX > (CANVAS_WIDTH - ballRadius) || ballX < (ballRadius))
                {
                    ballSpeedX = - ballSpeedX;
                }
                else if(ballY > (CANVAS_HEIGHT - ballRadius) || ballY < (ballRadius))
                {
                    ballSpeedY = -ballSpeedY;
                }
                    
               else if ((ballY + ballRadius) >= paddleY ) {
                    int tempPoint = (int) (ballX );
                    if ((tempPoint > paddleX) && (tempPoint < paddleX + paddleW)) {
                        ballSpeedX = -ballSpeedX;
                        ballSpeedY = -ballSpeedY;
                        //System.out.println("Paddle Collision");
                    }
                }
               
               /*
                 * Collision with pythagoran
                 */
                double a = ballX - staticBallX;
                double b = ballY - staticBallY;
                /*
                 * Collision occur when d^2 = a^2 + b ^2
                 * where
                 * a = x1 - x2; b = y1 - y2
                 */
                double d = (a * a) + (b * b);
                //int sumR = (int) ((ballRadius + staticBallRadius) * (ballRadius + staticBallRadius));
                int sumR = (int) (ballRadius+staticBallRadius);
                if (Math.sqrt(d) <= sumR) {
                    delayGame();                   
                    
                    /*
                     * call Jenetics Algorithm Framework
                     */
                    Factory<Genotype<Float64Gene>> gtf = Genotype
        					.valueOf(new Float64Chromosome(0.0, 2.0 * PI));
        			Function<Genotype<Float64Gene>, Float64> ff = new Real();
        			GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm<>(
        					gtf, ff, Optimize.MAXIMUM);

        			ga.setStatisticsCalculator(new NumberStatistics.Calculator<Float64Gene, Float64>());

        			ga.setPopulationSize(20);

        			ga.setAlterers(new Mutator<Float64Gene>(0.03),
        					new MeanAlterer<Float64Gene>(0.6));

        			ga.setup();
        			ga.evolve(100);
        			System.out.println(ga.getBestStatistics());
                    
                    
                    
                    ballSpeedX = -ballSpeedX;
                    ballSpeedY = -ballSpeedY;
                } else {
                    staticBallColor = Color.BLUE;
                    runnerBallColor = Color.RED;
                }
               
               // Delay for timing control and give other threads a chance
               try {
                  Thread.sleep(10);  // milliseconds
               } catch (InterruptedException ex) { }
            }
         }
      };
      gameThread.start(); 
        
    }
 
    public void delayGame() {
        //running = false;
        runnerBallColor = Color.GREEN;
        staticBallColor = Color.GREEN;

        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        startTime = 0;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            
        }

    }
    
     /** Custom rendering codes for drawing the JPanel */
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);    // Paint background
  
      // Draw the box
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
  
      // Draw the RunnerBall
      g.setColor(runnerBallColor);
      g.fillOval((int) (ballX - ballRadius), (int) (ballY - ballRadius),
            (int)(2 * ballRadius), (int)(2 * ballRadius));
  
      //draw the StaticBall;
      g.setColor(staticBallColor);
      g.fillOval((int) (staticBallX - staticBallRadius), (int) (staticBallY - staticBallRadius),
            (int)(2 * staticBallRadius), (int)(2 * staticBallRadius));
      
      // Draw The Paddle
      g.setColor(paddleColor);
      g.fillRect(paddleX, paddleY, paddleW, paddleH);
      
      //check
      g.setColor(Color.WHITE);
      
      // Display the ball's information
      g.setColor(Color.WHITE);
      g.setFont(new Font("Tahoma", Font.PLAIN, 12));
      StringBuilder sb = new StringBuilder();
      Formatter formatter = new Formatter(sb);
      formatter.format("Ball @(%3.0f,%3.0f) Speed=(%2.0f,%2.0f)", ballX, ballY,
            ballSpeedX, ballSpeedY);
      g.drawString(sb.toString(), 10, 20);
      g.drawString("Elapsed Time : " + elapsedTime/1000000000 + " ns", 300, 30);
      float jarak = (float) Math.sqrt(Math.pow((ballX-staticBallX), 2)+ Math.pow((ballY-staticBallY), 2));
      g.drawString("Distance " + jarak, 10, 35);
      
      //line
      g.drawLine((int)ballX, (int)ballY, (int)staticBallX, (int)staticBallY);
   }
        
    public static void main(String[] args) {
    	
        JFrame frame = new JFrame("Collision Detection GA With Paddle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new Main());
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
