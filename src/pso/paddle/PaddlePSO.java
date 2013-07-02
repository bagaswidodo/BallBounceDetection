package pso.paddle;

import pytaghoran.paddle.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

import pso.Particle;
import pso.Position;
import pso.Velocity;

public class PaddlePSO extends JPanel {
    //status properties

    private long startTime, endTime, elapsedTime;
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
    private float staticBallX = CANVAS_WIDTH - 2 * staticBallRadius;
    private float staticBallY = 240;
    private Color staticBallColor = Color.BLUE;
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
    private static LinkedList<Particle> swarm;


    public PaddlePSO() {
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
                        if (paddleX < 0) {
                            paddleX = 0;
                        } else {
                            paddleX -= 5;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if ((paddleX + paddleW) > CANVAS_WIDTH) {
                            paddleX = (CANVAS_WIDTH - paddleW);
                        } else {
                            paddleX += 5;
                        }
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


                    // Execute one update step
                    // Calculate the ball's new position
                    ballX += ballSpeedX;
                    ballY += ballSpeedY;
                    if (ballX > (CANVAS_WIDTH - ballRadius) || ballX < (ballRadius)) {
                        ballSpeedX = -ballSpeedX;
                    } else if (ballY > (CANVAS_HEIGHT - ballRadius) || ballY < (ballRadius)) {
                        ballSpeedY = -ballSpeedY;
                    } else if ((ballY + ballRadius) >= paddleY) {
                        int tempPoint = (int) (ballX);
                        if ((tempPoint > paddleX) && (tempPoint < paddleX + paddleW)) {
                            ballSpeedX = -ballSpeedX;
                            ballSpeedY = -ballSpeedY;
                            System.out.println("Paddle Collision");
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
                    int sumR = (int) ((ballRadius + staticBallRadius) * (ballRadius + staticBallRadius));
                    if (d < sumR) {
                        pBest = new LinkedList<Double>();
                        pBestLoc = new LinkedList<Position>();


                        l.add(new Titik(ballX, ballY));
                        l.add(new Titik(staticBallX, staticBallY));
                        execute();

                        delayGame();
                        ballSpeedX = -ballSpeedX;
                        ballSpeedY = -ballSpeedY;
                    } else {
                        staticBallColor = Color.BLUE;
                        runnerBallColor = Color.RED;
                    }

                    // Delay for timing control and give other threads a chance
                    try {
                        Thread.sleep(10);  // milliseconds
                    } catch (InterruptedException ex) {
                    }
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
                (int) (2 * ballRadius), (int) (2 * ballRadius));

        //draw the StaticBall;
        g.setColor(staticBallColor);
        g.fillOval((int) (staticBallX - staticBallRadius), (int) (staticBallY - staticBallRadius),
                (int) (2 * staticBallRadius), (int) (2 * staticBallRadius));

        // Draw The Paddle
        g.setColor(paddleColor);
        g.fillRect(paddleX, paddleY, paddleW, paddleH);

        //check
        g.setColor(Color.WHITE);
//      g.drawString("A", paddleX, paddleY);
//      g.drawString("B", paddleX+paddleW, paddleY);
//      g.drawString("R", (int)ballX, (int)(ballY+ballRadius));
//      g.drawString("Rx", (int)(ballX), (int)(ballY+ballRadius));
//      g.drawLine(0, paddleY, paddleX, paddleY);

        // Display the ball's information
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.PLAIN, 12));
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("Ball @(%3.0f,%3.0f) Speed=(%2.0f,%2.0f)", ballX, ballY,
                ballSpeedX, ballSpeedY);
        g.drawString(sb.toString(), 20, 30);
        g.drawString("Elapsed Time : " + elapsedTime + " ns", 300, 30);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Collision Detection Pytaghoran With Paddle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new PaddlePSO());
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    /*
     * Define methods of PSO
     */
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

