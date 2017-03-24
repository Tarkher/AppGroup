package com.example.nath.appgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class CustomImageView extends ImageView {
    private Image img; // The image we display
    private Image imgTmp; // The image we calculate on
    private Image imgBackup; // The image use for backups and resets
    private int left; // Limits of the image displayed
    private int top;
    private int right;
    private int bottom;

    public CustomImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setImage(Image img, boolean freshImage) { // Sets the image displayed
        this.img = img.clone();

        if (freshImage) {
            imgTmp = img.clone();
            this.imgBackup = img.clone(); // We change the backup accordingly
            left = 0; // We display all the image
            top = 0;
            right = img.getWidth();
            bottom = img.getHeight();
        }

        drawImage(); // Display the new image
    }

    public Image getImage() {
        return img;
    } // Returns the current image

    public Image getImageBackup() {
        return imgBackup.clone();
    } // Makes a backup

    public void saveImageTemporary() {
        imgTmp = img.clone();
    } // Used to calculate on the original image when using a seekbar

    public Image getImageTmp() {
        return imgTmp.clone();
    }

    public void drawImage() { // Display the image in accordance with the distances
        int heightTmp = bottom - top;
        int widthTmp = right - left;
        int[] pixelsBitmap = img.getPixels(left, top, right, bottom);
        Bitmap bitmap = Bitmap.createBitmap(pixelsBitmap, widthTmp, heightTmp, Bitmap.Config.ARGB_8888);
        setImageBitmap(bitmap);
    }

    public void setCoord (int left, int top, int right, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }
}