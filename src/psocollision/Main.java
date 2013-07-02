/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psocollision;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;
import pso.Particle;
import pso.Position;
import pso.Velocity;

/** Bouncing Ball (Animation) via Swing Timer */
//@SuppressWarnings("serial")
public class Main extends JFrame implements Runnable {
    //status properties
    private boolean running = false;
    private long startTime,endTime,elapsedTime;
    
    // Define named-constants

    private static final int CANVAS_WIDTH = 640;
    private static final int CANVAS_HEIGHT = 480;
    private static final int UPDATE_PERIOD = 10; // milliseconds
    private static LinkedList<Particle> swarm;
    private DrawCanvas canvas;  // the drawing canvas (extends JPanel)
    // Attributes of moving object
    private int x1 = 100, y1 = 100;  // top-left (x, y)
    private int r1 = 25;        // width and height
    private int vX1 = 3, vY1 = 5; // displacement per step in x, y
    private Color c1 = Color.BLUE;
    private Color c2 = Color.RED;
    //another ball properties
    private int r2 = 75;
    private int x2 = CANVAS_WIDTH - 2 * r2;
    private int y2 = 240;
    private int vX2 = 3, vY2 = 5;
    
    /* =====================================================
     * PSO CONFIGURATION
     * =====================================================
     */
    
    //create list
    static LinkedList<Titik> l = new LinkedList<Titik>();
    /*
     * PSO Configuration
     */
    static int SWARM_SIZE = 2;
    static int DIMENSION = 2;
    static int MAX_ITERATION = 500;
    static double C1 = 2.0;
    static double C2 = 2.0;
    static double W_UP = 1.0;
    static double W_LO = 0.0;
    static LinkedList<Double> fitnessList = null;
    static LinkedList<Double> pBest = null;
    static LinkedList<Position> pBestLoc = null;
    static private Double gBest;
    static private Position gBestLoc;

    /** Constructor to setup the GUI components */
    public Main() {
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.setContentPane(canvas);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setTitle("Collision Dengan PSO");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        startGame();
    }

    private void startGame()
    {
        Thread t = new Thread(this);
        t.start();
    }
    
    public void delayGame() {
        c1 = Color.GREEN;
        c2 = Color.GREEN;
        vX1 = -vX1;
        vY1 = -vY1;


        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        startTime = 0;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }
    
    /** Update the (x, y) position of the moving object */
    public void update() {
        startTime = System.nanoTime();
        x1 += vX1;
        y1 += vY1;

        //properties to run big Ball
        x2++;
        if (x2 > CANVAS_WIDTH) {
            x2 = 0;
        }

        //wall collision
        if (x1 > CANVAS_WIDTH - 2 * r1 || x1 < 0) {
            vX1 = -vX1;
        } else if (y1 > CANVAS_HEIGHT - 2 * r1 || y1 < 0) {
            vY1 = -vY1;
        }

        double a = x1 - x2;
        double b = y1 - y2;

        double d = (a * a) + (b * b);
        int sumR = (r1 + r2) * (r1 + r2);
        if (d <= sumR) {
            //System.out.println("Collided !! BallA(" + x1 +"," + y1 + ") Ball B(" + x2 +"," + y2 + ")");
            //System.out.println("miring d : " + (Math.sqrt(d) - r1));
            //l = new LinkedList<Titik>();
            pBest = new LinkedList<Double>();
            pBestLoc = new LinkedList<Position>();


            l.add(new Titik(x1, y1));
            l.add(new Titik(x2, y2));
            execute();

            delayGame();
       
        } else {
            c1 = Color.BLUE;
            c2 = Color.RED;
        }
    }

    @Override
   public void run()
   {
       running = true;
       while (running) {
           repaint();
           update();   // update the (x, y) position

           try {
               Thread.sleep(10);
           } catch (InterruptedException e) {
           }
       }
   }

