package com.madnes;

import java.awt.geom.Point2D;

public class Artifact extends GameObject {

    private double energy;
    public int type = (int)(Math.random() * 4); // type for draw in html

    public Artifact(double energy) {
        this.position = new Point2D.Double(0, 0);
        this.energy = energy;
    }

    public double getEnergy() {
        return energy;
    }

}
