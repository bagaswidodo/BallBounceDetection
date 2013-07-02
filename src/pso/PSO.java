package pso;

import java.util.LinkedList;
import java.util.Random;

public class PSO {

        static LinkedList<Particle> swarm = null;
        static LinkedList<Double> fitnessList = null;
        static LinkedList<Double> pBest = null;
        static LinkedList<Position> pBestLoc = null;
    

        static int SWARM_SIZE = 2;
        static int DIMENSION = 2;
        static int MAX_ITERATION = 1000;
        static double C1 = 2.0;
        static double C2 = 2.0;
        static double W_UP = 1.0;
        static double W_LO = 0.0;
        
        static private Double gBest;
        static private Position gBestLoc;

        /**
         * Create all the particle in the swarm
         */
        private static void initializeSwarm() {
                Particle p;
                //Random generator = new Random();
                swarm = new LinkedList<Particle>();

                for (int i = 0; i < SWARM_SIZE; i++) {
                        p = new Particle();
                       double posX =  3.0 ;
                       double posY =  2.0 ;
                        p.setLocation(new Position(posX, posY));
                        System.out.println(posX + "," + posY);
                        double velX = 2.0 ;
                        double velY =2.0 ;
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
                        fitnessList=calculateAllFitness();

                        // update pBest
                        if (t == 0) {
                                for (int i = 0; i < SWARM_SIZE; i++) {
                                        pBest.add(i,fitnessList.get(i));
                                        pBestLoc.add(i,swarm.get(i).getLocation());
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
                        System.out.println("Iterasi ke ="+t+" Koordinat ~ : "+gBest);
                        t++;
                }
                //System.out.println("Particle Pos" + );
                System.out.println("Fitness : " + gBest);
        }
        //Get the best particle index of the Swarm
        private static int getBestParticle() {
                Double Gbest=new Double(0);
                int GbestParticle=0;
                for(int i=0;i<SWARM_SIZE;i++){
                        pBest.get(i);
                        if(pBest.get(i).floatValue()>Gbest.floatValue()){
                                Gbest=pBest.get(i);
                                GbestParticle=i;
                        }
                }
                return GbestParticle;
        }

        //Calculate the current fitness of all particle
        private static LinkedList<Double> calculateAllFitness() {
                // TODO Auto-generated method stub
                LinkedList<Double> ret=new LinkedList<Double>();
                for(int i=0;i<SWARM_SIZE;i++){
                        ret.add(swarm.get(i).getFitness());
                }
                return ret;
        }

        public static void main(String[] args) {
                // TODO Auto-generated method stub


                pBest = new LinkedList<Double>();
                pBestLoc = new LinkedList<Position>();
                execute();
        }

}