    /** DrawCanvas (inner class) is a JPanel used for custom drawing */
    private class DrawCanvas extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);  // paint parent's background
            setBackground(Color.BLACK);
            g.setColor(c1);
            g.fillOval(x1 - r1, y1 - r1, r1 * 2, r1 * 2);  // draw a circle

            //another circle
            g.setColor(c2);
            g.fillOval(x2 - r2, y2 - r2, r2 * 2, r2 * 2);

            //central point x
            g.setColor(Color.WHITE);
            g.drawString("A", x1, y1);
            g.drawString("B", x2, y2);
            g.drawString("Elapsed Time " + elapsedTime + " ns", 10, 10);
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

    /*
     * PSO BEGIN
     */
    /**
     * Create all the particle in the swarm
     */
    private static void initializeSwarm() {
        Particle p;
        //Random generator = new Random();
        swarm = new LinkedList<Particle>();
       // l = new LinkedList<Titik>();
        for (int i = 0; i < SWARM_SIZE; i++) {
            p = new Particle();
//            double posX = 3.0;
//            double posY = 2.0;
            p.setLocation(new Position(l.get(i).getX(), l.get(i).getY()));
//            System.out.println(posX + "," + posY);
            double velX = 2.0;
            double velY = 2.0;
            p.setVelocity(new Velocity(velX, velY));
            swarm.add(p);
        }
    }

    public static void execute() {
        Random generator = new Random();
        initializeSwarm();

        //evolutionaryStateEstimation();

        int t = 0;
        double w;

        while (t < MAX_ITERATION) {
            // calculate corresponding f(i,t) corresponding to location x(i,t)
            fitnessList = calculateAllFitness();

            // update pBest
            if (t == 0) {
                for (int i = 0; i < SWARM_SIZE; i++) {
                    pBest.add(i, fitnessList.get(i));
                    pBestLoc.add(i, swarm.get(i).getLocation());
                }
            } else {
                for (int i = 0; i < SWARM_SIZE; i++) {
                    if (fitnessList.get(i) < pBest.get(i)) {
                        pBest.remove(i);
                        pBestLoc.remove(i);
                        pBest.add(i, fitnessList.get(i));
                        pBestLoc.add(i, swarm.get(i).getLocation());
                    }
                }
            }

            int bestIndex = getBestParticle();
            // update gBest
            if (t == 0 || fitnessList.get(bestIndex) < gBest) {
                gBest = fitnessList.get(bestIndex);
                gBestLoc = swarm.get(bestIndex).getLocation();
            }

            w = W_UP - (((double) t) / MAX_ITERATION) * (W_UP - W_LO);

            for (int i = 0; i < SWARM_SIZE; i++) {
                // update particle Velocity
                double r1 = generator.nextDouble();
                double r2 = generator.nextDouble();
                double lx = swarm.get(i).getLocation().getX();
                double ly = swarm.get(i).getLocation().getY();
                double vx = swarm.get(i).getVelocity().getX();
                double vy = swarm.get(i).getVelocity().getY();
                double pBestX = pBestLoc.get(i).getX();
                double pBestY = pBestLoc.get(i).getY();
                double gBestX = gBestLoc.getX();
                double gBestY = gBestLoc.getY();

                double newVelX = (w * vx) + (r1 * C1) * (pBestX - lx)
                        + (r2 * C2) * (gBestX - lx);
                double newVelY = (w * vy) + (r1 * C1) * (pBestY - ly)
                        + (r2 * C2) * (gBestY - ly);
                swarm.get(i).setVelocity(new Velocity(newVelX, newVelY));

                // update particle Location
                double newPosX = lx + newVelX;
                double newPosY = ly + newVelY;
                swarm.get(i).setLocation(new Position(newPosX, newPosY));
            }
            //System.out.println("Iterasi ke =" + t + " Koordinat ~ : " + gBest);
            t++;
        }
        //System.out.println("Particle Pos" + );
        System.out.println("Fitness : " + gBest);
    }
    //Get the best particle index of the Swarm

    private static int getBestParticle() {
        Double Gbest = new Double(0);
        int GbestParticle = 0;
        for (int i = 0; i < SWARM_SIZE; i++) {
            pBest.get(i);
            if (pBest.get(i).floatValue() > Gbest.floatValue()) {
                Gbest = pBest.get(i);
                GbestParticle = i;
            }
        }
        return GbestParticle;
    }

    //Calculate the current fitness of all particle
    private static LinkedList<Double> calculateAllFitness() {
        // TODO Auto-generated method stub
        LinkedList<Double> ret = new LinkedList<Double>();
        for (int i = 0; i < SWARM_SIZE; i++) {
            ret.add(swarm.get(i).getFitness());
        }
        return ret;
    }
}