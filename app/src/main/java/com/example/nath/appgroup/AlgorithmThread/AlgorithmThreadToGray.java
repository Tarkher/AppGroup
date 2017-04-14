package com.example.nath.appgroup.AlgorithmThread;

import com.example.nath.appgroup.Image;

/**
 * <b>AlgorithmThreadToGray is a thread which executes toGray algorithm</b>
 * <p>
 * This class is used by AlgorithmThread only. It represent a thread in the pool.
 * </p>
 *
 * @author Maxime Romeas Nathan Castets Aziz Fouche
 * @version 3.0
 */
class AlgorithmThreadToGray extends Thread {
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
     * @since 1.0
     */
    public AlgorithmThreadToGray(Image img, Image imgPointer, int left, int top, int right, int bottom) {
        this.img = img;
        this.imgPointer = imgPointer;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * Start color filter on the working area
     */
    public void run() {
        int w = right - left;
        int h = bottom - top;
        int[] tab = img.getPixels(left, top, right, bottom);

        for (int i = 0; i < w * h; i++) {
            // Gets the ith argb pixel of the image
            int tmp = tab[i];
            // Gets each RGB components of the pixel by filtering the Color integer and weights
            // them to turn the image in gray level
            int blue = (int) ((tmp & 0x000000FF) * 0.11);
            int green = (int) (((tmp & 0x0000FF00) >> 8) * 0.59);
            int red = (int) (((tmp & 0x00FF0000) >> 16) * 0.3);
            int alpha = (tmp & 0xFF000000) >> 24;
            // Contains the pixel's gray level
            int color_custom = blue + green + red;
            // Makes an integer matching the Color's formatting
            int final_pix = (alpha << 24) | (color_custom << 16) | (color_custom << 8) | color_custom;
            // Replaces the pixel by the new (gray) one in the pixel array
            tab[i] = final_pix;
        }
        // Replaces the bitmap's pixels array by the gray one
        imgPointer.setPixels(tab, left, top, right, bottom);
    }
}
