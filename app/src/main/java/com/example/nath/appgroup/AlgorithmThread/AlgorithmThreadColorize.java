package com.example.nath.appgroup.AlgorithmThread;

import com.example.nath.appgroup.Image;

/**
 * <b>AlgorithmThreadColorize is a thread which executes colorize algorithm</b>
 * <p>
 * This class is used by AlgorithmThread only. It represent a thread in the pool.
 * </p>
 *
 * @author Maxime Romeas Nathan Castets Aziz Fouche
 * @version 3.0
 */
class AlgorithmThreadColorize extends Thread {
    /**
     * Image to modify
     */
    private Image imgPointer;
    /**
     * Image w're looking for value
     */
    private Image img;
    /**
     * Top left corner of working area
     */
    private int left;
    /**
     * Top left corner of working area
     */
    private int top;
    /**
     * Right bottom corner of working area
     */
    private int right;
    /**
     * Right bottom corner of working area
     */
    private int bottom;
    /**
     * Hue value used by colorize
     */
    private int hue;


    /**
     * Instances object AlgorithmThreadColorFilter
     *
     * @param img
     * The image we're looking for value
     *
     * @param imgPointer
     * The image to modify
     *
     * @param left
     * top left corner of working area
     *
     * @param top
     * top left corner of working area
     *
     * @param right
     * right bottom corner of working area
     *
     * @param bottom
     * right bottom corner of working area
     *
     * @param hue
     * The hue value used in color filter
     *
     * @since 1.0
     */
    public AlgorithmThreadColorize(Image img, Image imgPointer, int left, int top, int right, int bottom, int hue) {
        this.img = img;
        this.imgPointer = imgPointer;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.hue = hue;
    }

    /**
     * Start color filter on the working area
     */
    public void run() {
        int w = right - left;
        int h = bottom - top;
        int[] tab = img.getPixels(left, top, right, bottom);

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];

            // RGB to HSV conversion (the hue is already given so we are not calculating it)
            double blue = (tmp & 0x000000FF) / 255.0;
            double green = ((tmp & 0x0000FF00) >> 8) / 255.0;
            double red = ((tmp & 0x00FF0000) >> 16) / 255.0;
            int alpha = (tmp & 0xFF000000) >> 24;

            // Calculates the maximum and the minimum of the RGB values
            double color_max = blue >= green ? (blue >= red ? blue : red) : (green >= red ? green : red);
            double color_min = blue <= green ? (blue <= red ? blue : red) : (green <= red ? green : red);
            double delta = color_max - color_min;

            double saturation = color_max == 0.0 ? 0.0 : delta / color_max;
            double value = color_max;

            // HSV to RGB conversion
            double c = value * saturation;
            double x = c * (1 - Math.abs((hue / 60.0) % 2 - 1));
            double m = value - c;

            // Checks the angle of the hue on the R/G/B color circle
            if (0 <= hue & hue < 60) {
                red = c;
                green = x;
                blue = 0;
            } else if (60 <= hue & hue < 120) {
                red = x;
                green = c;
                blue = 0;
            } else if (120 <= hue & hue < 180) {
                red = 0;
                green = c;
                blue = x;
            } else if (180 <= hue & hue < 240) {
                red = 0;
                green = x;
                blue = c;
            } else if (240 <= hue & hue < 300) {
                red = x;
                green = 0;
                blue = c;
            } else {
                red = c;
                green = 0;
                blue = x;
            }
            int red_new = (int) ((red + m) * 255);
            int green_new = (int) ((green + m) * 255);
            int blue_new = (int) ((blue + m) * 255);

            // Formats the new pixel
            int final_pix = (alpha << 24) | (red_new << 16) | (green_new << 8) | blue_new;
            tab[i] = final_pix;
        }
        imgPointer.setPixels(tab, left, top, right, bottom);
    }
}
