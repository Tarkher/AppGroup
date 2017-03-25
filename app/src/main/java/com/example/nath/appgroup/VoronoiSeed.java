package com.example.nath.appgroup;

/**
 * <b>VoronoiSeed is a class used to represent seeds in the Voronoi's diagram.</b>
 * <p>
 * This class is used in the method mosaic of the class Algorithms.
 * </p>
 *
 * @see Algorithms#mosaic
 * @author Maxime Romeas
 * @version 1.0
 */
public class VoronoiSeed {
    /**
     * The x coordinate of the seed in R^2.
     */
    private double x;

    /**
     * The y coordinate of the seed in R^2.
     */
    private double y;

    /**
     * The ARGB color of the seed in R^2.
     */
    private int color;

    /**
     * Voronoi's seed constructor.
     *
     * @param x
     * The x coordinate of the seed in the plan.
     *
     * @param y
     * The y coordinate of the seed in the plan.
     *
     * @param color
     * The ARGB color of the seed.
     */
    public VoronoiSeed (double x, double y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Returns the ARGB color of the seed.
     *
     * @return The ARGB color of the seed.
     */
    public int getColor() {
        return color;
    }

    /**
     * Returns the x coordinate of the seed.
     *
     * @return The x coordinates of the seed. Warning : it is a double.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the seed.
     *
     * @return The y coordinates of the seed. Warning : it is a double.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the euclidean distance between the seed and a pixel of the image.
     *
     * @param xPrime
     * The x integer coordinate of the point.
     *
     * @param yPrime
     * The y integer coordinate of the point.
     *
     * @return The euclidean distance between the seed and the integer point (xPrime, yPrime)
     */
    public double distance (int xPrime, int yPrime) {
        return Math.sqrt((x - xPrime) * (x - xPrime) + (y - yPrime) * (y - yPrime));
    }
}
