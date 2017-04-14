package com.example.nath.appgroup.AlgorithmThread;

import android.graphics.Color;

import com.example.nath.appgroup.Algorithms;
import com.example.nath.appgroup.Image;

/**
 * <b>AlgorithmThreadColorFilter is a thread which executes colorFilter algorithm</b>
 * <p>
 * This class is used by AlgorithmThread only. It represent a thread in the pool.
 * </p>
 *
 * @author Maxime Romeas Nathan Castets Aziz Fouche
 * @version 3.0
 */
class AlgorithmThreadColorFilter extends Thread {
    private Image img;
    private Image imgPointer;
    private int left;
    private int top;
    private int right;
    private int bottom;
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
    public AlgorithmThreadColorFilter(Image img, Image imgPointer, int left, int top, int right, int bottom, int hue) {
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

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                int pixel = tab[i * w + j];

                // Gets each RGB components of the pixel by filtering the Color integer and weights them to turn the image in gray level
                int blue = pixel & 0x000000FF;
                int green = (pixel & 0x0000FF00) >> 8;
                int red = (pixel & 0x00FF0000) >> 16;

                float hsv[] = new float[3];
                Color.RGBToHSV(red, green, blue, hsv);

                int[] borne = new int[4];
                int sizeBorne;

                if (hue >= 350) {
                    sizeBorne = 2;

                    borne[0] = Algorithms.floorMod(hue - 10, 360);
                    borne[1] = 360;
                    borne[2] = 0;
                    borne[3] = Algorithms.floorMod(hue + 10, 360);
                }
                else if (hue < 10) {
                    sizeBorne = 2;

                    borne[0] = Algorithms.floorMod(hue - 10, 360);
                    borne[1] = 360;
                    borne[2] = 0;
                    borne[3] = Algorithms.floorMod(hue + 10, 360);
                }
                else {
                    sizeBorne = 1;

                    borne[0] = Algorithms.floorMod(hue - 10, 360);
                    borne[1] = Algorithms.floorMod(hue + 10, 360);
                }

                boolean setToGray = true;
                for (int k = 0; k < sizeBorne; ++k) {
                    if (hsv[0] < borne[2 * k] || hsv[0] > borne[2 * k + 1])
                        setToGray = false;
                    else
                        setToGray = true;
                }

                if (!setToGray) {
                    // Contains the pixel's gray level
                    int color_custom = (int)(blue * 0.11) + (int)(green * 0.59) + (int)(red * 0.3);
                    // Makes an integer matching the Color's formatting
                    int final_pix = 0xFF000000 | (color_custom << 16) | (color_custom << 8) | color_custom;
                    tab[i * w + j] = final_pix;
                }
            }
        }

        imgPointer.setPixels(tab, left, top, right, bottom);
    }
}
