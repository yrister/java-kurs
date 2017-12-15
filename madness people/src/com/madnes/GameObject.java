package com.madnes;

import java.awt.geom.Point2D;

/*
* класс обьекта карты
*/
public abstract class GameObject {
    public Point2D.Double position = new Point2D.Double(0, 0);
    public Double size;
}
