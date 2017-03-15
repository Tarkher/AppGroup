package com.example.nath.appgroup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Image implements Cloneable {
    private int[] pixels;
    private int height;
    private int width;

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
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
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
