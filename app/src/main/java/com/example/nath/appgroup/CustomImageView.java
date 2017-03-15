package com.example.nath.appgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class CustomImageView extends ImageView {
    final int MODE_DRAG = 1; // Constant for multitouch usage
    final int MODE_ZOOM = 2; // Constant for multitouch usage
    private Image img; // The image we display
    private Image imgTmp; // The image we calculate on
    private Image imgBackup; // The image use for backups and resets
    private int left; // Limits of the image displayed
    private int top;
    private int right;
    private int bottom;
    private int mode; // mode for multitouch usage can be MODE_DRAG or MODE_ZOOM
    private float previousX; // X Location at the first multitouch call
    private float previousY; // Y Location at the first multitouch call
    private double previousDistance; // Same for pinches when using multitouch

    public CustomImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setImage(Image img, boolean freshImage) { // Sets the image displayed
        this.img = img.clone();

        if (freshImage) {
            imgTmp = null;
            this.imgBackup = img.clone(); // We change the backup accordingly
            left = 0; // We display all the image
            top = 0;
            right = img.getWidth();
            bottom = img.getHeight();
            mode = MODE_DRAG; // The default mode is drag
            previousX = 0f; // A few more initializations
            previousY = 0f;
            previousDistance = 0.0;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) { // Gestion of the multitouch


        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mode = MODE_DRAG;
                previousX = event.getX();
                previousY = event.getY();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = MODE_ZOOM;
                previousDistance = Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) +
                        (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));
                break;

            case MotionEvent.ACTION_UP:
                if (mode == MODE_ZOOM)
                    break;

                int dx = (int)(previousX - event.getX());
                int dy = (int)(previousY - event.getY());

                left += dx;
                right += dx;
                top += dy;
                bottom += dy;

                if (left < 0) {
                    right += -left;
                    left = 0;
                }

                if (right > img.getWidth()) {
                    left -= right - img.getWidth();
                    right = img.getWidth();
                }

                if (top < 0) {
                    bottom += -top;
                    top = 0;
                }

                if (bottom > img.getHeight()) {
                    top -= bottom - img.getHeight();
                    bottom = img.getHeight();
                }

                drawImage();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                double distance = Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) +
                        (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));

                float ratio = (float)(distance / previousDistance);
                float zoom = Math.max(5, ratio);
                zoom -= 1;
                zoom /= 5;
                if (ratio < 1)
                    zoom *= -1f;

                left += zoom * img.getWidth() / 5;
                top += zoom * img.getHeight() / 5;
                right -= zoom * img.getWidth() / 5;
                bottom -= zoom * img.getHeight() / 5;

                left = Math.max(0, left);
                top = Math.max(0, top);
                right = Math.min(img.getWidth(), right);
                bottom = Math.min(img.getHeight(), bottom);

                right = Math.max(left + 1, right);
                bottom = Math.max(top + 1, bottom);
                left = Math.min(right - 1, left);
                top = Math.min(bottom - 1, top);

                drawImage();
                break;
        }

        return true;

    }
}
