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

    /**
     * Sphere constructor.
     *
     * @param centerX
     * The x coordinate of the sphere's center.
     *
     * @param centerY
     * The y coordinate of the sphere's center.
     *
     * @param radius
     * The radius of the sphere.
     *
     * @param color
     * The ARGB color of the sphere.
     */
    public Sphere (int centerX, int centerY, int radius, int color) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.color = color;
    }

    /**
     * Returns the x coordinate of the sphere's center.
     *
     * @return The x coordinates of the sphere's center.
     */
    public int getCenterX () {
        return this.centerX;
    }

    /**
     * Returns the y coordinate of the sphere's center.
     *
     * @return The y coordinates of the sphere's center.
     */
    public int getCenterY () {
        return this.centerY;
    }

    /**
     * Returns the radius of the sphere.
     *
     * @return The radius of the sphere.
     */
    public int getRadius () {
        return this.radius;
    }

    /**
     * Returns the ARGB color of the sphere.
     *
     * @return The ARGB color of the sphere.
     */
    public int getColor () {
        return this.color;
    }
}
