package com.example.nath.appgroup;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;


public class Algorithms {
    public static void toGray(Image img) {//Transforms a bitmap image into a gray level one using its pixels' array
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];//tmp is the ith argb pixel
            int blue = (int) ((tmp & 0x000000FF) * 0.11);//Gets the blue component of the pixel by filtering the Color integer and weights it
            int green = (int) (((tmp & 0x0000FF00) >> 8) * 0.59);//same for the green component
            int red = (int) (((tmp & 0x00FF0000) >> 16) * 0.3);//same for the red component
            int color_custom = blue + green + red;//The pixel's gray level
            int final_pix = 0xFF000000 | (color_custom << 16) | (color_custom << 8) | color_custom;//Makes an integer matching the Color's formatting
            tab[i] = final_pix;//Stores the pixel's gray level to find its new value in the LUT later
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());//Replaces the bitmap's pixels array by the gray one
    }

    public static void luminosity(Image img, int lum) {//Translates the histogram of an image to increase/decrease the luminosity
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, w, h);

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];//tmp is the ith argb pixel
            int blue = (tmp & 0x000000FF) + lum;//Gets the blue component of the pixel and translates it with a factor lum
            int green = ((tmp & 0x0000FF00) >> 8) + lum;//same for the green component
            int red = ((tmp & 0x00FF0000) >> 16) + lum;//same for the red component
            int alpha = ((tmp & 0xFF000000) >> 24);
            blue = blue > 255 ? 255 : blue;
            red = red > 255 ? 255 : red;
            green = green > 255 ? 255 : green;
            blue = blue < 0 ? 0 : blue;
            red = red < 0 ? 0 : red;
            green = green < 0 ? 0 : green;
            int final_pix = (alpha << 24) | (red << 16) | (green << 8) | blue;//Makes an integer matching the Color's formatting
            tab[i] = final_pix;//Stores the pixel's gray level to find its new value in the LUT later
        }
        img.setPixels(tab, 0, 0, w, h);//Replaces the bitmap's pixels array by the gray one
    }

    private static int[] lookUpTable(int a, int b, int c, int d) {//Generates the Look Up Table for linear histogram extension from [min,max] to [c,d]
        int[] tab = new int[256];
        for (int gray_lvl = a; gray_lvl < b + 1; gray_lvl++) {
            tab[gray_lvl] = (int) (((d - c) * (((gray_lvl - a) / (double) (b - a)))) + c);//bijection from [min,max] to [c,d]
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

    private static int[] histo(int[] tab) {//grayhistogram
        int n = tab.length;
        int[] h = new int[256];

        for (int tmp : tab) {
            h[tmp] += 1;
        }
        return h;
    }

    private static int[] histo_c(int[] tab1) {//cumulative histogram in RGB
        int[] tab = histo(tab1);
        int[] h = new int[256];

        h[0] = tab[0];

        for (int i = 1; i < 256; i++) {
            h[i] = h[i - 1] + tab[i];
        }

        return h;
    }

    public static void dynamicExtensionColor(Image img) {//Performs the linear dynamic extension algorithm on a RGB image
        int w = img.getWidth();
        int h = img.getHeight();
        int size = w * h;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int[] val = new int[size];

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];//tmp is the ith argb pixel
            int blue = tmp & 0x000000FF;//Gets the blue component of the pixel by filtering the Color integer
            int green = (tmp & 0x0000FF00) >> 8;//same for the green component
            int red = (tmp & 0x00FF0000) >> 16;//same for the red component

            val[i] = blue > red ? (blue > green ? blue : green) : (red > green ? red : green);//the max of the R/G/B values (value field in HSV)
        }

        int min = 255;
        int max = 0;

        for (int i = 0; i < size; i++) {
            max = val[i] > max ? val[i] : max;
            min = val[i] < min ? val[i] : min;
        }

        int[] LUT = lookUpTable(min, max, 0, 255);//Generates the appropriate look up table for the red component

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;//Gets the blue component of the pixel by filtering the Color integer
            int green = (tmp & 0x0000FF00) >> 8;//same for the green component
            int red = (tmp & 0x00FF0000) >> 16;//same for the red component

            float[] hsv = new float[3];
            Color.RGBToHSV(red, green, blue, hsv);

            float new_value = LUT[val[i]] / 255.0f;

            hsv[2] = new_value;
            tmp = Color.HSVToColor(hsv);

            tab[i] = tmp;//Replaces the pixel in the array
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());//Replaces the bitmap's pixels array by the gray one
    }

    public static void contrastEqualization(Image img, int goal) {//Equalize the values of the pixels with the cumulative histogram for a better contrast result on dark pictures
        int w = img.getWidth();
        int hei = img.getHeight();
        int size = w * hei;
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int[] val = new int[size];

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;//Gets the blue component of the pixel by filtering the Color integer
            int green = (tmp & 0x0000FF00) >> 8;//same for the green component
            int red = (tmp & 0x00FF0000) >> 16;//same for the red component

            val[i] = blue > red ? (blue > green ? blue : green) : (red > green ? red : green);//the max of the R/G/B values (value field in HSV)
        }

        int[] h = histo_c(val);
        int min = size;

        for (int i = 0; i < 256; i++) {//We get the min and max of pixels below a i level of value
            min = h[i] < min ? h[i] : min;
        }


        //Send the values of the cumulative histogram in [0,255], max is always size by definition
        int[] LUT_value = norm_h(min, size, 0, goal, h);

        for (int i = 0; i < size; i++) {
            int tmp = tab[i];
            int blue = tmp & 0x000000FF;//Gets the blue component of the pixel by filtering the Color integer
            int green = (tmp & 0x0000FF00) >> 8;//same for the green component
            int red = (tmp & 0x00FF0000) >> 16;//same for the red component
            float[] hsv = new float[3];
            Color.RGBToHSV(red, green, blue, hsv);
            float new_value = LUT_value[val[i]] / 255.0f;
            hsv[2] = new_value;
            tmp = Color.HSVToColor(hsv);
            tab[i] = tmp;//Replaces the pixel in the array
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());//Replaces the bitmap's pixels array by the gray one
    }

    public static void colorize(Image img, double hue) {//Modifies the bitmap's hue with a self made hsv to rgb translator
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        img.getPixels(0, 0, img.getWidth(), img.getHeight());

        for (int i = 0; i < w * h; i++) {
            int tmp = tab[i];
            /* RGB to HSV conversion (the hue is already given so we are not calculating it) */
            double blue = (tmp & 0x000000FF) / 255.0;
            double green = ((tmp & 0x0000FF00) >> 8) / 255.0;
            double red = ((tmp & 0x00FF0000) >> 16) / 255.0;

            double color_max = blue >= green ? (blue >= red ? blue : red) : (green >= red ? green : red);//max of the R/G/B values
            double color_min = blue <= green ? (blue <= red ? blue : red) : (green <= red ? green : red);//min of the R/G/B values
            double delta = color_max - color_min;

            double saturation = color_max == 0.0 ? 0.0 : delta / color_max;
            double value = color_max;
            /* -------------------------------------------------- */

            /* HSV to RGB conversion */
            double c = value * saturation;
            double x = c * (1 - Math.abs((hue / 60.0) % 2 - 1));
            double m = value - c;

            if (0 <= hue & hue < 60) {//Checks the angle of the hue on the R/G/B color circle
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

            int final_pix = 0xFF000000 | (red_new << 16) | (green_new << 8) | blue_new;//Formats the new pixel
            tab[i] = final_pix;//Adds the new pixel to the array
        }
        img.setPixels(tab, 0, 0, img.getWidth(), img.getHeight());//Replaces the pixel array by the new one
    }

    public static void convolution(Image img, float[][] matrix) {
        int height = img.getHeight();
        int width = img.getWidth();
        int pixels[] = img.getPixels(0, 0, img.getWidth(), img.getHeight());
        int output[] = new int[width * height];

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

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int value = 0;

                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix.length; j++) {
                        int yPrime = floorMod(y + i - (matrix.length - 1) / 2, height);
                        int xPrime = floorMod(x + j - (matrix.length - 1) / 2, width);
                        value += (0x000000FF & pixels[yPrime * width + xPrime]) * matrix[i][j];
                    }
                }

                if (value > 255 || value < 0) {
                    value = (int) ((value - min_value)/(max_value - min_value)) * 255;
                }
                output[y * width + x] = 0xFF000000 | (value << 16) | (value << 8) | value;
            }
        }

        img.setPixels(output, 0, 0, width, height);
    }

    private static int floorMod(int a, int m) {
        int b = a % m;
        if (b < 0)
            b += m;
        return b;
    }

    //TODO: ZOOM------------------------------------------------------------------------------------

    public void zoomAliasing(Bitmap img, ImageView image, float zoom) {//(x,y) is top left corner's pixel for the zoomed image
        int w = img.getWidth();
        int h = img.getHeight();
        int[] tab = new int[h * w];
        img.getPixels(tab, 0, w, 0, 0, w, h);

        int w_new = (int) (w * zoom);
        int h_new = (int) (h * zoom);
        int new_size = w_new * h_new;
        int[] tab_new = new int[new_size];

        for (int k = 0; k < new_size; ++k) {//j is the number of the line
            int i = k % w_new;
            int j = k / w_new;
            tab_new[j * w_new + i] = tab[(int) (j / zoom) * w + (int) (i / zoom)];//Managing the scales in the new and old image
        }

        Bitmap tmp = Bitmap.createBitmap(w_new, h_new, Bitmap.Config.RGB_565);//Creating a new well sized bitmap
        tmp.setPixels(tab_new, 0, w_new, 0, 0, w_new, h_new);//with the zoomed pixels
        image.setImageBitmap(tmp);//to put in the image view
    }

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
                    try {
                        int corner_color = tab[(int) (corner_j) * w + (int) (corner_i)];
                        float area = Math.abs(corner_i - center_i) * Math.abs(corner_j - center_j);
                        float proportion = (area == 0) ? 0 : area;
                        blue += (corner_color & 0x000000FF) * proportion;
                        green += ((corner_color & 0x0000FF00) >> 8) * proportion;
                        red += ((corner_color & 0x00FF0000) >> 16) * proportion;
                    } catch (Exception e) {
                        System.err.print(e.getMessage());
                    }
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