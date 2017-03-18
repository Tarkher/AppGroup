package com.example.nath.appgroup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * <b>Image contains our implementation of all the basic functionalities that
 * can be useful to work on images</b>
 * <p>
 * Furthermore, this class only contains public methods that are analog to the
 * the most used on of the Bitmap class.
 * </p>
 * <p>
 * An image is defined by those informations :
 * <ul>
 * <li>An array of pixels, likely to be modified entirely</li>
 * <li>The number of lines (the height) of the image</li>
 * <li>The number of columns (the width) of the image</li>
 * </ul>
 * </p>
 *
 * @author Nathan Castets
 * @version 2.0
 */
public class Image implements Cloneable {
    /**
    * The one-dimensional array containing the values of the image's pixels.
    * This array can be changed.
    * 
    * @see Image#Image(int[], int, int)
    * @see Image#setPixels
    * @see Image#getPixels
    */
    private int[] pixels;
    
     /**
    * The number of rows in the image, it can be used to identify a given pixel more easily.
    * 
    * @see Image#Image(int[], int, int)
    * @see Image#getHeight
    */
    private int height;
    
    /**
    * The number of columns in the image, it can be used to identify a given pixel more easily.
    * 
    * @see Image#Image(int[], int, int)
    * @see Image#getWidth
    */
    private int width;

    /**
     * Image constructor.
     * <p>
     * When an Image is created, the array pixels is created with a size width*height then
     * its values are the one of the array pixels.
     * </p>
     * 
     * @param pixels
     * The one-dimensional array the pixels are stored into.
     * 
     * @param height
     * The height of the image.
     *
     * @param width
     * The width of the image.
     * 
     * @see Image#pixels
     * @see Image#height
     * @see Image#width
     */
    public Image(int[] pixels, int height, int width) {
        this.pixels = new int[height * width];

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                this.pixels[i * width + j] = pixels[i * width + j];
            }
        }

        this.height = height;
        this.width = width;
    }

    public Image clone() {
        Image img = null;

        try {
            img = (Image)super.clone();
            int[] newPixels = new int[height * width];

            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    newPixels[i * width + j] = pixels[i * width + j];
                }
            }

            img.pixels = newPixels;
            img.height = this.height;
            img.width = this.width;
        }
        catch (Exception e) {
            Log.wtf("ERROR", "can't clone image");
        }

        return img;
    }

    public Bitmap getBitmap() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixels(int left, int top, int right, int bottom) {
        int heightTmp = bottom - top;
        int widthTmp = right - left;
        int[] output = new int[heightTmp * widthTmp];

        for (int i = 0; i < heightTmp; ++i) {
            for (int j = 0; j < widthTmp; ++j) {
                output[i * widthTmp + j] = pixels[(top + i) * width + (left + j)];
            }
        }

        return output;
    }

    public void setPixels(int[] pixels, int left, int top, int right, int bottom) {
        int heightTmp = bottom - top;
        int widthTmp = right - left;

        for (int i = 0; i < heightTmp; ++i) {
            for (int j = 0; j < widthTmp; ++j) {
                this.pixels[i * widthTmp + j] = pixels[(top + i) * widthTmp + j];
            }
        }
    }
}
