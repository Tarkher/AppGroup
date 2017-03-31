package com.example.nath.appgroup.AlgorithmThread;

import com.example.nath.appgroup.Image;

public class AlgorithmThreadToGray extends Thread {
    private Image imgPointer;
    private int left;
    private int top;
    private int right;
    private int bottom;

    public AlgorithmThreadToGray(Image imgPointer, int left, int top, int right, int bottom) {
        this.imgPointer = imgPointer;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void run() {
        int w = right - left;
        int h = bottom - top;
        int[] tab = imgPointer.getPixels(left, top, right, bottom);

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
