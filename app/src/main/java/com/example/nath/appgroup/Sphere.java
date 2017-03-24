package com.example.nath.appgroup;

/**
 * <b>Sphere is a class used to represent spheres of a given color.</b>
 * <p>
 * This class is used in the method spheres of the class Algorithms.
 * </p>
 *
 * @see Algorithms#spheres
 * @author Aziz Fouche
 * @version 1.0
 */
public class Sphere {
    /**
     * The x coordinate of the sphere's center.
     */
    private int centerX;

    /**
     * The y coordinate of the sphere's center.
     */
    private int centerY;

    /**
     * The radius of the sphere.
     */
    private int radius;

    /**
     * The ARGB color of the sphere.
     */
    private int color;

    public Sphere (int centerX, int centerY, int radius, int color) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.color = color;
    }

    public int getCenterX () {
        return this.centerX;
    }

    public int getCenterY () {
        return this.centerY;
    }

    public int getRadius () {
        return this.radius;
    }

    public int getColor () {
        return this.color;
    }
}
