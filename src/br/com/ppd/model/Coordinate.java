package br.com.ppd.model;

import java.io.Serializable;

public class Coordinate implements Serializable {

    private static final long serialVersionUID = 1L;

    private double x;

    private double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Double[] asArray() {
        return new Double[] { this.getX(), this.getY() };
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", this.x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        Coordinate coordinate = (Coordinate) o;
        if (this.x != coordinate.getX() || this.y != coordinate.getY())
            return false;
        else
            return true;
    }
}
