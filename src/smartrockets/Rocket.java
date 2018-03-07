package smartrockets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Rocket {

    private double posX, posY, speedX, speedY, fitness;

    private double[][] genes;
    private int currentThruster, updatesReq;
    private static final double THRUSTER_MAX = 2.0;
    private static final double SPEED_MAX = 8.0;
    private boolean alive, targetHit;
    private Point target;
    
    public Rocket(int x, int y,Point target) {
        genes = new double[300][2];
        resetRocket(x,y);
        this.target = target;
    }

    public void resetRocket(int x, int y) {
        posX = x;
        posY = y;
        currentThruster = 0;
        updatesReq = 0;
        fitness = 0;
        alive = true;
    }
    
    //Calculate the rockets score. (basicly how well it did) we take the following things into account: time, target hit, dead?
    public void calcRocketScore(){
        fitness = 1 / (Math.hypot(posX - target.x, posY - target.y));
        if(targetHit){
            fitness *= 20;
            fitness *= ((1.0 / (double)(updatesReq + 1)) * 100) + 1;
        }
        if(!alive){
            fitness /= 10;
        }
    }

    //Generates random Genes
    public void getRandomGenes() {
        for (int i = 0; i < genes.length; i++) {
            genes[i][0] = (THRUSTER_MAX * 100 - SmartRockets.rng.nextInt((int) (THRUSTER_MAX * 200))) / 100;
            genes[i][1] = (THRUSTER_MAX * 100 - SmartRockets.rng.nextInt((int) (THRUSTER_MAX * 200))) / 100;
        }
    }

    public void draw(int oX, int oY,Graphics g) {
        g.setColor(Color.red);
        g.fillOval(oX + (int) posX - 6, oY + (int) posY - 6, 12, 12);
    }

    public void move() {
        if (alive && !targetHit) {
            if(Math.hypot(posX - target.x, posY - target.y) < 20){
                targetHit = true;
            }
            if (SmartRockets.isPointPathable((int) posX, (int) posY)) {
                posX += speedX;
                posY += speedY;
                updatesReq++;
                if(updatesReq > 150){
                    alive = false;
                }
            } else {
                alive = false;
            }
        }
    }

    public void applyThruster() {
        currentThruster++;
        if (currentThruster < genes.length) {
            speedX += genes[currentThruster][0];
            speedY += genes[currentThruster][1];
            if(Math.hypot(speedX, speedY) > SPEED_MAX){
                double angle = Math.atan2(speedY, speedX);
                speedX = SPEED_MAX * Math.cos(angle);
                speedY = SPEED_MAX * Math.sin(angle);
            }
        }
    }

    public double[][] getGenes() {
        return genes;
    }

    //Aquires Genes Randomly from 2 genes.
    public void getGenesFromParent(double[][] p1, double[][] p2) {
        for (int i = 0; i < genes.length; i++) {
            if (SmartRockets.rng.nextBoolean()) {
                genes[i][0] = p1[i][0];
                genes[i][1] = p1[i][1];
            } else {
                genes[i][0] = p2[i][0];
                genes[i][1] = p2[i][1];
            }
            mutation(i);
        }
    }

    //A small chance of the gen mutating.
    private void mutation(int i) {
        if (SmartRockets.rng.nextInt(100) < 5) {
            genes[i][0] = (THRUSTER_MAX * 100 - SmartRockets.rng.nextInt((int) (THRUSTER_MAX * 200))) / 100;
            genes[i][1] = (THRUSTER_MAX * 100 - SmartRockets.rng.nextInt((int) (THRUSTER_MAX * 200))) / 100;
        }
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getFitness() {
        return fitness;
    }

    public boolean isTargetHit() {
        return targetHit;
    }

}
