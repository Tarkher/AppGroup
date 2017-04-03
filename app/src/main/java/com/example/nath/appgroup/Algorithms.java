package com.example.nath.appgroup;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * <b>Algorithms contains all the algorithms used for image processing</b>
 * <p>
 * Furthermore, this class only contains public static method that are called in the MainActivity
 * and their private counterparts used for diverse tasks in the algorithms.
 * </p>
 *
 * @author Maxime Romeas Nathan Castets Aziz Fouche
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
     * Turns all the pixels of the image into gray level except the ones of a given color.
     *
     * @param img
     * The image we work on
     *
     * @param degree
     * The degree of the hue (in the HSV representation) of the pixels not affected by the filter.
     *
     * @since 4.0
     */
    public static void colorFilter(Image img, int degree) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, w, h);

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

                if (degree >= 350) {
                    sizeBorne = 2;

                    borne[0] = floorMod(degree - 10, 360);
                    borne[1] = 360;
                    borne[2] = 0;
                    borne[3] = floorMod(degree + 10, 360);
                }
                else if (degree < 10) {
                    sizeBorne = 2;

                    borne[0] = floorMod(degree - 10, 360);
                    borne[1] = 360;
                    borne[2] = 0;
                    borne[3] = floorMod(degree + 10, 360);
                }
                else {
                    sizeBorne = 1;

                    borne[0] = floorMod(degree - 10, 360);
                    borne[1] = floorMod(degree + 10, 360);
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

        img.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Increase or decrease the "luminosity" of an Image with a multiplication of factor lum.
     *
     * @param img
     * The image we work on
     *
     * @param lum
     * The multiplication's factor.
     * If lum equals 0 then the image becomes black.
     * If lum equals 1 then the image remains unchanged.
     *
     * @since 2.0
     */
    public static void multiplicativeLuminosity(Image img, double lum) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, w, h);

        lum = lum > 255 ? 255 : (lum < 0 ? 0 : lum);

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];

            //Gets the RGB values of the pixel and multiplies each of them by a factor lum
            int blue = (int) ((tmp & 0x000000FF) * lum);
            int green = (int) (((tmp & 0x0000FF00) >> 8) * lum);
            int red = (int) (((tmp & 0x00FF0000) >> 16) * lum);
            int alpha = ((tmp & 0xFF000000) >> 24);

            // Makes sure that the values don't leave the interval [0,255] by thresholding
            // The values can be less than zero so ne tests are needed for that
            blue = blue > 255 ? 255 : blue;
            red = red > 255 ? 255 : red;
            green = green > 255 ? 255 : green;

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

    /**
     * Generates the look up table used for the contrast equalization that sends a gray level in
     * [minSource , maxSource] to its corresponding gray level in the target interval [minTarget , maxTarget].
     * To understand, if x % of the gray levels are below the gray level n then the gray level n
     * will be sent to x % of maxTarget.
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
    private static int[] norm_h(int minSource, int maxSource, int minTarget, int maxTarget, int[] h) {
        int[] tab = new int[256];
        for (int gray_lvl = 0; gray_lvl < 256; gray_lvl++) {
            tab[gray_lvl] = (int) (((maxTarget - minTarget) * (((h[gray_lvl] - minSource) /
                    (double) (maxSource - minSource)))) + minTarget);
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
        // Creates an accumulator indexed on the possible gray levels of an image
        int[] h = new int[256];

        for (int tmp : tab)
            h[tmp] += 1;

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

        for (int i = 1; i < 256; i++)
            h[i] = h[i - 1] + tab[i];

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

    /**
     * Increases the contrast of an RGB image with the contrast equalization algorithm performed
     * on the canal value of the HSV representation to preserve the coherence of the image.
     *
     * @param img
     * The image we work on.
     *
     * @param goal
     * The new maximum color value of the image's pixels.
     *
     * @see Algorithms#norm_h
     * @see Algorithms#cumulativeHistogram
     *
     * @since 2.0
     */
    public static void contrastEqualization(Image img, int goal) {
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

        // Gets the cumulative histogram
        int[] h = cumulativeHistogram(val);
        int min = h[0];

        // Sends the values of the cumulative histogram in [0,255], max is always size by definition
        int[] LUT_value = norm_h(min, size, 0, goal, h);

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;
            int green = (tmp & 0x0000FF00) >> 8;
            int red = (tmp & 0x00FF0000) >> 16;

            float[] hsv = new float[3];
            Color.RGBToHSV(red, green, blue, hsv);

            // Modifies the value of the pixel thanks to the look up table
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
    public static void colorize(Image img, double hue) {
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
     * @see Algorithms#gaussianFilter
     * @see Algorithms#meanFilter
     * @see Algorithms#laplacien
     * @see Algorithms#floorMod
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
                int alpha = (pixels[y * width + x] & 0xFF000000) >> 24;

                // Checks if the output is not in [0,255] and uses the appropriate bijection to fix it
                if (value > 255 || value < 0)
                    value = (int) ((value - min_value)/(max_value - min_value)) * 255;

                output[y * width + x] = (alpha << 24) | (value << 16) | ( value << 8) | value;
            }
        }

        img.setPixels(output, 0, 0, width, height);
    }

    /**
     * Calculates the convolution between the ARGB image and a generic mask on the HSV's value canal
     * to preserve the image's coherence and manages values out of range by thresholding (for the
     * sharpness modifiers algorithms).
     * For specific usage of the convolution operator see below.
     *
     * @param img
     * The image we work on.
     *
     * @param matrix
     * The mask used to calculate its convolution with the image.
     *
     * @see Algorithms#sharpens
     * @see Algorithms#contrastFilter
     *
     * @since 4.0
     */
    public static void convolutionColor(Image img, float[][] matrix) {
        int height = img.getHeight();
        int width = img.getWidth();
        int pixels[] = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int output[] = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float result = 0;

                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix.length; j++) {
                        int yPrime = floorMod(y + i - (matrix.length - 1) / 2, height);
                        int xPrime = floorMod(x + j - (matrix.length - 1) / 2, width);

                        int tmp = pixels[yPrime * width + xPrime];

                        int value = 0;

                        int blue = tmp & 0x000000FF;
                        int green = (tmp & 0x0000FF00) >> 8;
                        int red = (tmp & 0x00FF0000) >> 16;

                        // Calculates the value (HSV) of the pixel which is the max of the RGB canals
                        value = blue > red ? (blue > green ? blue : green) : (red > green ? red : green);

                        result += value * matrix[i][j];
                    }
                }

                if (result > 255)
                    result = 255.0f;
                else if (result < 0)
                    result = 0.0f;

                float [] hsv = new float[3];
                int tmp = pixels[y * width + x];

                int blue = tmp & 0x000000FF;
                int green = (tmp & 0x0000FF00) >> 8;
                int red = (tmp & 0x00FF0000) >> 16;
                int alpha = (tmp & 0xFF000000) >> 24;

                Color.RGBToHSV(red, green, blue, hsv);
                hsv[2] = result/255.0f;
                output[y * width + x] = Color.HSVToColor(alpha, hsv);
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
    public static int floorMod(int a, int b) {
        int r = a % b;
        if (r < 0)
            r += b;
        return r;
    }

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
    public void zoomAliasing(Bitmap img, ImageView image, float zoom) {
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
     * @param zoom
     * The zoom factor. It can be less than 1 to unzoom.
     *
     * @since 2.0
     */
    public void zoomNoAliasing(Bitmap img, ImageView image, float zoom) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = new int[h * w];
        img.getPixels(tab, 0, w, 0, 0, w, h);

        // Calculates the dimension of the zoomed image
        int w_new = (int) (w * zoom);
        int h_new = (int) (h * zoom);
        int new_size = w_new * h_new;
        int[] tab_new = new int[new_size];

        float[] offsets = {-0.5f, 0.5f};

        for (int k = 0; k < new_size; ++k) {
            // Calculates the corresponding pixel in the new image
            int i = k % w_new;
            int j = k / w_new;
            
            // Calculates the coordinates of the center of the pixel in the old image
            float new_i = (i + 0.5f) / zoom;
            float new_j = (j + 0.5f) / zoom;
            
            // Calculates the nearest center with integer coordinates of
            // the square of corner (new_i,new_j) in the old image
            int center_i = (int) (new_i + 0.5f);
            int center_j = (int) (new_j + 0.5f);
            
            double red = 0.0;
            double green = 0.0;
            double blue = 0.0;
            
            // Goes through the four corners
            for (float offset_i : offsets) {
                for (float offset_j : offsets) {
                    float corner_i = new_i + offset_i;
                    float corner_j = new_j + offset_j;
                    int corner_color = tab[(int) (corner_j) * w + (int) (corner_i)];
                    float area = Math.abs(corner_i - center_i) * Math.abs(corner_j - center_j);
                    blue += (corner_color & 0x000000FF) * area;
                    green += ((corner_color & 0x0000FF00) >> 8) * area;
                    red += ((corner_color & 0x00FF0000) >> 16) * area;
                }
            }
            int color = 0xFF000000 | ((int) red << 16) | ((int) green << 8) | (int) blue;
            tab_new[k] = color;
        }

        Bitmap tmp = Bitmap.createBitmap(w_new, h_new, Bitmap.Config.ARGB_8888);
        tmp.setPixels(tab_new, 0, w_new, 0, 0, w_new, h_new);
        image.setImageBitmap(tmp);
    }

    /**
     * Trace a binary image on a source image by inverting the pixels of the binary trace and drawing
     * the black ones onto the source to emphasize the edges.
     *
     * @param source
     * The image to trace on.
     *
     * @param draw
     * The trace (usually the edges detected with the canny edge detector
     *
     *
     * @since 3.0
     */
    public static void traceEdges(Image source, Image draw) {
        int w = source.getWidth();
        int h = source.getHeight();
        int size = w * h;
        
        // The source image
        int[] tab = source.getPixels(0, 0, source.getWidth(), source.getHeight());
        // The trace
        int[] tab2 = draw.getPixels(0, 0, draw.getWidth(), draw.getHeight());

        for (int i = 0; i < size; i++) {
            // The pixels of the source image
            int tmp = tab[i];
            // The pixels of the trace
            int tmp2 = tab2[i];
            
            // Gets the gray level of the image to trace
            int gray = tmp2 & 0x000000FF;

            int pixel;
            // If we are on a pixel to trace onto the source we set the color to black
            // Otherwise we keep the pixel of the source as it is
            if (gray == 255)
                pixel = 0xFF000000;
            else
                pixel = tmp;
            tab[i] = pixel;
        }

        source.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Find the closest value of a given one in a given array.
     *
     * @param tab
     * The array of all the possible values.
     * 
     * @param e
     * The value for which we seek the closest value in the array.
     * 
     * @return The closest float value found in the array.
     * 
     * @see Algorithms#cartoonize
     * 
     * @since 3.0
     */
    private static float find_closest_value(float[] tab, float e) {
        float closest = tab[0];
        
        // The current minimal distance between the value of reference and the elements of the array
        float d_min = Math.abs(closest - e);
        
        for (int i = 1; i < tab.length; i++) {
            float d = Math.abs(tab[i] - e);
            // Updates the distance if it finds a closer element
            if (d < d_min) {
                d_min = d;
                closest = tab[i];
            }
        }
        return closest;
    }

    /**
     * Calculates the HSV to RGB conversion with values in the interval [0,1]
     *
     * @param h
     * The hue of the pixel.
     * 
     * @param s
     * The saturation of the pixel.
     * 
     * @param v
     * The value of the pixel
     *
     * @return The red, green and blue associated values in the interval [0,255]
     * 
     * @see Algorithms#cartoonize
     *
     * @since 3.0
     */
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

    /**
     * Calculates the RGB to HSV conversion.
     *
     * @param r
     * The red component of a pixel.
     * 
     * @param g
     * The green component of a pixel.
     * 
     * @param b
     * The blue component of a pixel.
     * 
     * @return The hue, value and saturation in a float array of values within the interval [0,1]
     *
     * @see Algorithms#cartoonize
     *
     * @since 3.0
     */
    private static float[] RGBtoHSV(int r, int g, int b) {
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

    /**
     * Gives to an image a cartoonlike effect by discretisying the HSV values of the pixels
     * and tracing the edges of the image onto itself.
     *
     * @param img
     * The image we work on.
     *
     * @param n
     * The number of values used to discretize the interval [0,1] of the HSV values
     * with the roots of the 2nth Chebychev's polynomial.
     *
     * @see Algorithms#traceEdges
     * @see Algorithms#RGBtoHSV
     * @see Algorithms#HSVtoRGB
     *
     * @since 1.0
     */
    public static void cartoonize(Image img, int n) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());

        // For the discrete space of HSV values we choose the roots of the nth Chebychev's polynomial
        // because of our testing beforehand.

        int j = 0;
        float[] hValues = new float[n];
        float[] sValues = new float[n];
        float[] vValues = new float[n];
        for (int k = 1; k <= 2*n; k++) {
            // Chebyshev's polynomials roots
            float root = (float) Math.cos((2 * k - 1) * Math.PI / (2 * 2*n));
            // they are symmetrical so we only take the positive ones
            if (root > 0) {
                hValues[j] = root;
                sValues[j] = root;
                vValues[j] = root;
                j++;
            }
        }

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];

            int blue = tmp & 0x000000FF;
            int green = (tmp & 0x0000FF00) >> 8;
            int red = (tmp & 0x00FF0000) >> 16;

            float[] hsv = RGBtoHSV(red, green, blue);

            // Discretization of the HSV values
            hsv[0] = Algorithms.find_closest_value(hValues, hsv[0]);
            hsv[1] = Algorithms.find_closest_value(sValues, hsv[1]);
            hsv[2] = Algorithms.find_closest_value(vValues, hsv[2]);

            tab[i] = HSVtoRGB(hsv[0], hsv[1], hsv[2]);
        }

        img.setPixels(tab, 0, 0, w, h);
    }
    
    /**
     * Uses the Hough transformation to detect the lines lying in a binary image by sending the pixels
     * from the (x,y) plane to the (rho,theta) one to transform the problem of finding colinear points
     * to the one of finding concurrent sinusoidal curves.
     * <p> Code written based on the paper "Use of the Hough Transformation To Detect Lines and
     * Curves in Pictures" by Richard O. Duda and Peter E. Hart in January 1972.</p>
     *
     * @param img
     * The image we work on.
     * 
     * @param theta
     * The resolution of the theta's discretization.
     * 
     * @param threshold
     * The threshold above which an intersection point corresponds to a line in the image.
     * 
     * @since 3.0
     */
    public static void hough_transform(Image img, double theta, int threshold) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());

        // The resolution of the rho's discretisation in the Hough space is always 1 (personal
        // choice for simpler usage)
        // The resolution of the theta's discretisation in the Hough space is theta

        // In [0;pi] the normal parameters for a line are unique
        int Ntheta = (int) (180.0 / theta);
        // The number of steps needed to describe the whole Hough space
        int Nrho = (int) Math.floor(Math.sqrt(w * w + h * h));

        // The size of a single step for rho
        double drho = Math.floor(Math.sqrt(w * w + h * h)) / Nrho;
        // The size of a single step for theta
        double dtheta = Math.PI / Ntheta;

        // The accumulator used to count the intersection point
        int[][] acc = new int[Ntheta][Nrho];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = tab[i * w + j];
                int gray = pixel & 0x000000FF;
                if (gray > 200) {
                    for (int i_theta = 0; i_theta < Ntheta; i_theta++) {
                        double ith_theta = i_theta * dtheta;
                        // Parametrization of the line in the (x,y) plane
                        double ith_rho = j * Math.cos(ith_theta) + (h - i) * Math.sin(ith_theta);
                        // We find out the corresponding rho step
                        int j_rho = (int) (ith_rho / drho);
                        // If it fits in our Hough plane we increment all the pixels for which the
                        // discrete sinusoidal curve passes through
                        if (j_rho > 0 && j_rho < Nrho)
                            acc[i_theta][j_rho] += 1;
                    }
                }
            }
        }

        // Now the accumulator is set and we can find the lines in the image in the (rho, theta) plane

        // Will contain the lines in the (x, y) plane after a conversion of the ones from the (rho, theta) plane
        ArrayList<Double[]> lines = new ArrayList<>();

        for (int i_theta = 0; i_theta < Ntheta; i_theta++) {
            for (int j_rho = 0; j_rho < Nrho; j_rho++) {
                if (acc[i_theta][j_rho] > threshold) {
                    double theta0 = i_theta * dtheta;
                    double rho0 = j_rho * drho;
                    // We are now trying to find the corresponding line y = a*x + b of the 
                    // line rho0 = x * cos(theta0) + y * sin(theta0)

                    Double a, b;

                    // The cosine is almost equal to zero (horizontal line)
                    if (Math.abs(theta0 - Math.PI / 2.0) < 0.1) { 
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
        // The number of lines we found in the image
        int n_lines = lines.size();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int on_a_line;
                for (int k = 0; k < n_lines; k++) {
                    // t = {a, b} the coefficients of the line
                    Double[] t = lines.get(k); 
                    // If the line is vertical
                    if (t[0].equals(Double.NaN)) 
                        on_a_line = j - t[1].intValue();
                    else
                        on_a_line = (int) ((h - i) - t[0] * j - t[1]); // Testing if the point is on the line ie if y - ax - b = 0

                    // If a line is detected, draws it in red
                    if (on_a_line == 0) {
                        tab[i * w + j] = 0xFFFF0000;
                    }
                }
            }
        }

        img.setPixels(tab, 0, 0, w, h);
    }


    /* -------------- Algorithms using convolutions ---------------- */

    /**
     * Calculates the convolution with a 3x3 mask that computes the mean of the pixels.
     *
     * @param img
     * The image we work on.
     *
     * @param n
     * The size of the mask will be n x n.
     *
     * @see Algorithms#convolution
     *
     * @since 2.0
     */
    public static void meanFilter (Image img, int n) {
        Algorithms.toGray(img);
        float m = n*n;
        float matrixMean[][] = new float [n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                matrixMean[i][j] = 1f/m;
        Algorithms.convolutionColor(img, matrixMean);
    }

    /**
     * Calculates the convolution with a 3x3 mask that sharpens the edges of an ARGB image.
     *
     * @param img
     * The image we work on.
     *
     * @see Algorithms#convolutionColor
     *
     * @since 4.0
     */
    public static void sharpens (Image img) {
        float matrixSharp[][] = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
        Algorithms.convolutionColor(img, matrixSharp);
    }

    /**
     * Calculates the convolution with a 3x3 mask that sharpens the edges of an ARGB image.
     *
     * @param img
     * The image we work on.
     *
     * @see Algorithms#convolutionColor
     *
     * @since 4.0
     */
    public static void contrastFilter (Image img) {
        float matrixContrast[][] = {{1, -3, 1}, {-3, 9, -3}, {1, -3, 1}};
        Algorithms.convolutionColor(img, matrixContrast);
    }

    /**
     * Calculates the convolution with a gaussian mask of arbitrary size and standard deviation.
     *
     * @param img
     * The image we work on.
     * 
     * @param n
     * The number of rows ans columns of the gaussian mask, it must be odd.
     *
     * @param sigma
     * The standard deviation used to calculate the gaussian.
     * 
     * @see Algorithms#convolution
     *
     * @since 4.0
     */
    public static void gaussianFilter (Image img, int n, double sigma) {
        // size 3 sigma 0.8 are good values for lenna
        // size 5 sigma 1.25 too
        Algorithms.toGray(img);
        float[][] matrixGaussien = new float[n][n];
        int coord = (n-1)/2;
        for (int x = -coord; x <= coord; x++)
            for (int y = -coord; y <= coord; y++)
                matrixGaussien[x+coord][y+coord] = (float) (1/(2*Math.PI*sigma*sigma) * Math.exp(-(x*x+y*y)/(2*sigma*sigma)));
        Algorithms.convolution(img, matrixGaussien);
    }

    /**
     * Calculates the convolution with a 3x3 laplacian mask to detect the edges in an image.
     *
     * @param img
     * The image we work on.
     *
     * @see Algorithms#convolution
     *
     * @since 2.0
     */
    public static void laplacien (Image img) {
        Algorithms.toGray(img);
        float matrixLaplacien[][] = {{-1,-1,-1}, {-1,8,-1}, {-1,-1,-1}};
        Algorithms.convolution(img, matrixLaplacien);
    }

    /**
     * Calculates two convolutions with the two 3x3 Sobel masks and uses the gradient's
     * norm to detect the edges in the image.
     *
     * @param img
     * The image we work on.
     *
     * @see Algorithms#convolution
     *
     * @since 2.0
     */
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
                value = (int) (val/Math.sqrt(2));
                output[i * img.getWidth() + j] = 0xFF000000 | (value << 16) | (value << 8) | value;
            }
        }

        img.setPixels(output, 0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Represents the image as a patchwork of spheres of a given radius.
     *
     * @param img
     * The image we work on.
     *
     * @param radius
     * The radius of the spheres.
     *
     * @see Sphere
     *
     * @since 4.0
     */
    public static void spheres (Image img, int radius) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, w, h);

        ArrayList<Sphere> spheresList = new ArrayList<>();

        // Stocks all the spheres within the image for later
        for (int x = radius; x < h; x += 2*radius)
            for (int y = radius; y < w; y += 2*radius)
                spheresList.add(new Sphere(x, y, radius, tab[x * w + y]));

        // Erases the image
        for (int i = 0; i < size; i++)
            tab[i] = 0xFFFFFFFF;

        for (int i = 0; i < spheresList.size(); i++) {
            Sphere tmp = spheresList.get(i);
            for(int x = Math.max(0, tmp.getCenterX() - radius); x < Math.min(h, tmp.getCenterX() + radius); x++)
                for(int y = Math.max(0, tmp.getCenterY() - radius); y < Math.min(w, tmp.getCenterY() + radius); y++)
                    // Checks if a point is in the sphere and draws it if it is
                    if (Math.sqrt(Math.pow((x - tmp.getCenterX()),2) + Math.pow((y - tmp.getCenterY()),2)) < radius)
                        tab[x * w + y] = tmp.getColor();
        }

        img.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Rotates the image by 90 degrees.
     *
     * @param img
     * The image we work on.
     *
     * @param imView
     * The image view in which the image is displayed.
     *
     * @since 4.0
     */
    public static void rotate (Image img, CustomImageView imView) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int[] newTab = new int[size];

        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                newTab[j * h + (h-1-i)] = tab[i * w + j];

        img.setPixels(newTab, 0, 0, w, h);
        img.setWidth(h);
        img.setHeight(w);
        imView.setCoord(0, 0, h, w);
    }

    /**
     * Calculates two convolutions with the two 3x3 Sobel masks, then calculates the local maximums of the gradient obtained,
     * then uses a double threshold and finally tracks the edges by hysteresis to find the edges in the image.
     *
     * @param img
     * The image we work on.
     *
     * @param lowThresh
     * The lowest threshold used during the double threshold step.
     *
     * @param highThresh
     * The highest threshold used during the double threshold step.
     *
     * @see Algorithms#convolution
     *
     * @since 4.0
     */
    public static void cannyEdgeDetector (Image img, double lowThresh, double highThresh) {
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w*h;

        Algorithms.toGray(img);

        float[][] Gx = {{-1,0,1}, {-2,0,2}, {-1,0,1}};
        float[][] Gy = {{-1,-2,-1}, {0,0,0}, {1,2,1}};

        Image imgGx = img.clone();
        Image imgGy = img.clone();

        Algorithms.convolution(imgGx, Gx);
        Algorithms.convolution(imgGy, Gy);

        int[] imgGxPixels = imgGx.getPixels(0, 0, w, h);
        int[] imgGyPixels = imgGy.getPixels(0, 0, w, h);

        int[] imgGxPixelsNorm = new int[size];
        int[] imgGyPixelsNorm = new int[size];

        // Normalization of the gradients
        for (int i = 0; i < size; i++) {
            int valGx = 0x000000FF & (imgGxPixels[i]);
            int valGy = 0x000000FF & (imgGyPixels[i]);
            double norm = Math.sqrt(valGx * valGx + valGy * valGy);
            imgGxPixelsNorm[i] = (int) (valGx/norm);
            imgGyPixelsNorm[i] = (int) (valGy/norm);
        }

        int[] output = new int[size];
        int maxGray = 0;

        // Calculates the norm of the gradient's image
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int valGx = 0x000000FF & (imgGxPixels[i * w + j]);
                int valGy = 0x000000FF & (imgGyPixels[i * w + j]);

                int value = 0;
                double val = Math.sqrt(valGx * valGx + valGy * valGy);
                value = (int) (val/Math.sqrt(2));
                //value = (int) val;

                if (value > maxGray)
                    maxGray = value;

                output[i * w + j] = 0xFF000000 | (value << 16) | (value << 8) | value;
            }
        }

        // Seeks the local maximums of the gradient
        for (int i = 1; i < h - 1; i++) {
            for (int j = 1; j < w - 1; j++) {
                int valGxNorm = (imgGxPixelsNorm[i * w + j]);
                int valGyNorm = (imgGyPixelsNorm[i * w + j]);
                double angle = Math.atan2(valGyNorm, valGxNorm);

                if (angle < Math.PI/6 && angle > -Math.PI/6) {
                    if (output[i * w + (j+1)] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
                else if (angle < Math.PI/3 && angle > 0) {
                    if (output[(i-1) * w + (j+1)] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
                else if (angle < 2 * Math.PI/3 && angle > 0) {
                    if (output[(i-1) * w + j] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
                else if (angle < 5 * Math.PI/6 && angle > 0) {
                    if (output[(i-1) * w + (j-1)] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
                else if ((angle < Math.PI && angle > 0) || (angle < -5 * Math.PI/6)) {
                    if (output[i * w + (j-1)] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
                else if (angle < -2 * Math.PI/3) {
                    if (output[(i+1) * w + (j-1)] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
                else if (angle < -Math.PI/3) {
                    if (output[(i+1) * w + j] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
                else {
                    if (output[(i+1) * w + (j+1)] > output[i * w + j])
                        output[i * w + j] = 0xFF000000;
                }
            }
        }

        // High threshold
        double highThreshold = highThresh * maxGray;
        double lowThreshold = lowThresh * maxGray;

        // Thresholding
        for (int i = 0; i < size; i++) {
            if ((0x000000FF & output[i]) > highThreshold)
                output[i] = 0xFFFFFFFF;
            else if ((0x000000FF & output[i]) < lowThreshold)
                output[i] = 0xFF000000;
        }

        // Hysteresis
        boolean hasNeighbour = true;
        while (hasNeighbour) {
            hasNeighbour = false;
            for (int i = 1; i < h-1; i++) {
                for (int j = 1; j < w-1; j++) {
                    int gray = 0x000000FF & output[i * w + j];
                    if (gray > lowThreshold && gray < highThreshold)
                        for (int x = -1; x < 2; x++)
                            for (int y = -1; y < 2; y++)
                                if (output[(i + x) * w + (j + y)] == 0xFFFFFFFF) {
                                    output[i * w + j] = 0xFFFFFFFF;
                                    hasNeighbour = true;
                                    break;
                                }
                }
            }
        }

        img.setPixels(output, 0, 0, img.getWidth(), img.getHeight());
    }

    /**
     * Uses the Voronoi's cells to apply a mosaic effect on the image thanks to Voronoi's diagram.
     * This methods provides a tessellation of the plan because :
     * <p>If we call Voronoi's cell associated with the Voronoi's germ p (p in the plan and
     * in the set S of all the Voronoi's cells), then the set : </p>
     * <center> Vor_S(p) = {x in R^2 | Forall q in S, d(x, p) <= d(x, q)}</center>
     * Is such as all the points belong to at least one of these sets, and the ones that belongs to two
     * or more are exactly the frontiers of the polygons of the tessellation.
     *
     * @param img
     * The image we work on.
     *
     * @param n
     * The number of Voronoi's seeds.
     *
     * @see VoronoiSeed
     *
     * @since 4.0
     */
    public static void mosaic (Image img, int n) {
        int w = img.getWidth();
        int h = img.getHeight();
        int [] tab = img.getPixels(0, 0, w, h);

        ArrayList<VoronoiSeed> seeds = new ArrayList<>(0);
        for (int i = 0; i < n; i++) {
            double x = Math.random()*(h-1);
            double y = Math.random()*(w-1);
            seeds.add(new VoronoiSeed(x, y, tab[(int) x * w + (int) y]));
        }

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double dmin = w*w*2;
                for (int k = 0; k < seeds.size(); k++) {
                    double dcurrent = seeds.get(k).distance(i, j);
                    if (dcurrent - dmin < 0) {
                        dmin = dcurrent;
                        tab[i * w + j] = seeds.get(k).getColor();
                    }
                    else if (dcurrent == dmin) {
                        tab[i + w + j] = 0xFF000000;
                    }
                }
            }
        }

        img.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Translates a texture into an array of ratios linked to the texture's pixels' values.
     *
     * @param texture
     * The texture to tanslate.
     *
     * @return
     * The array of values that have been smoothed by a sigmoid.
     *
     * @see Algorithms#applyTexture
     *
     * @since 4.0
     */
    private static double [] fetchTexture (Image texture) {
        int w = texture.getWidth();
        int h = texture.getHeight();
        int [] tab = texture.getPixels(0, 0, w, h);
        double [] light = new double[w*h];

        for (int i = 0; i < w*h; i++) {
            int tmp = tab[i];
            int blue = 0x000000FF & tmp;
            int green = (0x0000FF00 & tmp) >> 8;
            int red = (0x00FF0000 & tmp) >> 16;

            float value = red > green ? (red > blue ? red : blue) : (green > blue ? green : blue);
            value /= 255f;

            // The sigmoid we use to smooth the values.
            light[i] = 1 / (1 + Math.exp(-2 * value));
        }

        return light;
    }

    /**
     * Applies the given texture onto the image.
     *
     * @param texture
     * The texture to apply (can be bricks, wood, ...)
     *
     * @param source
     * The image we work on.
     *
     * @see Algorithms#fetchTexture
     *
     * @since 4.0
     */
    public static void applyTexture (Image source, Image texture) {
        int w = source.getWidth();
        int h = source.getHeight();
        int [] tab = source.getPixels(0, 0, w, h);

        int wTex = texture.getWidth();
        int hTex = texture.getHeight();

        double [] tabTexture = fetchTexture(texture);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int tmp = tab[i * w + j];
                double ratio = tabTexture[(i % hTex) * wTex + (j % wTex)];
                int blue = (int) ((tmp & 0x000000FF) * ratio);
                int green = (int) (((tmp & 0x0000FF00) >> 8) * ratio);
                int red = (int) (((tmp & 0x00FF0000) >> 16) * ratio);
                int alpha = (tmp & 0xFF000000);
                tab[i * w + j] = alpha | (red << 16) | (green << 8) | blue;
            }
        }
        source.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Applies the given texture onto the image.
     *
     * @param img
     * The image we work on.
     *
     * @param radius
     * The radius of the brush.
     *
     * @since 4.0
     */
    public static void brush (Image img, int radius) {
        int w = img.getWidth();
        int h = img.getHeight();
        int [] tab = img.getPixels(0, 0, w, h);

        int Nx = w/radius;
        int Ny = h/radius;

        // For each square of edge radius
        for (int i = 0; i < Ny; i++) {
            for (int j = 0; j < Nx; j++) {
                // For each pixel of this square
                for (int x = 0; x < radius; x++) {
                    for (int y = 0; y < radius; y++) {
                        // We detect if we are above or under the unit circle, and we choose the most appropriate color.
                        double relativeX = (x % radius) / radius;
                        double relativeY = 1 - (y % radius) / radius;
                        int color;
                        try {
                            if (relativeY > Math.sqrt(1 - (1 - relativeX) * (1 - relativeX)))
                                color = tab[(i * radius + 1) * w + (j * radius - 1)];
                            else
                                color = tab[(i * radius + radius) * w + (j * radius + radius)];
                            tab[(i * radius + x) * w + (j * radius + y)] = color;
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }
        img.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Creates a randomly generated labyrinth pattern whose density depends on the pixels' lightness.
     *
     * @param img
     * The image we work on.
     *
     * @param max
     * The number of possible spacings.
     *
     * @param ratio
     * A higher ratio allows for more details. Belongs to [0,255]
     *
     * @since 4.0
     */
    public static void labyrinth (Image img, int max, float ratio) {
        int w = img.getWidth();
        int h = img.getHeight();
        int [] tab = img.getPixels(0, 0, w, h);

        // For each square of edge radius
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int tmp = tab[i * w + j];
                int blue = (tmp & 0x000000FF);
                int green = (tmp & 0x0000FF00) >> 8;
                int red = (tmp & 0x00FF0000) >> 16;
                int alpha = (tmp & 0xFF000000);

                float value = red > green ? (red > blue ? red : blue) : (green > blue ? green : blue);
                int k = value/ratio == 0 ? 0 : (int) Math.ceil((value/ratio)/(1/(1.0 * max)));
                double coin = Math.random();
                int winner = coin < 0.5 ? i : j;

                if (!(k !=0 && (k < max) && (winner % k == 0)))
                    tab[i * w + j] = alpha | 0x00FFFFFF;
            }
        }
        img.setPixels(tab, 0, 0, w, h);
    }

    /**
     * Duplicates the image by permuting pixels depending on their parity. Cycles back to the original
     * image after repetitive usages (a divisor of the lcm of h*w.
     *
     * @param img
     * The image we work on.
     *
     * @since 4.0
     */
    public static void duplicate (Image img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int [] tab = img.getPixels(0, 0, w, h);
        int [] newTab = new int[w*h];

        int dw = (int) Math.floor(w/2.0);
        int dh = (int) Math.floor(h/2.0);

        // For each square of edge radius
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int tmp = tab[i * w + j];

                if (i % 2 == 0) {
                    if (j % 2 == 0)
                        newTab[i / 2 * w + j / 2] = tab[i * w + j];
                    else
                        newTab[i / 2 * w + (j - 1) / 2 + dw] = tab[i * w + j];
                } else {
                    if (j % 2 == 0)
                        newTab[((i - 1) / 2 + dh) * w + j / 2] = tab[i * w + j];
                    else
                        newTab[((i - 1) / 2 + dh) * w + (j - 1) / 2 + dw] = tab[i * w + j];
                }
            }
        }
        img.setPixels(newTab, 0, 0, w, h);
    }

    /**
     * Gives a painting effect to the image by giving to each pixel the color of itself or one of its
     * neighbours randomly.
     *
     * @param img
     * The image we work on.
     *
     * @param n
     * The number of times we apply the effect.
     *
     * @since 4.0
     */
    public static void painting (Image img, int n) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, w, h);
        int[] newTab = new int[w * h];

        int [] coord = {-1, 0, 1};

        for (int k = 0; k < n; k++) {
            if (k != 0)
                for (int x = 1; x < h-1; x++)
                    for (int y = 1; y < w-1; y++)
                        tab[x * w + y] = newTab[x * w + y];

            for (int i = 1; i < h-1; i++) {
                for (int j = 1; j < w-1; j++) {
                    int coin1 = (int) (Math.random()*3);
                    int coin2 = (int) (Math.random()*3);

                    newTab[i * w + j] = tab[(i + coord[coin1]) * w + (j + coord[coin2])];
                }
            }
        }

        img.setPixels(newTab, 0, 0, w, h);
    }
}