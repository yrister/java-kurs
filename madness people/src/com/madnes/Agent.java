package com.madnes;

import java.awt.geom.Point2D;

public class Agent extends GameObject implements Runnable {

    private double speed; // distance when agent moves // read from config
    private double direction = 0; // angle 0...2PI started from 3 o'clock anticlockwise // random in start and change whale play

    private double energy; // decrease with every move, hit and get damage // read from config
    private double power; // power of hit // read from config
    private double armor; // decreasing of damage // 0...1 // read from config

    public double getSpeed() { return speed; }
    public double getEnergy() { return energy;  }
    public double getPower() { return power; }
    public double getDirection() {
        return direction;
    }

    public boolean isAlive = true; // движется ли агент
    public boolean isVisable = true; // показывать ли агента на карте
    public int groupID = 0;
    public int agentID = 0;
    public double damage = 0; // полученный агентом урон

    public Agent(double speed, double energy, double power, double armor) {
        this.speed = speed;
        this.energy = energy;
        this.power = power;
        this.armor = armor;
        direction = Math.random() * Math.PI * 2;
    }

    /*
    * делает один ход агентом в соответсвии с направлением и скоростью
    */
    public void makeMove() {

        double minPos = 0;
        double maxX = StaticResource.fieldWidth - size;
        double maxY = StaticResource.fieldHeight - size;

        Point2D.Double moveVector = getVector();
        double newX = position.getX() + moveVector.x;
        double newY = position.getY() + moveVector.y;

        if (newX < minPos) {
            newX = minPos;
            direction = Math.PI - direction;
        } else if (newX > maxX) {
            newX = maxX;
            direction = Math.PI - direction;
        }

        if (newY < minPos) {
            newY = minPos;
            direction = -direction;
        } else if (newY > maxY) {
            newY = maxY;
            direction = -direction;
        }
        position.setLocation(newX, newY);

        energy -= StaticResource.energyPerMove * speed;
    }

    /*
    * ударить агента
    * @param power сила агента, который ударяет
    */
    public void makeHit(double power) {
        energy -= power * (1 - armor);
        damage += power * (1 - armor);
    }

    /*
    * добавить энергии (из артифакта)
    * @param energyPoints количетсво энергии
    */
    public void addEnergy(double energyPoints) {
        energy += energyPoints;
    }

    /*
    * добавить импульс (при ударении)
    * @param objectVector вектор импульса
    */
    public void applyCollisionImpulse(Point2D.Double objectVector) {

        // закон сохранения энергиии и импульса


        Point2D.Double selfVector = getVector();

        double newX = objectVector.getX() + selfVector.getX();
        double newY = objectVector.getY() + selfVector.getY();

        // we will not change speed
//        speed = Math.sqrt(newX*newX + newY*newY);

        if (newY == 0) {
            if (newX < 0) {
                direction = Math.PI;
                return;
            } else {
                direction = 0;
                return;
            }
        }
        double angle = Math.atan(newY / newX);
        if (newX < 0) {
            angle = Math.PI + angle;
        }
        direction = angle;
    }

    /*
    * векторное представление направление из угла и скорости
    * @param angle угол направления
    * @param speed скорость движения
    */
    private Point2D.Double getVectorFrom(double angle, double speed) {
        double normalAngle = angle % (Math.PI * 2);
        if (normalAngle < 0) {
            normalAngle = Math.PI * 2 + normalAngle;
        }

        double x = speed * Math.cos(normalAngle);
        double y = speed * Math.sin(normalAngle);
        return new Point2D.Double(x, y);
    }

    /*
    * векторное представление направления агента
    */
    public Point2D.Double getVector() {
        return getVectorFrom(direction, speed);
    }

    /*
    * движение агента выполняемое во втором потоке
    */
    @Override
    public void run() {
        while (isAlive) {
            if (GameRuntime.getInstance().isPlaying()) {
                makeMove();
            }
            try { Thread.sleep(StaticResource.timeForMove); }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
