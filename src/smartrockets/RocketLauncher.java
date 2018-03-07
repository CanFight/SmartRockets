package smartrockets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class RocketLauncher {

    private ArrayList<Rocket> rockets;
    private Point target;
    private Point launcherPosition;

    public RocketLauncher() {
        target = new Point(500, 500);
        launcherPosition = new Point(100, 100);
        rockets = new ArrayList();
        for (int i = 0; i < 20; i++) {
            rockets.add(new Rocket(launcherPosition.x, launcherPosition.y, target));
            rockets.get(rockets.size() - 1).getRandomGenes();
        }
    }

    public void draw(int oX, int oY, Graphics g) {
        g.setColor(Color.cyan);
        g.fillOval(oX + target.x - 10, oY + target.y - 10, 20, 20);
        g.setColor(Color.magenta);
        g.fillOval(oX + launcherPosition.x - 20, oY + launcherPosition.y - 20, 40, 40);
        for (Rocket r : rockets) {
            r.draw(oX, oY, g);
        }
    }

    public void update() {
        int aliveRockets = 0;
        for (Rocket r : rockets) {
            r.applyThruster();
            r.move();
            if (r.isAlive() && !r.isTargetHit()) {
                aliveRockets++;
            }
        }
        if (aliveRockets == 0) {
            newRocketPool();
        }
    }

    private void newRocketPool() {
        ArrayList<Rocket> parents = new ArrayList();
        //Calculate the fitness of all our rockets.
        for (Rocket r : rockets) {
            r.calcRocketScore();
        }

        //Adds a rocket to the array of parents a number of times based on it's fitness.
        for (Rocket r : rockets) {
            int pickRate = (int) (r.getFitness() * 10000.0);
            for (int i = 0; i < pickRate; i++) {
                parents.add(r);
            }
        }
        //generate the new rockets based on the old rockets dna.
        if (!parents.isEmpty()) {
            rockets.clear();
            for (int i = 0; i < 20; i++) {
                rockets.add(new Rocket(launcherPosition.x, launcherPosition.y, target));
                rockets.get(rockets.size() - 1).getGenesFromParent(parents.get(SmartRockets.rng.nextInt(parents.size())).getGenes(),
                        parents.get(SmartRockets.rng.nextInt(parents.size())).getGenes());
            }
        } else {
            System.out.println("BAD!!!");
            for (Rocket r : rockets) {
                r.resetRocket(launcherPosition.x, launcherPosition.y);
            }
        }
    }
    
    public void moveLauncher(int x, int y){
        launcherPosition.setLocation(x, y);
    }
    
    public void moveTarget(int x, int y){
        target.setLocation(x, y);
    }
}
