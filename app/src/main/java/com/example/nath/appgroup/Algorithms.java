package com.example.nath.appgroup;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * <b>Algorithms contains all the algorithms used for image processing</b>
 * <p>
 * Furthermore, this class only contains public static method that are called in the MainActivity
 * and their private counterparts used for diverse tasks in the algorithms.
 * </p>
 *
 * @author Maxime Roméas Nathan Castets Aziz Fouché
 * @version 3.0
 */
public class Algorithms {
    /**
     * Turns a RGB image into a gray level one using an hexadecimal filtering.
     *
     * @param img
     * The image we work on
     *
     * @since 1.0
     */
    public static void toGray(Image img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());

        for (int i = 0; i < w * h; i++) {
            // Gets the ith argb pixel of the image
            int tmp = tab[i];
            // Gets each RGB components of the pixel by filtering the Color integer and weights them to turn the image in gray level
            int blue = (int) ((tmp & 0x000000FF) * 0.11);
            int green = (int) (((tmp & 0x0000FF00) >> 8) * 0.59);
            int red = (int) (((tmp & 0x00FF0000) >> 16) * 0.3);
            // Contains the pixel's gray level
            int color_custom = blue + green + red;
            // Makes an integer matching the Color's formatting
            int final_pix = 0xFF000000 | (color_custom << 16) | (color_custom << 8) | color_custom;
            // Replaces the pixel by the new (gray) one in the pixel array
            tab[i] = final_pix;
        }
        // Replaces the bitmap's pixels array by the gray one
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Turns a RGB image into a gray level one using the Color class.
     *
     * @param img
     * The image we work on
     *
     * @deprecated Since 1.0 because it's way slower than toGray.
     *
     * @see Algorithms#toGray
     *
     * @since 1.0
     */
    public static void toGrayOld(Image img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];
            int blue = (int) (Color.red(tmp) * 0.11);
            int green = (int) (Color.green(tmp) * 0.59);
            int red = (int) (Color.red(tmp) * 0.3);
            int color_custom = blue + green + red;
            int final_pix = Color.rgb(color_custom, color_custom, color_custom);
            tab[i] = final_pix;
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Increase or decrease the luminosity of an Image with a translation of factor lum.
     *
     * @param img
     * The image we work on
     *
     * @param lum
     * The translation's factor.
     * If lum equals 0 then the image remains unchanged.
     * If lum equals 255 (respectively -255) then the image becomes white (respectively black)
     *
     * @since 2.0
     */
    public static void luminosity(Image img, int lum) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, w, h);

        lum = lum > 255 ? 255 : (lum < -255 ? -255 : lum);

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];
            
            //Gets the RGB values of the pixel and translates each of them by a factor lum
            int blue = (tmp & 0x000000FF) + lum;
            int green = ((tmp & 0x0000FF00) >> 8) + lum;
            int red = ((tmp & 0x00FF0000) >> 16) + lum;
            int alpha = ((tmp & 0xFF000000) >> 24);
            
            // Makes sure that the values don't leave the interval [0,255] by thresholding
            blue = blue > 255 ? 255 : blue;
            red = red > 255 ? 255 : red;
            green = green > 255 ? 255 : green;
            blue = blue < 0 ? 0 : blue;
            red = red < 0 ? 0 : red;
            green = green < 0 ? 0 : green;
            
            int final_pix = (alpha << 24) | (red << 16) | (green << 8) | blue;
            tab[i] = final_pix;
        }
        img.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Generates the look up table used for the linear histogram extension that sends a gray level in
     * [minSource , maxSource] to an other gray level in the target interval [minTarget , maxTarget].
     *
     * @param minSource
     * The minimum value of the source interval.
     *
     * @param maxSource
     * The maximum value of the source interval.
     *
     * @param minTarget
     * The minimum value of the target interval.
     *
     * @param maxTarget
     * The maximum value of the target interval.
     *
     * @return The corresponding look up table which is an integer array.
     *
     * @see Algorithms#dynamicExtensionColor
     *
     * @since 1.0
     */
    private static int[] lookUpTable(int minSource, int maxSource, int minTarget, int maxTarget) {
        int[] tab = new int[256];
        for (int gray_lvl = minSource; gray_lvl < maxSource + 1; gray_lvl++) {
            tab[gray_lvl] = (int) (((maxTarget - minTarget) *
                    (((gray_lvl - minSource) / (double) (maxSource - minSource)))) + minTarget);
        }
        return tab;
    }

    private static int[] norm_h(int a, int b, int c, int d, int[] h) {//Normalize the cumulative histogram
        int[] tab = new int[256];
        for (int gray_lvl = 0; gray_lvl < 256; gray_lvl++) {
            tab[gray_lvl] = (int) (((d - c) * (((h[gray_lvl] - a) / (double) (b - a)))) + c);//bijection from [min,max] to [c,d]
        }
        return tab;
    }

    /**
     * Generates the gray level histogram for a gray level image which is useful to calculate its
     * cumulative histogram with cumulativeHistogram.
     *
     * @param tab
     * The pixels array of the image.
     *
     * @return The gray level histogram of the image which is an int array of size 256.
     *
     * @see Algorithms#cumulativeHistogram
     *
     * @since 2.0
     */
    private static int[] histogram(int[] tab) {
        int n = tab.length;
        // Creates an accumulator indexed on the possible gray levels of an image
        int[] h = new int[256];

        for (int tmp : tab) {
            h[tmp] += 1;
        }
        return h;
    }

    /**
     * Generates the gray level cumulative histogram for a gray level image which is useful to perform
     * the contrast equalization algorithm with constrastEqualization.
     *
     * @param tab1
     * The pixels array of the image.
     *
     * @return The gray level cumulative histogram of the image which is an int array of size 256.
     *
     * @see Algorithms#contrastEqualization
     *
     * @since 2.0
     */
    private static int[] cumulativeHistogram(int[] tab1) {
        int[] tab = histogram(tab1);
        int[] h = new int[256];

        h[0] = tab[0];

        for (int i = 1; i < 256; i++) {
            h[i] = h[i - 1] + tab[i];
        }

        return h;
    }

    /**
     * Increases the contrast of an RGB image with the linear dynamic extension algorithm performed
     * on the canal value of the HSV representation to preserve the coherence of the image.
     *
     * @param img
     * The image we work on.
     *
     * @see Algorithms#lookUpTable
     *
     * @since 1.0
     */
    public static void dynamicExtensionColor(Image img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int[] val = new int[size];

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;
            int green = (tmp & 0x0000FF00) >> 8;
            int red = (tmp & 0x00FF0000) >> 16;

            // Calculates the max of the R/G/B values (value field in HSV)
            val[i] = blue > red ? (blue > green ? blue : green) : (red > green ? red : green);
        }

        // Calculates the minimum and the maximum value (the V field in HSV) of the pixels
        int min = 255;
        int max = 0;

        for (int i = 0; i < size; i++) {
            max = val[i] > max ? val[i] : max;
            min = val[i] < min ? val[i] : min;
        }

        // Generates the appropriate look up table for the value component
        int[] LUT = lookUpTable(min, max, 0, 255);

        // Replaces the value (HSV wise) of each pixel by the appropriate value in the look up table.
        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;
            int green = (tmp & 0x0000FF00) >> 8;
            int red = (tmp & 0x00FF0000) >> 16;

            float[] hsv = new float[3];
            Color.RGBToHSV(red, green, blue, hsv);

            float new_value = LUT[val[i]] / 255.0f;

            hsv[2] = new_value;
            tmp = Color.HSVToColor(hsv);

            tab[i] = tmp;
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());
    }

    
    public static void contrastEqualization(Image img, int goal) {//Equalize the values of the pixels with the cumulative histogram for a better contrast result on dark pictures
        int w = img.getWidth();
        int hei = img.getHeight();
        int size = w * hei;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int[] val = new int[size];

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;
            int green = (tmp & 0x0000FF00) >> 8;
            int red = (tmp & 0x00FF0000) >> 16;

            // Calculates the value (HSV) of the pixel which is the max of the RGB canals
            val[i] = blue > red ? (blue > green ? blue : green) : (red > green ? red : green);
        }

        int[] h = cumulativeHistogram(val);
        int min = h[0];

        /*
        for (int i = 0; i < 256; i++) {//We get the min and max of pixels below a i level of value
            min = h[i] < min ? h[i] : min;
        }
        */

        //Send the values of the cumulative histogram in [0,255], max is always size by definition
        int[] LUT_value = norm_h(min, size, 0, goal, h);

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;
            int green = (tmp & 0x0000FF00) >> 8;
            int red = (tmp & 0x00FF0000) >> 16;
            
            // REFAIRE CA MANUELLEMENT
            float[] hsv = new float[3];
            Color.RGBToHSV(red, green, blue, hsv);
            
            float new_value = LUT_value[val[i]] / 255.0f;
            hsv[2] = new_value;
            
            tmp = Color.HSVToColor(hsv);
            tab[i] = tmp;
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Modifies the bitmap's hue.
     *
     * @param img
     * The image we work on.
     *
     * @param hue
     * The new hue of the image which is a double in the interval [0,360[
     *
     * @see Algorithms#lookUpTable
     *
     * @since 1.0
     */
    public static void colorize(Image img, double hue) {//Modifies the bitmap's hue with a self made hsv to rgb translator
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        img.getPixels(0, 0, img.getWidth(), img.getHeight());

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];
            
            // RGB to HSV conversion (the hue is already given so we are not calculating it)
            double blue = (tmp & 0x000000FF) / 255.0;
            double green = ((tmp & 0x0000FF00) >> 8) / 255.0;
            double red = ((tmp & 0x00FF0000) >> 16) / 255.0;
            
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
            int final_pix = 0xFF000000 | (red_new << 16) | (green_new << 8) | blue_new;
            tab[i] = final_pix;
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Calculates the convolution between the image and a generic mask. For specific usage of
     * the convolution operator see below.
     *
     * @param img
     * The image we work on.
     *
     * @param matrix
     * The mask used to calculate its convolution with the image.
     *
     * @see Algorithms#sobelEdgeDetector
     *      Algorithms#gaussianFilter
     *      Algorithms#meanFilter
     *      Algorithms#laplacien
     *      Algorithms#floorMod
     *
     * @since 2.0
     */
    public static void convolution(Image img, float[][] matrix) {
        int height = img.getHeight();
        int width = img.getWidth();
        int pixels[] = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int output[] = new int[width * height];

        // Calculates the maximum and minimum output of the convolution with the given mask
        float max_value = 0;
        float min_value = 0;
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] >= 0)
                    max_value += matrix[i][j] * 255;
                else
                    min_value += matrix[i][j] * 255;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = 0;

                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix.length; j++) {
                        int yPrime = floorMod(y + i - (matrix.length - 1) / 2, height);
                        int xPrime = floorMod(x + j - (matrix.length - 1) / 2, width);
                        value += (0x000000FF & pixels[yPrime * width + xPrime]) * matrix[i][j];
                    }
                }

                // Checks if the output is not in [0,255] and uses the appropriate bijection to fix it
                if (value > 255 || value < 0) {
                    value = (int) ((value - min_value)/(max_value - min_value)) * 255;
                }
                output[y * width + x] = 0xFF000000 | (value << 16) | (value << 8) | value;
            }
        }

        img.setPixels(output, 0, 0, width, height);
    }

    /**
     * Modulo with an always positive result.
     *
     * @param a
     * The integer for which we want to calculate its remainder in an euclidean division.
     *
     * @param b
     * The divisor of the euclidean division.
     *
     * @return The remainder r of the euclidean division of a by b which verifies 0 <= r < |b| 
     *
     * @see Algorithms#convolution
     *
     * @since 2.0
     */
    private static int floorMod(int a, int b) {
        int r = a % b;
        if (r < 0)
            r += b;
        return r;
    }

    //TODO: ZOOM------------------------------------------------------------------------------------

    /**
     * Calculates the zoomed image with aliasing for a given zoom factor
     *
     * @param img
     * The image we work on.
     *
     * @param zoom
     * The zoom factor. It can be less than 1 to unzoom.
     *
     * @see Algorithms#zoomNoAliasing
     *
     * @since 2.0
     */
    public void zoomAliasing(Bitmap img, ImageView image, float zoom) {//(x,y) is top left corner's pixel for the zoomed image
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = new int[h * w];
        img.getPixels(tab, 0, w, 0, 0, w, h);

        // Calculates the dimension of the new image
        int w_new = (int) (w * zoom);
        int h_new = (int) (h * zoom);
        int new_size = w_new * h_new;
        int[] tab_new = new int[new_size];

        // Goes through the new image
        for (int k = 0; k < new_size; k++) {
            // Calculates the corresponding lines and columns in the new image
            int i = k % w_new;
            int j = k / w_new;
            // Managing the difference of scale between the new and old image
            tab_new[j * w_new + i] = tab[(int) (j / zoom) * w + (int) (i / zoom)];
        }

        // Creates a new bitmap with the appropriate size with the new pixels
        Bitmap tmp = Bitmap.createBitmap(w_new, h_new, Bitmap.Config.ARGB_8888);
        tmp.setPixels(tab_new, 0, w_new, 0, 0, w_new, h_new);
        image.setImageBitmap(tmp);
    }

    /**
     * Calculates the zoomed image with no aliasing with a given zoom factor by weighting each pixels
     * by the area it occupies on the surface defined by the coordinates of the old pixels.
     *
     * @param img
     * The image we work on.
     *
     * @param float
     * The zoom factor. It can be less than 1 to unzoom.
     *
     *
     * @since 2.0
     */
    public void zoomNoAliasing(Bitmap img, ImageView image, float zoom) {//(x,y) is top left corner's pixel for the zoomed image
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = new int[h * w];
        img.getPixels(tab, 0, w, 0, 0, w, h);

        int w_new = (int) (w * zoom);
        int h_new = (int) (h * zoom);
        int new_size = w_new * h_new;
        int[] tab_new = new int[new_size];

        float[] offsets = {-0.5f, 0.5f};

        for (int k = 0; k < new_size; ++k) {
            int i = k % w_new;
            int j = k / w_new;
            float new_i = (i + 0.5f) / zoom;
            float new_j = (j + 0.5f) / zoom;
            int center_i = (int) (new_i + 0.5f);
            int center_j = (int) (new_j + 0.5f);
            double red = 0.0;
            double green = 0.0;
            double blue = 0.0;
            for (float offset_i : offsets) {
                for (float offset_j : offsets) {
                    float corner_i = new_i + offset_i;
                    float corner_j = new_j + offset_j;
                    int corner_color = tab[(int) (corner_j) * w + (int) (corner_i)];
                    float area = Math.abs(corner_i - center_i) * Math.abs(corner_j - center_j);
                    float proportion = (area == 0) ? 0 : area;
                    blue += (corner_color & 0x000000FF) * proportion;
                    green += ((corner_color & 0x0000FF00) >> 8) * proportion;
                    red += ((corner_color & 0x00FF0000) >> 16) * proportion;
                }
            }
            int color = 0xFF000000 | ((int) red << 16) | ((int) green << 8) | (int) blue;
            tab_new[k] = color;
        }

        Bitmap tmp = Bitmap.createBitmap(w_new, h_new, Bitmap.Config.RGB_565);//Creating a new well sized bitmap
        tmp.setPixels(tab_new, 0, w_new, 0, 0, w_new, h_new);//with the zoomed pixels
        image.setImageBitmap(tmp);//to put in the image view
    }

    public static void trace(Image source, Image draw, int threshold) { // Trace the draw gray image on the source image
        int w = source.getWidth();
        int h = source.getHeight();
        int size = w * h;
        int[] tab = source.getPixels(0, 0, source.getWidth(), source.getHeight());
        int[] tab2 = draw.getPixels(0, 0, source.getWidth(), source.getHeight());

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];

            int tmp2 = tab2[i];
            int gray = tmp2 & 0x000000FF; //Gets the gray level of the image to trace

            int pixel;
            if (gray > threshold) // if we are on an edge we set the color to black
                pixel = 0xFF000000;
            else
                pixel = tmp;
            tab[i] = pixel;
        }

        source.setPixels(tab, 0, 0, w, h);//Replaces the pixel array by the new one
    }

    private static float find_closest_value(float[] tab, float e) {
        float closest = tab[0];
        float d_min = Math.abs(closest - e);
        for (int i = 1; i < tab.length; i++) {
            float d = Math.abs(tab[i] - e);
            if (d < d_min) {
                d_min = d;
                closest = tab[i];
            }
        }
        return closest;
    }

    private static int HSVtoRGB(float h, float s, float v) {
        float r, g, b, f, p, q, t;
        r = 0.0f;
        b = 0.0f;
        g = 0.0f;
        int i;
        i = (int) Math.floor(h * 6);
        f = h * 6 - i;
        p = v * (1 - s);
        q = v * (1 - f * s);
        t = v * (1 - (1 - f) * s);
        switch (i % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
        }
        return 0xFF000000 | (Math.round(r * 255) << 16) | (Math.round(g * 255) << 8) | Math.round(b * 255);
    }

    static float[] RGBtoHSV(int r, int g, int b) {
        int max = Math.max(Math.max(r, g), b), min = Math.min(Math.min(r, g), b), d = max - min;
        float h, s = (max == 0 ? 0.0f : d / (1.0f * max)), v = max / 255.0f;


        if (max == min)
            h = 0;
        else if (max == r) {
            h = (g - b) + d * (g < b ? 6 : 0);
            h /= 6 * d;
        } else if (max == g) {
            h = (b - r) + d * 2;
            h /= 6 * d;
        } else {
            h = (r - g) + d * 4;
            h /= 6 * d;
        }
        float[] hsv = {h, s, v};
        return hsv;
    }

    public static void cartoonize(Image img, int n) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());

        // For the discrete space of HSV values we choose the roots of the nth Chebychev's polynomial (

        int j = 0;
        float[] hValues = new float[n / 2];
        float[] sValues = new float[n / 2];
        float[] vValues = new float[n / 2];
        for (int k = 1; k <= n; k++) {
            float root = (float) Math.cos((2 * k - 1) * Math.PI / (2 * n)); // Chebychev's polynomials roots
            if (root > 0) { // they are symmetrical so we take only the positive ones
                hValues[j] = root;
                sValues[j] = root;
                vValues[j] = root;
                j++;
            }
        }

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];

            int blue = tmp & 0x000000FF;//Gets the blue component of the pixel by filtering the Color integer
            int green = (tmp & 0x0000FF00) >> 8;//same for the green component
            int red = (tmp & 0x00FF0000) >> 16;//same for the red component

            float[] hsv = RGBtoHSV(red, green, blue);

            hsv[0] = Algorithms.find_closest_value(hValues, hsv[0]);
            hsv[1] = Algorithms.find_closest_value(sValues, hsv[1]);
            hsv[2] = Algorithms.find_closest_value(vValues, hsv[2]);

            tab[i] = HSVtoRGB(hsv[0], hsv[1], hsv[2]);
        }

        img.setPixels(tab, 0, 0, w, h);//Replaces the pixel array by the new one
    }

    public static void hough_transform(Image img, double rho, double theta, int threshold) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());

        // The resolution of the rho's discretisation in the Hough space is rho
        // The resolution of the theta's discretisation in the Hough space is theta

        int Ntheta = (int) (180.0 / theta); // In [0;pi] the normal parameters for a line are unique
        int Nrho = (int) Math.floor(Math.sqrt(w * w + h * h)); // The number of steps needed to describe the whole Hough space

        double drho = Math.floor(Math.sqrt(w * w + h * h)) / Nrho; // The size of a single step for rho
        double dtheta = Math.PI / Ntheta; // The size of a single step for theta

        int[][] acc = new int[Ntheta][Nrho];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = tab[i * w + j];
                int gray = pixel & 0x000000FF;
                if (gray > 200) {
                    for (int i_theta = 0; i_theta < Ntheta; i_theta++) {
                        double ith_theta = i_theta * dtheta;
                        double ith_rho = j * Math.cos(ith_theta) + (h - i) * Math.sin(ith_theta); // Parametrization of the line in the (x,y) plane
                        int j_rho = (int) (ith_rho / drho); // We find out the corresponding rho step
                        if (j_rho > 0 && j_rho < Nrho) // If it fits in our Hough plane
                            acc[i_theta][j_rho] += 1; // We increment all the pixels the discrete sinusoidal curve passes through
                    }
                }
            }
        }

        // Now the accumulator is set and we can find the lines in the image in the (rho, theta) plane

        ArrayList<Double[]> lines = new ArrayList<>(); // Will contain the lines in the (x, y) plane after a conversion of the ones from the (rho, theta) plane

        for (int i_theta = 0; i_theta < Ntheta; i_theta++) {
            for (int j_rho = 0; j_rho < Nrho; j_rho++) {
                if (acc[i_theta][j_rho] > threshold) {
                    double theta0 = i_theta * dtheta;
                    double rho0 = j_rho * drho;
                    // We are now trying to find the corresponding line y = a*x + b of the line rho0 = x * cos(theta0) + y * sin(theta0)

                    Double a, b;

                    if (Math.abs(theta0 - Math.PI / 2.0) < 0.1) { // The cosine is almost equal to zero (horizontal line)
                        a = 0.0;
                        b = rho0 / Math.sin(theta0);
                    } else if (Math.abs(theta0 - Math.PI) < 0.1 || Math.abs(theta0) < 0.1) { // The sine is almost equal to zero (vertical line)
                        a = Double.NaN; // Convention for vertical lines
                        b = rho0 / Math.cos(theta0);
                    } else { // classic line
                        a = (-1.0) * Math.cos(theta0) / Math.sin(theta0);
                        b = rho0 / Math.sin(theta0);
                    }

                    Double[] line = {a, b};
                    lines.add(line);
                }
            }
        }

        // Now we go back into the (x, y) plane and trace the lines onto the image
        int n_lines = lines.size(); // the number of lines we found in the image

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int on_a_line;
                for (int k = 0; k < n_lines; k++) {
                    Double[] t = lines.get(k); // t = {a, b} the coefficients of the line
                    if (t[0].equals(Double.NaN)) // If the line is vertical
                        on_a_line = j - t[1].intValue();
                    else
                        on_a_line = (int) ((h - i) - t[0] * j - t[1]); // Testing if the point is on the line ie if y - ax - b = 0

                    if (on_a_line == 0) {
                        tab[i * w + j] = 0xFFFF0000; // Drawing the (red) line
                    }
                }
            }
        }

        img.setPixels(tab, 0, 0, w, h); // Replaces the pixel array by the new one
    }


    /* -------------- Algorithms using convolutions ---------------- */

    public static void meanFilter (Image img) {
        Algorithms.toGray(img);
        float matrixMoyenneur[][] = {{1f/9f, 1f/9f, 1f/9f}, {1f/9f, 1f/9f, 1f/9f}, {1f/9f, 1f/9f, 1f/9f}};
        Algorithms.convolution(img, matrixMoyenneur);
    }

    public static void gaussianFilter (Image img) { // Do it with any size of filter (n)
        Algorithms.toGray(img);
        float[][] matrixGaussien = {{1f/98f, 2f/98f, 3f/98f, 2f/98f, 1f/98f}, {2f/98f, 6f/98f, 8f/98f, 6f/98f, 2f/98f},
                {3f/98f, 8f/98f, 10f/98f, 8f/98f, 3f/98f}, {2f/98f, 6f/98f, 8f/98f, 6f/98f, 2f/98f},
                {1f/98f, 2f/98f, 3f/98f, 2f/98f, 1f/98f}};
        Algorithms.convolution(img, matrixGaussien);
    }

    public static void laplacien (Image img) {
        Algorithms.toGray(img);
        float matrixLaplacien[][] = {{0,-1,0}, {-1,4,-1}, {0,-1,0}};
        Algorithms.convolution(img, matrixLaplacien);
    }

    public static void sobelEdgeDetector (Image img) {
        Algorithms.toGray(img);

        float[][] Gx = {{-1,0,1}, {-2,0,2}, {-1,0,1}};
        float[][] Gy = {{-1,-2,-1}, {0,0,0}, {1,2,1}};

        Image imgGx = img.clone();
        Image imgGy = img.clone();

        Algorithms.convolution(imgGx, Gx);
        Algorithms.convolution(imgGy, Gy);

        int[] imgGxPixels = imgGx.getPixels(0, 0, imgGx.getWidth(), imgGx.getHeight());
        int[] imgGyPixels = imgGy.getPixels(0, 0, imgGy.getWidth(), imgGy.getHeight());

        int[] output = new int[img.getHeight() * img.getWidth()];

        for (int i = 0; i < img.getHeight(); ++i) {
            for (int j = 0; j < img.getWidth(); ++j) {
                int valGx = 0x000000FF & (imgGxPixels[i * img.getWidth() + j]);
                int valGy = 0x000000FF & (imgGyPixels[i * img.getWidth() + j]);

                int value = 0;
                double val = Math.sqrt(valGx * valGx + valGy * valGy);
                if (val > 255.0)
                    value = (int) (val/(Math.sqrt(2) * 4 * 255));
                else
                    value = (int) val;
                output[i * img.getWidth() + j] = 0xFF000000 | (value << 16) | (value << 8) | value;
            }
        }

        img.setPixels(output, 0, 0, img.getWidth(), img.getHeight());
    }
}
